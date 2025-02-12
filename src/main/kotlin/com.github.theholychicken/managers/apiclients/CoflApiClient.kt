package com.github.theholychicken.managers.apiclients

import com.github.theholychicken.config.GuiConfig
import com.google.gson.Gson
import com.google.gson.JsonObject

object CoflApiClient {
    private val gson = Gson()
    private val key = if (GuiConfig.useSellOffer) "buy" else "sell"

    fun fetchPrice(itemTag: String): Double {
        val response = HttpClient.sendRequest("https://sky.cofl.net/api/price/{itemTag}/current")
        return gson.fromJson(response, JsonObject::class.java).get(key).asDouble
    }
}