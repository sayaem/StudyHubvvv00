package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfile::class,
        SubjectEntity::class,
        ChapterEntity::class,
        StudyTaskEntity::class,
        StudySessionEntity::class,
        AdmissionTargetEntity::class,
        StudyNoteEntity::class,
        NotificationItemEntity::class,
        AppSettingsEntity::class,
        FocusPresetEntity::class,
        FocusGamificationEntity::class,
        ActiveFocusSessionEntity::class,
        RevisionItemEntity::class,
        RevisionHistoryEntity::class,
        BuddyConversationEntity::class,
        BuddyMessageEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun subjectDao(): SubjectDao
    abstract fun chapterDao(): ChapterDao
    abstract fun taskDao(): StudyTaskDao
    abstract fun sessionDao(): StudySessionDao
    abstract fun admissionDao(): AdmissionDao
    abstract fun noteDao(): StudyNoteDao
    abstract fun notificationDao(): NotificationDao
    abstract fun settingsDao(): SettingsDao
    abstract fun focusPresetDao(): FocusPresetDao
    abstract fun focusGamificationDao(): FocusGamificationDao
    abstract fun activeFocusSessionDao(): ActiveFocusSessionDao
    abstract fun revisionDao(): RevisionDao
    abstract fun buddyDao(): BuddyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `revision_items` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `subjectId` INTEGER NOT NULL,
                        `chapterId` INTEGER NOT NULL,
                        `status` TEXT NOT NULL,
                        `firstTrackedAt` INTEGER NOT NULL,
                        `lastReviewedAt` INTEGER,
                        `nextDueAt` INTEGER NOT NULL,
                        `nextDueDate` TEXT NOT NULL,
                        `currentIntervalDays` REAL NOT NULL,
                        `reviewCount` INTEGER NOT NULL,
                        `consecutiveSuccessCount` INTEGER NOT NULL,
                        `lastRecallRating` TEXT,
                        `estimatedDurationMinutes` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_revision_items_chapterId` ON `revision_items` (`chapterId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_revision_items_nextDueDate` ON `revision_items` (`nextDueDate`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_revision_items_subjectId` ON `revision_items` (`subjectId`)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `revision_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `revisionItemId` INTEGER NOT NULL,
                        `completedAt` INTEGER NOT NULL,
                        `completedDate` TEXT NOT NULL,
                        `recallRating` TEXT NOT NULL,
                        `previousIntervalDays` REAL NOT NULL,
                        `newIntervalDays` REAL NOT NULL,
                        `reviewTimingStatus` TEXT NOT NULL,
                        `durationSeconds` INTEGER NOT NULL,
                        `focusSessionId` INTEGER,
                        `notes` TEXT
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_revision_history_revisionItemId` ON `revision_history` (`revisionItemId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_revision_history_completedDate` ON `revision_history` (`completedDate`)")
            }
        }

        val MIGRATION_4_5 = object : androidx.room.migration.Migration(4, 5) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `buddy_conversations` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `initialContextTopicId` INTEGER,
                        `initialContextSubjectId` INTEGER
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_buddy_conversations_updatedAt` ON `buddy_conversations` (`updatedAt`)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `buddy_messages` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `conversationId` INTEGER NOT NULL,
                        `sender` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `suggestedActionsJson` TEXT
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_buddy_messages_conversationId` ON `buddy_messages` (`conversationId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_buddy_messages_createdAt` ON `buddy_messages` (`createdAt`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studyhub_database"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
