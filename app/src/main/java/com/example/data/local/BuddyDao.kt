package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BuddyDao {

    @Query("SELECT * FROM buddy_conversations ORDER BY updatedAt DESC")
    fun getAllConversations(): Flow<List<BuddyConversationEntity>>

    @Query("SELECT * FROM buddy_conversations WHERE id = :id LIMIT 1")
    fun getConversationById(id: Long): Flow<BuddyConversationEntity?>

    @Query("SELECT * FROM buddy_conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationByIdSync(id: Long): BuddyConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: BuddyConversationEntity): Long

    @Update
    suspend fun updateConversation(conversation: BuddyConversationEntity)

    @Delete
    suspend fun deleteConversation(conversation: BuddyConversationEntity)

    @Query("DELETE FROM buddy_conversations WHERE id = :id")
    suspend fun deleteConversationById(id: Long)

    @Query("SELECT * FROM buddy_messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    fun getMessagesForConversation(conversationId: Long): Flow<List<BuddyMessageEntity>>

    @Query("SELECT * FROM buddy_messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getMessagesForConversationSync(conversationId: Long): List<BuddyMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: BuddyMessageEntity): Long

    @Update
    suspend fun updateMessage(message: BuddyMessageEntity)

    @Query("DELETE FROM buddy_messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesForConversation(conversationId: Long)

    @Transaction
    suspend fun deleteConversationAndMessages(conversationId: Long) {
        deleteMessagesForConversation(conversationId)
        deleteConversationById(conversationId)
    }
}
