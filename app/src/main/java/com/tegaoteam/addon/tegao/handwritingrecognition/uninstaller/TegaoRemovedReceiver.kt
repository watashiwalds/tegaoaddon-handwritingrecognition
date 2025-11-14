package com.tegaoteam.addon.tegao.handwritingrecognition.uninstaller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TegaoRemovedReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action != Intent.ACTION_PACKAGE_FULLY_REMOVED) return
        val removedPackage = p1.data?.schemeSpecificPart ?: return
        if (removedPackage == "com.tegaoteam.application.tegao") {
            SelfUninstallWorker.trySelfUninstall(p0!!)
        }
    }
}