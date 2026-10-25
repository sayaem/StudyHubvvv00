package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserProfile?

    @Upsert
    suspend fun upsertUser(user: UserProfile)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY sortOrder ASC, id ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    fun getSubjectById(id: Long): Flow<SubjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)
}

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters ORDER BY sortOrder ASC, id ASC")
    fun getAllChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY sortOrder ASC, id ASC")
    fun getChaptersForSubject(subjectId: Long): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)

    @Query("DELETE FROM chapters WHERE subjectId = :subjectId")
    suspend fun deleteChaptersForSubject(subjectId: Long)
}

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY date ASC, time ASC, id ASC")
    fun getAllTasks(): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_tasks WHERE date = :date ORDER BY time ASC, id ASC")
    fun getTasksForDate(date: String): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<StudyTaskEntity>): List<Long>

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Delete
    suspend fun deleteTask(task: StudyTaskEntity)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE startedAt >= :start AND startedAt <= :end ORDER BY startedAt DESC")
    fun getSessionsForRange(start: Long, end: Long): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)
}

@Dao
interface AdmissionDao {
    @Query("SELECT * FROM admission_targets WHERE isArchived = 0 ORDER BY applicationDeadline ASC, id ASC")
    fun getAllTargets(): Flow<List<AdmissionTargetEntity>>

    @Query("SELECT * FROM admission_targets WHERE id = :id LIMIT 1")
    fun getTargetById(id: Long): Flow<AdmissionTargetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarget(target: AdmissionTargetEntity): Long

    @Update
    suspend fun updateTarget(target: AdmissionTargetEntity)

    @Delete
    suspend fun deleteTarget(target: AdmissionTargetEntity)
}

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM study_notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNoteEntity): Long

    @Update
    suspend fun updateNote(note: StudyNoteEntity)

    @Delete
    suspend fun deleteNote(note: StudyNoteEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_items ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationItemEntity>>

    @Query("SELECT * FROM notification_items ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentNotificationsSync(limit: Int = 50): List<NotificationItemEntity>

    @Query("SELECT COUNT(*) FROM notification_items WHERE createdAt >= :startOfDayTimestamp")
    suspend fun getTodayNotificationCount(startOfDayTimestamp: Long): Int

    @Query("SELECT COUNT(*) FROM notification_items WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(item: NotificationItemEntity): Long

    @Query("UPDATE notification_items SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()

    @Delete
    suspend fun deleteNotification(item: NotificationItemEntity)

    @Query("DELETE FROM notification_items WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    @Query("DELETE FROM notification_items")
    suspend fun clearAllNotifications()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): AppSettingsEntity?

    @Upsert
    suspend fun upsertSettings(settings: AppSettingsEntity)
}

@Dao
interface FocusPresetDao {
    @Query("SELECT * FROM focus_presets ORDER BY isDefault DESC, id ASC")
    fun getAllPresets(): Flow<List<FocusPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: FocusPresetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<FocusPresetEntity>)

    @Update
    suspend fun updatePreset(preset: FocusPresetEntity)

    @Delete
    suspend fun deletePreset(preset: FocusPresetEntity)
}

@Dao
interface FocusGamificationDao {
    @Query("SELECT * FROM focus_gamification WHERE id = 1 LIMIT 1")
    fun getGamification(): Flow<FocusGamificationEntity?>

    @Query("SELECT * FROM focus_gamification WHERE id = 1 LIMIT 1")
    suspend fun getGamificationSync(): FocusGamificationEntity?

    @Upsert
    suspend fun upsertGamification(item: FocusGamificationEntity)
}

@Dao
interface ActiveFocusSessionDao {
    @Query("SELECT * FROM active_focus_session WHERE id = 1 LIMIT 1")
    fun getActiveSessionFlow(): Flow<ActiveFocusSessionEntity?>

    @Query("SELECT * FROM active_focus_session WHERE id = 1 LIMIT 1")
    suspend fun getActiveSession(): ActiveFocusSessionEntity?

    @Upsert
    suspend fun saveActiveSession(session: ActiveFocusSessionEntity)

    @Query("DELETE FROM active_focus_session WHERE id = 1")
    suspend fun clearActiveSession()
}


