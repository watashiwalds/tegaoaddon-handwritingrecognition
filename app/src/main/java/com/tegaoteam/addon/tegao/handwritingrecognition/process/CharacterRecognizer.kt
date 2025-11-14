package com.tegaoteam.addon.tegao.handwritingrecognition.process

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.get
import androidx.core.graphics.scale
import com.tegaoteam.addon.tegao.handwritingrecognition.AddonApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CharacterRecognizer private constructor(context: Context) {
    private var outputDecoder: ModelOutputDecoder = ModelOutputDecoder(context.assets.open(CSV_CHARACTER_FILE_NAME))
    private var modelInterpreter: ModelInterpreter = ModelInterpreter(context.assets.open(MODEL_FILE_NAME))

    private val defaultScope = CoroutineScope(Dispatchers.Default)
    private var processJob: Job? = null

    private var recognitionCallback: ((List<String>) -> Unit)? = null

    fun setRecognitionCallback(callback: (List<String>) -> Unit) {
        recognitionCallback = callback
    }

    fun isModelReady() = (outputDecoder.decoderReady && modelInterpreter.modelReady)

    fun recognizeThisWriting(input: ByteArray) {
        if (!isModelReady()) {
            Log.w("CharacterRecognizer", "Model hasn't ready, cancel request")
            return
        }
        processJob?.cancel() // cancel any performing processing to do new one

        processJob = defaultScope.launch {
            Log.i("CharacterRecognizer", "Model started processing...")
            val modelOutput = modelInterpreter.run(input)
            val suggestions = outputDecoder.decodeResult(modelOutput)
            withContext(Dispatchers.Main) {
                recognitionCallback?.invoke(suggestions)
                Log.i("CharacterRecognizer", "Called for callback function $suggestions ${recognitionCallback != null}")
            }
        }
    }

    companion object {
        const val CSV_CHARACTER_FILE_NAME = "jis_map.csv"
        const val MODEL_FILE_NAME = "model_float16.tflite"
        const val IMAGE_SIZE = 96
        val instance by lazy { CharacterRecognizer(AddonApplication.Companion.instance) }
    }
}