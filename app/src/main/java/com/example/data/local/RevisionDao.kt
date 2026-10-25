package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RevisionDao {

    @Query("SELECT * FROM revision_items ORDER BY nextDueDate ASC, id ASC")
    fun getAllRevisionItems(): Flow<List<RevisionItemEntity>>

    @Query("SELECT * FROM revision_items WHERE status = 'ACTIVE' ORDER BY nextDueDate ASC, id ASC")
    fun getActiveRevisionItems(): Flow<List<RevisionItemEntity>>

    @Query("SELECT * FROM revision_items WHERE status = 'ACTIVE' ORDER BY nextDueDate ASC, id ASC")
    suspend fun getActiveRevisionItemsSync(): List<RevisionItemEntity>

    @Query("SELECT * FROM revision_items WHERE id = :id LIMIT 1")
    fun getRevisionItemById(id: Long): Flow<RevisionItemEntity?>

    @Query("SELECT * FROM revision_items WHERE id = :id LIMIT 1")
    suspend fun getRevisionItemByIdSync(id: Long): RevisionItemEntity?

    @Query("SELECT * FROM revision_items WHERE chapterId = :chapterId LIMIT 1")
    fun getRevisionItemByChapterId(chapterId: Long): Flow<RevisionItemEntity?>

    @Query("SELECT * FROM revision_items WHERE chapterId = :chapterId LIMIT 1")
    suspend fun getRevisionItemByChapterIdSync(chapterId: Long): RevisionItemEntity?

    @Query("SELECT * FROM revision_items WHERE status = 'ACTIVE' AND nextDueDate <= :targetDate ORDER BY nextDueDate ASC")
    suspend fun getDueRevisionItemsSync(targetDate: String): List<RevisionItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisionItem(item: RevisionItemEntity): Long

    @Update
    suspend fun updateRevisionItem(item: RevisionItemEntity)

    @Delete
    suspend fun deleteRevisionItem(item: RevisionItemEntity)

    @Query("DELETE FROM revision_items WHERE chapterId = :chapterId")
    suspend fun deleteRevisionItemByChapterId(chapterId: Long)

    @Query("DELETE FROM revision_items WHERE subjectId = :subjectId")
    suspend fun deleteRevisionItemsForSubject(subjectId: Long)

    // --- Revision History ---

    @Query("SELECT * FROM revision_history ORDER BY completedAt DESC")
    fun getAllHistory(): Flow<List<RevisionHistoryEntity>>

    @Query("SELECT * FROM revision_history WHERE revisionItemId = :revisionItemId ORDER BY completedAt DESC")
    fun getHistoryForRevisionItem(revisionItemId: Long): Flow<List<RevisionHistoryEntity>>

    @Query("SELECT * FROM revision_history WHERE revisionItemId = :revisionItemId ORDER BY completedAt DESC")
    suspend fun getHistoryForRevisionItemSync(revisionItemId: Long): List<RevisionHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: RevisionHistoryEntity): Long

    @Delete
    suspend fun deleteHistory(history: RevisionHistoryEntity)

    @Query("DELETE FROM revision_history WHERE revisionItemId = :revisionItemId")
    suspend fun deleteHistoryForRevisionItem(revisionItemId: Long)

    /**
     * Atomically updates a revision item's schedule and logs the completed review history.
     */
    @Transaction
    suspend fun recordCompletedReview(
        updatedItem: RevisionItemEntity,
        historyEntry: RevisionHistoryEntity
    ): Long {
        updateRevisionItem(updatedItem)
        return insertHistory(historyEntry)
    }
}
