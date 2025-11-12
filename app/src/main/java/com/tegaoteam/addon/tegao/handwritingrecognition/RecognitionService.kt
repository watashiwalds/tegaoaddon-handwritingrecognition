package com.tegaoteam.addon.tegao.handwritingrecognition

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class RecognitionService : Service() {
    private var trustedUid: Int? = null
    private var trustedPackageName = "com.tegaoteam.application.tegao"
    private fun firstVerify(callingUid: Int) {
        val callerPackages = packageManager.getPackagesForUid(callingUid)
        val callerPackageName = callerPackages?.firstOrNull()
        trustedUid = if (callerPackageName == trustedPackageName) callingUid else -1
    }

    private val binder = object: IRecognitionService.Stub() {
        override fun requestInputSuggestions(input: ByteArray?): List<String?>? {
            val callingUid = getCallingUid()
            if (trustedUid == null) firstVerify(callingUid)
            if (callingUid != trustedUid) return null

            val test = RecognitionModel.getSomeRandomChars().apply {
                add(input?.size.toString())
            }
            return test
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("RecognitionService", "Service created, ready for functions")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("RecognitionService", "Service bind with correct intent call, caller unidentified until first use")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i("RecognitionService", "Service unbind from $trustedUid which was ${if (trustedUid != -1) "trusted" else "not trusted"}")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("RecognitionService", "Service destroyed, memory freed")
    }
}