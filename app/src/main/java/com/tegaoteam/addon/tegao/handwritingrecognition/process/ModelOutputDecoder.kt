package com.tegaoteam.addon.tegao.handwritingrecognition.process

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class ModelOutputDecoder(csvInputStream: InputStream) {
    private var characterMap: Map<Int, String>? = null
    var decoderReady = false
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val mapResult = mutableMapOf<Int, String>()
            csvInputStream.bufferedReader().lines().forEach { line ->
                val entry = line.split(",")
                mapResult[entry[0].toInt()] = (entry[1])
            }
            withContext(Dispatchers.Main) {
                characterMap = mapResult
                decoderReady = true
                csvInputStream.close()
                Log.i("ModelOutputDecoder", "CSV read finished with ${characterMap?.size} entries")
            }
        }
    }

    fun decodeResult(modelOutput: List<Pair<Int, Float>>): List<String> {
        val suggestions = mutableListOf<String>()
        if (decoderReady) modelOutput.forEach {
            characterMap!![it.first]?.let { char ->
                suggestions.add(char)
                smallKanas[char]?.let { smol -> suggestions.add(smol) }
            }
        }
        Log.i("ModelOutputDecoder", "Result decoded: $suggestions")
        return suggestions
    }

    companion object {
        private val smallKanas = mapOf<String, String>(
            "あ" to "ぁ", "い" to "ぃ", "う" to "ぅ", "え" to "ぇ", "お" to "ぉ",
            "や" to "ゃ", "ゆ" to "ゅ", "よ" to "ょ",
            "わ" to "ゎ",
            "つ" to "っ"
        )
    }
}