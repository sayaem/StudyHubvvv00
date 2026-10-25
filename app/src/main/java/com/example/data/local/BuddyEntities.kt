package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a conversation thread with Study Buddy.
 */
@Entity(
    tableName = "buddy_conversations",
    indices = [
        Index(value = ["updatedAt"])
    ]
)
data class BuddyConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String = "New Study Session",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val initialContextTopicId: Long? = null,
    val initialContextSubjectId: Long? = null
)

/**
 * Represents a single message in a Study Buddy conversation.
 */
@Entity(
    tableName = "buddy_messages",
    indices = [
        Index(value = ["conversationId"]),
        Index(value = ["createdAt"])
    ]
)
data class BuddyMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val conversationId: Long,
    val sender: String, // "USER", "ASSISTANT", "SYSTEM"
    val content: String,
    val status: String = "SENT", // "SENDING", "SENT", "FAILED"
    val createdAt: Long = System.currentTimeMillis(),
    val suggestedActionsJson: String? = null // Optional encoded action tags
)
