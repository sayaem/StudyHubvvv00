package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tracks an active syllabus item (chapter/topic) in the Spaced Repetition Revision Engine.
 * Unique index on chapterId ensures that the same chapter cannot have duplicate active tracking records.
 */
@Entity(
    tableName = "revision_items",
    indices = [
        Index(value = ["chapterId"], unique = true),
        Index(value = ["nextDueDate"]),
        Index(value = ["subjectId"])
    ]
)
data class RevisionItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val subjectId: Long,
    val chapterId: Long,
    val status: String = "ACTIVE", // "ACTIVE", "PAUSED", "ARCHIVED"
    val firstTrackedAt: Long = System.currentTimeMillis(),
    val lastReviewedAt: Long? = null,
    val nextDueAt: Long,
    val nextDueDate: String, // YYYY-MM-DD
    val currentIntervalDays: Double = 1.0,
    val reviewCount: Int = 0,
    val consecutiveSuccessCount: Int = 0,
    val lastRecallRating: String? = null, // "HARD", "OKAY", "EASY"
    val estimatedDurationMinutes: Int = 25,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Immutable append-only log of every completed revision session.
 */
@Entity(
    tableName = "revision_history",
    indices = [
        Index(value = ["revisionItemId"]),
        Index(value = ["completedDate"])
    ]
)
data class RevisionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val revisionItemId: Long,
    val completedAt: Long = System.currentTimeMillis(),
    val completedDate: String, // YYYY-MM-DD
    val recallRating: String, // "HARD", "OKAY", "EASY"
    val previousIntervalDays: Double,
    val newIntervalDays: Double,
    val reviewTimingStatus: String, // "ON_TIME", "OVERDUE", "EARLY"
    val durationSeconds: Int = 0,
    val focusSessionId: Long? = null,
    val notes: String? = null
)
