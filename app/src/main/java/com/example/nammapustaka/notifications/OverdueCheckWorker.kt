package com.example.nammapustaka.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nammapustaka.database.AppDatabase
import kotlin.random.Random

/**
 * Periodically checks overdue rows and surfaces local notifications.
 */
class OverdueCheckWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val helper = LibraryNotificationHelper(applicationContext)
        helper.createChannels()
        val rows = db.transactionDao().loadOverdueRows()
        rows.forEachIndexed { index, row ->
            val id = Random.nextInt(10_000, 99_999) + index
            helper.notifyOverdue(row.bookTitle, row.studentName, row.overdueDays, id)
        }
        return Result.success()
    }
}
