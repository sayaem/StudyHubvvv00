package com.example.buddy

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Context Engine that inspects verified local facts, classifies user intent,
 * formats grounded system instructions, and suggests safe in-app actions.
 */
object StudyBuddyContextEngine {

    /**
     * Classifies user intent from prompt keywords.
     */
    fun classifyIntent(prompt: String): BuddyQueryIntent {
        val lower = prompt.lowercase(Locale.ROOT)
        return when {
            lower.contains("revis") || lower.contains("recall") || lower.contains("spaced repetition") || lower.contains("review") -> {
                BuddyQueryIntent.REVISION
            }
            lower.contains("plan") || lower.contains("schedule") || lower.contains("today") || lower.contains("routine") || lower.contains("missed") -> {
                BuddyQueryIntent.PLANNING
            }
            lower.contains("exam") || lower.contains("admission") || lower.contains("test") || lower.contains("deadline") || lower.contains("buet") || lower.contains("medical") || lower.contains("varsity") -> {
                BuddyQueryIntent.EXAM_PREPARATION
            }
            lower.contains("focus") || lower.contains("pomodoro") || lower.contains("distract") || lower.contains("concentrat") || lower.contains("timer") -> {
                BuddyQueryIntent.FOCUS_HABITS
            }
            lower.contains("stat") || lower.contains("progress") || lower.contains("hour") || lower.contains("time") || lower.contains("score") || lower.contains("analytics") -> {
                BuddyQueryIntent.STATS_PROGRESS
            }
            lower.contains("chapter") || lower.contains("topic") || lower.contains("syllabus") || lower.contains("subject") || lower.contains("physics") || lower.contains("chemistry") || lower.contains("math") || lower.contains("biology") -> {
                BuddyQueryIntent.TOPIC_SYLLABUS
            }
            else -> BuddyQueryIntent.GENERAL
        }
    }

    /**
     * Builds the system instruction prompt strictly grounded in verified facts.
     */
    fun buildSystemInstruction(envelope: BuddyContextEnvelope): String {
        val facts = envelope.verifiedFacts
        val sb = StringBuilder()

        sb.appendLine("You are Study Buddy, a warm, pragmatic, encouraging, and deeply context-aware study companion inside the StudyHub Android application.")
        sb.appendLine("You assist Bangladeshi and international HSC, SSC, and university admission students.")
        sb.appendLine()
        sb.appendLine("=== LANGUAGE GUIDELINES ===")
        sb.appendLine("Match the student's language naturally:")
        sb.appendLine("- If the student writes in Bangla or Banglish, reply warmly and naturally in Bangla / Banglish.")
        sb.appendLine("- If the student writes in English, reply in clean, accessible English.")
        sb.appendLine()
        sb.appendLine("=== TRUTHFULNESS & GROUNDING RULES (MANDATORY) ===")
        sb.appendLine("1. Only use facts explicitly verified in the 'VERIFIED STUDENT CONTEXT' section below.")
        sb.appendLine("2. Never invent or hallucinate exams, grades, chapters, study hours, streaks, or deadlines.")
        sb.appendLine("3. If a fact is unrecorded or zero, truthfully acknowledge it (e.g., 'You currently have no tasks scheduled for today').")
        sb.appendLine("4. Never guilt or shame the student for missed sessions or zero hours; offer calm, realistic, actionable recovery steps.")
        sb.appendLine("5. Do not pretend you have read textbooks or private notes unless explicitly quoted.")
        sb.appendLine("6. Keep replies structured with clear bullet points and actionable study advice. Avoid huge walls of text.")
        sb.appendLine()
        sb.appendLine("=== CURRENT TIME REFERENCE ===")
        sb.appendLine("Current Date & Time: ${envelope.timeReference}")
        sb.appendLine()
        sb.appendLine("=== VERIFIED STUDENT CONTEXT (LOCAL APP DATA) ===")
        sb.appendLine("- Student Name: ${facts.studentName ?: "Student"}")
        sb.appendLine("- Academic Level: ${facts.academicLevel ?: "Not specified"}")
        sb.appendLine("- Target Admission Track: ${facts.admissionTrack ?: "General"}")
        sb.appendLine("- Daily Goal: ${facts.dailyGoalHours?.let { "$it hours/day" } ?: "Not set"}")
        sb.appendLine("- Preferred Study Time: ${facts.preferredStudyTime ?: "Flexible"}")
        sb.appendLine("- Syllabus Status: ${facts.completedChaptersCount}/${facts.totalChaptersCount} chapters completed across ${facts.totalSubjectsCount} subjects (${facts.incompleteChaptersCount} remaining).")
        sb.appendLine("- Due Revisions Today: ${facts.dueRevisionsCount} items due.")
        if (facts.dueRevisionSubjects.isNotEmpty()) {
            sb.appendLine("  Subjects needing revision: ${facts.dueRevisionSubjects.joinToString(", ")}")
        }
        sb.appendLine("- Today's Recorded Study Time: ${facts.todayStudyMinutesRecorded} minutes.")
        sb.appendLine("- Total Study Time Logged: ${"%.1f".format(Locale.US, facts.totalStudyHoursRecorded)} hours.")
        sb.appendLine("- Next Recorded Exam / Target: ${facts.nextExamTarget ?: "No upcoming exam date recorded."}")
        sb.appendLine("- Today's Scheduled Tasks (${facts.todayPlannedTasksCount}):")
        if (facts.todayPlannedTasksSummary.isEmpty()) {
            sb.appendLine("  (No tasks scheduled for today)")
        } else {
            facts.todayPlannedTasksSummary.forEach { task ->
                sb.appendLine("  • $task")
            }
        }
        if (!facts.specificTopicName.isNullOrBlank()) {
            sb.appendLine("- Active Focus Topic: '${facts.specificTopicName}' in subject '${facts.specificSubjectName ?: "Syllabus"}'")
        }
        if (!envelope.entryPointContext.isNullOrBlank()) {
            sb.appendLine("- Handoff Context: ${envelope.entryPointContext}")
        }

        return sb.toString()
    }

