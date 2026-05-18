package com.sanju.expensetracker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.sanju.expensetracker.R

object NotificationHelper {

    const val CHANNEL_ID = "expense_reminder_channel"

    fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(
                    R.string.expense_reminder_channel_name
                ),
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}