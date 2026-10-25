package com.example.notifications

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.Random
import kotlin.concurrent.thread
import kotlin.math.sin

class AmbientSoundManager {

    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var soundThread: Thread? = null

    @Volatile
    private var activeSoundType: String = "OFF"

    @Volatile
    private var activeVolume: Float = 0.7f

    fun setSound(soundType: String) {
        if (activeSoundType == soundType && isPlaying) return
        activeSoundType = soundType
        if (soundType == "OFF") {
            stop()
        } else {
            startSoundGenerator()
        }
    }

    fun setVolume(volume: Float) {
        activeVolume = volume.coerceIn(0f, 1f)
        audioTrack?.setVolume(activeVolume)
    }

    fun isPlaying(): Boolean = isPlaying && activeSoundType != "OFF"

    fun getActiveSound(): String = activeSoundType

    private fun startSoundGenerator() {
        stop()

        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.setVolume(activeVolume)
        audioTrack?.play()
        isPlaying = true

        soundThread = thread(start = true, isDaemon = true) {
            val random = Random()
            val chunk = ShortArray(bufferSize)
            var phase = 0.0
            var wavePhase = 0.0

            while (isPlaying && activeSoundType != "OFF") {
                val currentType = activeSoundType
                for (i in chunk.indices) {
                    when (currentType) {
                        "RAIN" -> {
                            // Pink-ish noise synthesis for rainfall
                            val white = random.nextFloat() * 2f - 1f
                            val drop = if (random.nextFloat() < 0.001f) (random.nextFloat() * 0.8f) else 0f
                            val sample = (white * 0.35f) + drop
                            chunk[i] = (sample * 8000).toInt().coerceIn(-32768, 32767).toShort()
                        }
                        "OCEAN" -> {
                            // Undulating wave sound
                            wavePhase += 0.0001
                            val modulator = (sin(wavePhase) + 1.0) * 0.5
                            val white = random.nextFloat() * 2f - 1f
                            val sample = white * (0.2f + (modulator * 0.4f))
                            chunk[i] = (sample * 9000).toInt().coerceIn(-32768, 32767).toShort()
                        }
                        "LOFI" -> {
                            // Soft warm dual tone harmony
                            phase += 0.02
                            val freq1 = 220.0 // A3
                            val freq2 = 277.18 // C#4
                            val tone = (sin(2.0 * Math.PI * freq1 * phase / sampleRate) +
                                    sin(2.0 * Math.PI * freq2 * phase / sampleRate)) * 0.15
                            val noise = (random.nextFloat() * 2f - 1f) * 0.03f
                            chunk[i] = ((tone + noise) * 10000).toInt().coerceIn(-32768, 32767).toShort()
                        }
                        "CAFE" -> {
                            // Warm ambient texture
                            val white = random.nextFloat() * 2f - 1f
                            val sample = white * 0.22f
                            chunk[i] = (sample * 6000).toInt().coerceIn(-32768, 32767).toShort()
                        }
                        "WHITE_NOISE" -> {
                            val white = random.nextFloat() * 2f - 1f
                            chunk[i] = (white * 4500).toInt().coerceIn(-32768, 32767).toShort()
                        }
                        else -> {
                            chunk[i] = 0
                        }
                    }
                }
                audioTrack?.write(chunk, 0, chunk.size)
            }
        }
    }

    fun stop() {
        isPlaying = false
        soundThread?.interrupt()
        soundThread = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }
}
