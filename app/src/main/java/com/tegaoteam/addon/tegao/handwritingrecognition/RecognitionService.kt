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

            // null return occasions
            if (callingUid != trustedUid) return null
            if (input == null) return null

            val result = RecognitionModel.instance.recognizeThisWriting(input)

            return result
        }
    }

    override fun onCreate() {
        super.onCreate()
        RecognitionModel.instance
        Log.i("RecognitionService", "Service created")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.i("RecognitionService", "Service bind with correct explicit intent call, caller unidentified until first use")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i("RecognitionService", "Service unbind from $trustedUid which was ${if (trustedUid != -1) "trusted" else "not trusted"}")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("RecognitionService", "Service destroyed")
    }
}