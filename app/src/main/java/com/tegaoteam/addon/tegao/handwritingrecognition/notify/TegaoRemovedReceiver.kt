package com.tegaoteam.addon.tegao.handwritingrecognition.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class TegaoRemovedReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action != Intent.ACTION_PACKAGE_FULLY_REMOVED) return
        val removedPackage = p1.data?.schemeSpecificPart ?: return
        if (removedPackage == "com.tegaoteam.application.tegao") {
            Log.i("TegaoRemovedReceiver", "Tegao Main removed, send notify to guide uninstallment")
            AddonNotificationCentre.instance.sendNotification(AddonNotificationCentre.NOTIFY_REMEMBER_UNINSTALL)
        }
    }
}