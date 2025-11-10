package com.tegaoteam.addon.tegao.handwritingrecognition

import java.io.ByteArrayOutputStream

interface IRecognitionService {
    fun requestInputSuggestions(input: ByteArrayOutputStream): List<String>
    fun requestFinished(callback: IRecognitionCallback)
}