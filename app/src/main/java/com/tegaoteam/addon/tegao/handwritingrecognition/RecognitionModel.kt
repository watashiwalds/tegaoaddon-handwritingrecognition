package com.tegaoteam.addon.tegao.handwritingrecognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import androidx.core.graphics.scale
import kotlinx.coroutines.Job

class RecognitionModel private constructor(private val context: Context) {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private var jisToChar: Map<Int, String>? = null
    private var modelInterpreter: Interpreter? = null

    private val defaultScope = CoroutineScope(Dispatchers.Default)
    private var processJob: Job? = null

    private var modelOutputShape: IntArray? = null

    // read JIS-Character mapping from csv (jis_map.csv)
    private fun loadJISMap(jisCsvReader: BufferedReader) {
        val mapJob = ioScope.launch {
            val mapResult = mutableMapOf<Int, String>()
            jisCsvReader.lines().forEach { line ->
                val entry = line.split(",")
                mapResult[entry[0].toIntOrNull()?: 0] = entry[1]
            }
            withContext(Dispatchers.Main) {
                jisToChar = mapResult
                Log.i("RecognitionModel", "CSV read finished with ${jisToChar?.size} entries")
            }
        }
    }

    // load TFLite model into memory, wrapped by Interpreter
    private fun loadTFLiteModel(modelAsset: InputStream) {
        val modelJob = ioScope.launch {
            // load model data to memory
            val modelBytes = modelAsset.readBytes()
            val modelBuffer = ByteBuffer.allocateDirect(modelBytes.size).apply {
                order(ByteOrder.nativeOrder()) // set to use CPU's byteOrder
                put(modelBytes) // put the bytes from input to memory
                rewind() // set the read cursor to start
            }
            // register the model under TensorflowLite's interpreter
            val interpreter = Interpreter(modelBuffer)
            withContext(Dispatchers.Main) {
                modelInterpreter = interpreter
                modelOutputShape = modelInterpreter!!.getOutputTensor(0).shape()
                Log.i("RecognitionModel", "TFLite model loaded into memory")
            }
        }
    }

    init {
        val csvReader = context.assets.open("jis_map.csv").bufferedReader()
        val modelReader = context.assets.open("model_float16.tflite")
        loadJISMap(csvReader)
        loadTFLiteModel(modelReader)
    }

    fun isModelReady() = (jisToChar != null && modelInterpreter != null)

    // transforming input image (byteArray) to proper model input
    fun inputTransformer(rawInput: ByteArray): ByteBuffer {
        val bitmap = BitmapFactory
            .decodeByteArray(rawInput, 0, rawInput.size)
            .copy(Bitmap.Config.ARGB_8888, false)
            .scale(IMAGE_SIZE, IMAGE_SIZE)

        val buffer = ByteBuffer.allocateDirect(IMAGE_SIZE * IMAGE_SIZE * 3 * 4).order(ByteOrder.nativeOrder())
        for (y in 0 until IMAGE_SIZE)
            for (x in 0 until IMAGE_SIZE) {
                val pixel = bitmap[x, y]
                if (Color.alpha(pixel) < 128) {
                    buffer.putFloat(255f)
                    buffer.putFloat(255f)
                    buffer.putFloat(255f)
                } else {
                    buffer.putFloat(0f)
                    buffer.putFloat(0f)
                    buffer.putFloat(0f)
                }
            }
        buffer.rewind()
        return buffer
    }

    suspend fun runRecognitionModel(input: ByteArray): List<Pair<Int, Float>>? {
        // return structure of the model: Array with 1 dim, inside nesting 3036 Pair<Index, Percentage>
        val outputData = Array(1) { FloatArray(modelOutputShape!![1]) }
        modelInterpreter!!.run(inputTransformer(input), outputData)
        Log.i("RecognitionModel", "Model finished processing")
        val topCharIndex = outputData[0]
            .mapIndexed { idx, value -> idx to value }
            .sortedByDescending { it.second }
            .take(10)
            .takeWhile { it.second >= 0.05 }
        Log.i("RecognitionModel", "Model result: $topCharIndex")
        return topCharIndex
    }

    fun recognizeThisWriting(input: ByteArray): List<String>? {
        if (!isModelReady()) return null
        processJob?.cancel() // cancel any performing processing to do new one

        Log.i("RecognitionModel", "Model started processing...")
        var result: List<String>? = null
        return result
    }

    companion object {
        const val IMAGE_SIZE = 96
        val instance by lazy { RecognitionModel(AddonApplication.instance) }
    }
}