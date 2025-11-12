package com.tegaoteam.addon.tegao.handwritingrecognition

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RecognitionModel private constructor(context: Context) {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private var jisToChar: Map<Int, String>? = null
    private var modelInterpreter: Interpreter? = null

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

    fun isModelReady() = jisToChar != null

    companion object {
        private val randomChars = listOf<Char>('漢','字','梵','語','千','文','鬘','唐','聖','照','権','実','鏡','弘','仁','真','名','仮','平','万','葉','子','供','煙','草','天')
        private val randomNumbers = listOf<Int>(0,1,2,3,4,5,6,7,8,9,10)
        fun getSomeRandomChars(): MutableList<String> {
            Log.i("RecognitionModel", "Call received, return dummy data")
            val result = mutableListOf<String>()
            for (i in 0..randomNumbers.random()) result.add(randomChars.random().toString())
            return result
        }

        val instance by lazy { RecognitionModel(AddonApplication.instance) }
    }
}