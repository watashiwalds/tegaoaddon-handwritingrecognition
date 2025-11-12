package com.tegaoteam.addon.tegao.handwritingrecognition

import android.app.Application

class AddonApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: Application
            private set
    }
}