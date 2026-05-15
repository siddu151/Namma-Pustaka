package com.example.nammapustaka.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.nammapustaka.R

/**
 * Local notification channels for library reminders (demo / offline-first).
 */
class LibraryNotificationHelper(private val context: Context) {
    private val appContext = context.applicationContext
    private val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val overdue = NotificationChannel(
            CH_OVERDUE,
            "Overdue books",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Alerts when borrowed books cross due date" }
        val due = NotificationChannel(
            CH_DUE,
            "Due reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Friendly reminders before the due date" }
        val reserve = NotificationChannel(
            CH_RESERVE,
            "Reservations",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "When a reserved title becomes available" }
        nm.createNotificationChannels(listOf(overdue, due, reserve))
    }

    fun notifyOverdue(bookTitle: String, studentName: String, days: Long, notificationId: Int) {
        val n = NotificationCompat.Builder(appContext, CH_OVERDUE)
            .setSmallIcon(R.drawable.ic_book_24)
            .setContentTitle(appContext.getString(R.string.notif_overdue_title))
            .setContentText(
                appContext.getString(
                    R.string.notif_overdue_body,
                    bookTitle,
                    studentName,
                    days.toInt().coerceAtLeast(0)
                )
            )
            .setAutoCancel(true)
            .build()
        nm.notify(notificationId, n)
    }

    fun notifyDueSoon(bookTitle: String, studentName: String, notificationId: Int) {
        val n = NotificationCompat.Builder(appContext, CH_DUE)
            .setSmallIcon(R.drawable.ic_book_24)
            .setContentTitle(appContext.getString(R.string.notif_due_title))
            .setContentText(appContext.getString(R.string.notif_due_body, bookTitle, studentName))
            .setAutoCancel(true)
            .build()
        nm.notify(notificationId, n)
    }

    fun showReservedBookAvailable(bookTitle: String, targetStudentId: Long) {
        val id = (BASE_RESERVE_ID + targetStudentId + bookTitle.hashCode()).toInt()
        val n = NotificationCompat.Builder(appContext, CH_RESERVE)
            .setSmallIcon(R.drawable.ic_book_24)
            .setContentTitle(appContext.getString(R.string.notif_reserve_title))
            .setContentText(appContext.getString(R.string.notif_reserve_body, bookTitle))
            .setAutoCancel(true)
            .build()
        nm.notify(id, n)
    }

    companion object {
        const val CH_OVERDUE = "np_overdue"
        const val CH_DUE = "np_due"
        const val CH_RESERVE = "np_reserve"
        private const val BASE_RESERVE_ID = 7000
    }
}
