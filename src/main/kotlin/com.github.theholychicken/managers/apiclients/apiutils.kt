package com.github.theholychicken.managers.apiclients

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

object HttpClient {
    private val httpClient = OkHttpClient()
    private val gson = Gson()

    fun sendRequest(url: String): String {
        val request = Request.Builder().url(url).build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("API request was unsuccessful.")
            }
            return response.body?.string() ?: throw IOException("Empty response")
        }
    }
}