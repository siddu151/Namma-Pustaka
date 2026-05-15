package com.example.nammapustaka

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nammapustaka.BuildConfig
import com.example.nammapustaka.database.AppDatabase
import com.example.nammapustaka.network.GeminiClient
import com.example.nammapustaka.notifications.LibraryNotificationHelper
import com.example.nammapustaka.notifications.OverdueCheckWorker
import com.example.nammapustaka.repository.LibraryRepository
import com.example.nammapustaka.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Application shell: Room database, repository, session, Gemini client, and background reminders.
 */
class NammaPustakaApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val sessionManager: SessionManager by lazy { SessionManager(this) }
    val notificationHelper: LibraryNotificationHelper by lazy { LibraryNotificationHelper(this) }
    val repository: LibraryRepository by lazy {
        LibraryRepository(
            db = database,
            geminiApi = GeminiClient.create(),
            geminiApiKey = BuildConfig.GEMINI_API_KEY,
            notificationHelper = notificationHelper
        )
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannels()
        appScope.launch(Dispatchers.IO) {
            repository.ensureSeedData()
        }
        scheduleOverdueWorker()
    }

    private fun scheduleOverdueWorker() {
        val work = PeriodicWorkRequestBuilder<OverdueCheckWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORK_OVERDUE,
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )
    }

    companion object {
        private const val WORK_OVERDUE = "nammapustaka_overdue_scan"
    }
}
