package com.tegaoteam.addon.tegao.handwritingrecognition

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RecognitionService : Service() {

    private val binder = object: IRecognitionService.Stub() {
        override fun requestInputSuggestions(input: ByteArray?): List<String?>? {
            val test = RecognitionModel.getSomeRandomChars().apply {
                add(input?.size.toString())
            }
            return test
        }
    }

    override fun onBind(intent: Intent): IBinder = binder
}