    /**
     * Determines which contextual action chips should accompany the reply based on intent and facts.
     */
    fun suggestActions(intent: BuddyQueryIntent, facts: StudyHubVerifiedFacts): List<BuddyAction> {
        val actions = mutableListOf<BuddyAction>()

        when (intent) {
            BuddyQueryIntent.PLANNING -> {
                actions.add(BuddyAction(BuddyActionType.OPEN_CALENDAR, "Open Today's Calendar"))
                actions.add(BuddyAction(BuddyActionType.START_FOCUS, "Start a Focus Session"))
            }
            BuddyQueryIntent.REVISION -> {
                actions.add(BuddyAction(BuddyActionType.OPEN_REVISION_HUB, "Open Revision Hub"))
                if (facts.dueRevisionsCount > 0) {
                    actions.add(BuddyAction(BuddyActionType.START_FOCUS, "Start Revision Session"))
                }
            }
            BuddyQueryIntent.TOPIC_SYLLABUS -> {
                actions.add(BuddyAction(BuddyActionType.OPEN_SYLLABUS, "View Syllabus"))
                actions.add(BuddyAction(BuddyActionType.START_FOCUS, "Start Focus on Topic"))
            }
            BuddyQueryIntent.STATS_PROGRESS -> {
                actions.add(BuddyAction(BuddyActionType.OPEN_STATS, "View Detailed Analytics"))
            }
            BuddyQueryIntent.EXAM_PREPARATION -> {
                actions.add(BuddyAction(BuddyActionType.OPEN_ADMISSION_CALENDAR, "Check Exam Targets"))
                actions.add(BuddyAction(BuddyActionType.OPEN_CALENDAR, "Plan Exam Prep in Calendar"))
            }
            BuddyQueryIntent.FOCUS_HABITS -> {
                actions.add(BuddyAction(BuddyActionType.START_FOCUS, "Start Focus Timer"))
                actions.add(BuddyAction(BuddyActionType.OPEN_STATS, "View Focus Stats"))
            }
            BuddyQueryIntent.GENERAL -> {
                actions.add(BuddyAction(BuddyActionType.OPEN_CALENDAR, "Plan My Day"))
                actions.add(BuddyAction(BuddyActionType.START_FOCUS, "Start Focus Session"))
            }
        }

        return actions.distinctBy { it.actionType }
    }

    fun getCurrentTimeReference(): String {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
