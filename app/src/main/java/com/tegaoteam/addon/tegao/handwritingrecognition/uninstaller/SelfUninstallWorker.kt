package com.tegaoteam.addon.tegao.handwritingrecognition.uninstaller

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class SelfUninstallWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params) {
    @RequiresPermission(Manifest.permission.REQUEST_DELETE_PACKAGES)
    override suspend fun doWork(): Result {
        delay((300L..700L).random())
        igniteUninstallProcess()
        return if (UninstallState.pendingUninstall) Result.success() else Result.failure()
    }

    @RequiresPermission(anyOf = [Manifest.permission.REQUEST_DELETE_PACKAGES])
    private fun igniteUninstallProcess() {
        val installer = applicationContext.packageManager.packageInstaller
        val pending = PendingIntent.getBroadcast(
            applicationContext,
            0,
            Intent(applicationContext, SelfUninstallResultReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT)
        installer.uninstall(applicationContext.packageName, pending.intentSender)
    }

    companion object {
        fun trySelfUninstall(appContext: Context) {
            val followUninstall = OneTimeWorkRequestBuilder<SelfUninstallWorker>().build()
            WorkManager.getInstance(appContext).enqueue(followUninstall)
        }
    }
}