package com.tegaoteam.addon.tegao.handwritingrecognition

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.tegaoteam.addon.tegao.handwritingrecognition.process.CharacterRecognizer

class RecognitionService : Service() {
    private var trustedUid: Int? = null
    private var trustedPackageName = AddonApplication.Companion.parentPackage
    private fun firstVerify(callingUid: Int) {
        val callerPackages = packageManager.getPackagesForUid(callingUid)
        val callerPackageName = callerPackages?.firstOrNull()
        trustedUid = if (callerPackageName == trustedPackageName) callingUid else -1
    }

    private var recognitionCallback: IRecognitionCallback? = null

    private val binder = object: IRecognitionService.Stub() {
        override fun requestInputSuggestions(input: ByteArray?) {
            Log.i("RecognitionService", "Received request to suggesting by array ${input?.size}")
            val callingUid = getCallingUid()
            if (trustedUid == null) firstVerify(callingUid)
            if (callingUid != trustedUid) recognitionCallback?.onRecognized(null)
            Log.i("RecognitionService", "Request confirmed by trusted package")

            CharacterRecognizer.Companion.instance.recognizeThisWriting(input?: ByteArray(0))
        }

        override fun registerCallback(callback: IRecognitionCallback) {
            Log.i("RecognitionService", "Request to register callback $callback")
            val callingUid = getCallingUid()
            if (trustedUid == null) firstVerify(callingUid)
            if (callingUid != trustedUid) return

            recognitionCallback = callback
            CharacterRecognizer.Companion.instance.setRecognitionCallback{ suggestions ->
                callback.onRecognized(suggestions.toTypedArray())
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        CharacterRecognizer.Companion.instance
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