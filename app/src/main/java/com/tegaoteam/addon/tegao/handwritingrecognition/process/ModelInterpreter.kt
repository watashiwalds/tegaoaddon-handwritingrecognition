package com.tegaoteam.addon.tegao.handwritingrecognition.process

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.get
import androidx.core.graphics.scale
import com.tegaoteam.addon.tegao.handwritingrecognition.process.CharacterRecognizer.Companion.IMAGE_SIZE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ModelInterpreter(modelInputStream: InputStream) {
    private var modelInterpreter: Interpreter? = null
    private var modelOutputShape: IntArray? = null
    var modelReady = false
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // load model data to memory
            val modelBytes = modelInputStream.readBytes()
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
                modelReady = true
                modelInputStream.close()
                Log.i("ModelInterpreter", "TFLite model loaded into memory")
            }
        }
    }

    private fun transformInput(rawInput: ByteArray): ByteBuffer {
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

    suspend fun run(input: ByteArray): List<Pair<Int, Float>> {
        // return structure of the model: Array with 1 dim, inside nesting 3036 Pair<Index, Percentage>
        val outputData = Array(1) { FloatArray(modelOutputShape!![1]) }
        modelInterpreter!!.run(transformInput(input), outputData)
        val topCharIndex = outputData[0]
            .mapIndexed { idx, value -> idx to value }
            .sortedByDescending { it.second }
            .take(10)
            .takeWhile { it.second >= 0.01 }
        Log.i("ModelInterpreter", "Model ran, result: $topCharIndex")
        return topCharIndex
    }
}