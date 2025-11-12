package com.tegaoteam.addon.tegao.handwritingrecognition

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader

class RecognitionModel private constructor(context: Context) {
    private var initJob = Job()
    private val initScope = CoroutineScope(Dispatchers.IO + initJob)

    private var jisToChar: Map<Int, String>? = null
    fun loadMapAndModel(jisCsvReader: BufferedReader) {
        // read JIS-Character mapping from csv (jis_map.csv)
        initScope.launch {
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

    init {
        val csvReader = context.assets.open("jis_map.csv").bufferedReader()
        loadMapAndModel(csvReader)
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