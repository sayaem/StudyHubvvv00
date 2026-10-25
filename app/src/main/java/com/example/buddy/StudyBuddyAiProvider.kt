package com.example.buddy

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * Interface defining the AI Provider for Study Buddy.
 * Allows switching between Gemini REST, backend proxy, or fake test providers without UI changes.
 */
interface StudyBuddyAiProvider {
    suspend fun generateReply(request: BuddyGenerationRequest): BuddyAiResult
}

/**
 * REST API client that interacts directly with Google's Gemini API endpoints.
 * Development-only implementation as guided by Gemini API specification.
 */
class GeminiRestAiProvider(
    private val apiKeyProvider: () -> String = {
        try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
    },
    private val modelName: String = "gemini-2.5-flash",
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) : StudyBuddyAiProvider {

    override suspend fun generateReply(request: BuddyGenerationRequest): BuddyAiResult = withContext(Dispatchers.IO) {
        val apiKey = apiKeyProvider().trim()
        if (apiKey.isEmpty() || apiKey.equals("your_gemini_api_key_here", ignoreCase = true)) {
            return@withContext BuddyAiResult.Error(
                errorMessage = "Gemini API key is missing or not configured. Please add your GEMINI_API_KEY in the Secrets panel.",
                canRetry = false
            )
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
        val systemInstruction = StudyBuddyContextEngine.buildSystemInstruction(request.contextEnvelope)

        val requestPayload = JSONObject().apply {
            // System instructions
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstruction)
                    })
                })
            })

            // Conversation turns
            val contentsArray = JSONArray()
            // Recent turns
            val recentTurns = request.recentTurns.takeLast(6)
            for (turn in recentTurns) {
                val role = if (turn.first.equals("USER", ignoreCase = true)) "user" else "model"
                contentsArray.put(JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", turn.second)
                        })
                    })
                })
            }

            // Current prompt
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", request.userPrompt)
                    })
                })
            })

            put("contents", contentsArray)

            // Generation config
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("topP", 0.95)
                put("topK", 40)
            })
        }

        val body = requestPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val httpRequest = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(httpRequest).execute().use { response ->
                val responseBody = response.body?.string() ?: ""
                val statusCode = response.code

                if (!response.isSuccessful) {
                    return@withContext when (statusCode) {
                        429 -> BuddyAiResult.Error(
                            errorMessage = "Gemini API rate limit or quota reached. Please wait a moment before asking again.",
                            isQuotaExhausted = true,
                            canRetry = true
                        )
                        400, 401, 403 -> BuddyAiResult.Error(
                            errorMessage = "Authentication failed (HTTP $statusCode). Please check your Gemini API key in the Secrets panel.",
                            canRetry = false
                        )
                        503, 500 -> BuddyAiResult.Error(
                            errorMessage = "Gemini service is temporarily unavailable (HTTP $statusCode). Please retry in a few seconds.",
                            canRetry = true
                        )
                        else -> BuddyAiResult.Error(
                            errorMessage = "Request failed with HTTP status $statusCode. Please try again.",
                            canRetry = true
                        )
                    }
                }

                if (responseBody.isBlank()) {
                    return@withContext BuddyAiResult.Error(
                        errorMessage = "Received an empty response from the AI provider. Please retry.",
                        canRetry = true
                    )
                }

                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    val promptFeedback = jsonResponse.optJSONObject("promptFeedback")
                    val blockReason = promptFeedback?.optString("blockReason")
                    return@withContext if (!blockReason.isNullOrBlank()) {
                        BuddyAiResult.Error(
                            errorMessage = "Response was restricted due to safety guidelines ($blockReason). Please rephrase your query.",
                            canRetry = false
                        )
                    } else {
                        BuddyAiResult.Error(
                            errorMessage = "No response candidates returned. Please try asking in a different way.",
                            canRetry = true
                        )
                    }
                }

                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                val responseText = parts?.optJSONObject(0)?.optString("text")

                if (responseText.isNullOrBlank()) {
                    return@withContext BuddyAiResult.Error(
                        errorMessage = "Could not parse response content from AI provider.",
                        canRetry = true
                    )
                }

                val suggestedActions = StudyBuddyContextEngine.suggestActions(
                    intent = request.contextEnvelope.queryIntent,
                    facts = request.contextEnvelope.verifiedFacts
                )

                BuddyAiResult.Success(
                    responseText = responseText.trim(),
                    suggestedActions = suggestedActions
                )
            }
        } catch (e: SocketTimeoutException) {
            BuddyAiResult.Error(
                errorMessage = "Request timed out while waiting for Study Buddy. Check your connection and retry.",
                isNetworkError = true,
                canRetry = true
            )
        } catch (e: UnknownHostException) {
            BuddyAiResult.Error(
                errorMessage = "Unable to connect to Gemini servers. Please check your internet connection.",
                isNetworkError = true,
                canRetry = true
            )
        } catch (e: IOException) {
            BuddyAiResult.Error(
                errorMessage = "Network communication error: ${e.message ?: "Unknown I/O error"}",
                isNetworkError = true,
                canRetry = true
            )
        } catch (e: Exception) {
            BuddyAiResult.Error(
                errorMessage = "An unexpected error occurred: ${e.message ?: "Unknown error"}",
                canRetry = true
            )
        }
    }
}

/**
 * Deterministic Fake Provider for tests and offline development.
 */
class FakeStudyBuddyAiProvider(
    var simulatedResult: BuddyAiResult? = null
) : StudyBuddyAiProvider {

    override suspend fun generateReply(request: BuddyGenerationRequest): BuddyAiResult {
        simulatedResult?.let { return it }

        val facts = request.contextEnvelope.verifiedFacts
        val intent = request.contextEnvelope.queryIntent

        val reply = when (intent) {
            BuddyQueryIntent.PLANNING -> {
                "Here is your study plan based on your StudyHub tasks: You have ${facts.todayPlannedTasksCount} tasks planned for today. Start with the most challenging task first, followed by a focused 25-minute Pomodoro session!"
            }
            BuddyQueryIntent.REVISION -> {
                if (facts.dueRevisionsCount > 0) {
                    "You have ${facts.dueRevisionsCount} revision items due today across: ${facts.dueRevisionSubjects.joinToString(", ")}. I recommend tackling them with active recall!"
                } else {
                    "Great job! You have 0 pending revisions due right now. Keep up the consistent pace!"
                }
            }
            BuddyQueryIntent.EXAM_PREPARATION -> {
                if (facts.nextExamTarget != null) {
                    "Your upcoming target is ${facts.nextExamTarget}. Let's make sure high-weightage topics are well practiced."
                } else {
                    "You do not have any upcoming exam date recorded yet in Admission/Calendar. Would you like to set one?"
                }
            }
            else -> {
                "Hello ${facts.studentName ?: "there"}! I'm your Study Buddy. You have completed ${facts.completedChaptersCount}/${facts.totalChaptersCount} chapters. How can I help you study today?"
            }
        }

        return BuddyAiResult.Success(
            responseText = reply,
            suggestedActions = StudyBuddyContextEngine.suggestActions(intent, facts)
        )
    }
}
