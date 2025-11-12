package com.tegaoteam.addon.tegao.handwritingrecognition

import android.app.Service
import android.content.Intent
import android.os.IBinder

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

    override fun onBind(intent: Intent): IBinder = binder
}