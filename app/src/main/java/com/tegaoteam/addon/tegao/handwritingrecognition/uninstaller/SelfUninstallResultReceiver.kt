package com.tegaoteam.addon.tegao.handwritingrecognition.uninstaller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller

class SelfUninstallResultReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val uninstallStatus = p1?.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
        if (uninstallStatus == PackageInstaller.STATUS_FAILURE_ABORTED || uninstallStatus == PackageInstaller.STATUS_SUCCESS) {
            UninstallState.pendingUninstall = true
        } else {
            SelfUninstallWorker.trySelfUninstall(p0!!)
        }
    }
}