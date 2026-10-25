package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ChapterEntity
import com.example.data.local.RevisionHistoryEntity
import com.example.data.local.RevisionItemEntity
import com.example.data.local.SubjectEntity
import com.example.data.repository.StudyHubRepository
import com.example.revision.RecallRating
import com.example.revision.SmartRevisionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SmartRevisionIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: StudyHubRepository
    private lateinit var revisionManager: SmartRevisionManager

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = StudyHubRepository(db)
        revisionManager = SmartRevisionManager(repository)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `tracking a chapter creates a persistent revision item and prevents duplicate tracking`() = runBlocking {
        // Insert sample subject and chapter
        val subjectId = db.subjectDao().insertSubject(
            SubjectEntity(name = "Physics", colorHex = "#0796A3", iconKey = "atom", category = "Science")
        )
        val chapterId = db.chapterDao().insertChapter(
            ChapterEntity(subjectId = subjectId, title = "Newtonian Mechanics")
        )

        // Track in revision
        val trackedId = revisionManager.trackChapter(subjectId, chapterId)
        assertTrue(trackedId > 0)

        val items = db.revisionDao().getActiveRevisionItems().first()
        assertEquals(1, items.size)
        assertEquals(chapterId, items.first().chapterId)
        assertEquals(subjectId, items.first().subjectId)

        // Attempting to track the same chapter again must be idempotent and not create duplicate records
        val duplicateTrackedId = revisionManager.trackChapter(subjectId, chapterId)
        val itemsAfter = db.revisionDao().getActiveRevisionItems().first()
        assertEquals(1, itemsAfter.size)
    }

    @Test
    fun `completeReview atomically updates item schedule and appends history record`() = runBlocking {
        val subjectId = db.subjectDao().insertSubject(
            SubjectEntity(name = "Chemistry", colorHex = "#2563EB", iconKey = "flask", category = "Science")
        )
        val chapterId = db.chapterDao().insertChapter(
            ChapterEntity(subjectId = subjectId, title = "Chemical Bonding")
        )

        val itemId = revisionManager.trackChapter(subjectId, chapterId)

        // Complete review with EASY rating
        val result = revisionManager.completeReview(
            itemId = itemId,
            rating = RecallRating.EASY,
            durationSeconds = 1500, // 25 mins
            notes = "Mastered hybridization concepts"
        )

        assertNotNull(result)
        assertEquals(2.0, result!!.newIntervalDays, 0.001)

        val updatedItem = db.revisionDao().getRevisionItemByIdSync(itemId)
        assertNotNull(updatedItem)
        assertEquals(1, updatedItem!!.reviewCount)
        assertEquals("EASY", updatedItem.lastRecallRating)
        assertEquals(2.0, updatedItem.currentIntervalDays, 0.001)

        val historyList = db.revisionDao().getAllHistory().first()
        assertEquals(1, historyList.size)
        val history = historyList.first()
        assertEquals(itemId, history.revisionItemId)
        assertEquals("EASY", history.recallRating)
        assertEquals(1500, history.durationSeconds)
        assertEquals("Mastered hybridization concepts", history.notes)
    }

    @Test
    fun `postponing a revision shifts due date safely without altering interval multiplier`() = runBlocking {
        val subjectId = db.subjectDao().insertSubject(
            SubjectEntity(name = "Biology", colorHex = "#10B981", iconKey = "dna", category = "Science")
        )
        val chapterId = db.chapterDao().insertChapter(
            ChapterEntity(subjectId = subjectId, title = "Cell Division")
        )

        val itemId = revisionManager.trackChapter(subjectId, chapterId)
        val originalItem = db.revisionDao().getRevisionItemByIdSync(itemId)!!

        revisionManager.postponeItem(itemId, days = 2)

        val postponedItem = db.revisionDao().getRevisionItemByIdSync(itemId)!!
        assertTrue(postponedItem.nextDueAt > originalItem.nextDueAt)
        assertEquals(originalItem.currentIntervalDays, postponedItem.currentIntervalDays, 0.001)
        assertEquals(originalItem.reviewCount, postponedItem.reviewCount)
    }

    @Test
    fun `calendar proposal and commit are safe, idempotent, and avoid duplicate tasks`() = runBlocking {
        val subjectId = db.subjectDao().insertSubject(
            SubjectEntity(name = "Higher Math", colorHex = "#8B5CF6", iconKey = "function", category = "Science")
        )
        val chapterId = db.chapterDao().insertChapter(
            ChapterEntity(subjectId = subjectId, title = "Integration")
        )

        revisionManager.trackChapter(subjectId, chapterId)

        // Generate proposal
        val proposal = revisionManager.generateCalendarScheduleProposal(dailyBudgetMinutes = 60)
        assertEquals(1, proposal.size)
        assertEquals("Revise: Integration", proposal.first().title)

        // Commit proposal
        val committedCount = revisionManager.commitProposedTasksToCalendar(proposal)
        assertEquals(1, committedCount)

        val tasks = db.taskDao().getAllTasks().first()
        assertEquals(1, tasks.size)
        assertEquals("REVISION", tasks.first().planType)
        assertEquals("HIGH", tasks.first().priority)

        // Re-committing the same proposal should be idempotent (0 additional tasks inserted)
        val secondCommitCount = revisionManager.commitProposedTasksToCalendar(proposal)
        assertEquals(0, secondCommitCount)
        val tasksAfter = db.taskDao().getAllTasks().first()
        assertEquals(1, tasksAfter.size)
    }
}
