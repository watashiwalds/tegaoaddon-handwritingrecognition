package com.tegaoteam.addon.tegao.handwritingrecognition.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri

import com.tegaoteam.addon.tegao.handwritingrecognition.notify.AddonNotificationResources as Res

class AddonNotificationCentre {
    private lateinit var notifyManager: NotificationManager

    init {
        val channel = NotificationChannel(
            Res.notifyChannelId,
            Res.channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = Res.channelDesc
        }
        notifyManager = Res.appContext.getSystemService(NotificationManager::class.java)
        notifyManager.createNotificationChannel(channel)
    }

    fun sendNotification(premadeNotify: Int) {
        var reqNotif: NotificationCompat.Builder? = null
        when (premadeNotify) {
            NOTIFY_REMEMBER_UNINSTALL -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:${Res.appContext.packageName}".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                }
                val openDetails = PendingIntent.getActivity(
                    Res.appContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                reqNotif = NotificationCompat.Builder(Res.appContext, Res.notifyChannelId).apply {
                    setSmallIcon(Res.appIconResId)
                    setContentTitle(Res.rmbToUninstall_label)
                    setContentText(Res.rmbToUninstall_content)
                    setContentIntent(openDetails)
                    setAutoCancel(true)
                }
            }
        }
        reqNotif?.let { notifyManager.notify(NOTIFY_REMEMBER_UNINSTALL, it.build()) }
    }

    companion object {
        const val NOTIFY_REMEMBER_UNINSTALL = 2740
        val instance by lazy {
            AddonNotificationCentre()
        }
    }
}