package com.tegaoteam.addon.tegao.handwritingrecognition

interface IRecognitionCallback {
    fun requestFinished(suggestions: List<String>)
}