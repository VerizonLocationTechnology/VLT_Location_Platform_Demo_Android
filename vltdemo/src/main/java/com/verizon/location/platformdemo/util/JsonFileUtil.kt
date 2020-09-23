package com.verizon.location.platformdemo.util

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object JsonFileUtil {

    /**
     * Loads a JSON object from a file in the raw resources directory.
     */
    @JvmStatic
    fun loadJSONFromFile(context: Context, resId: Int): JSONObject {
        var data = JSONObject()
        val inputStream = context.resources.openRawResource(resId)
        inputStream.let {
            try {
                val inputStreamReader = InputStreamReader(inputStream)
                val reader = BufferedReader(inputStreamReader)
                reader.use {
                    val fileString = it.readText()
                    data = JSONObject(fileString)
                }
            } catch (ioe: IOException) {
                ioe.printStackTrace()
                throw ioe
            }
        }
        return data
    }

}