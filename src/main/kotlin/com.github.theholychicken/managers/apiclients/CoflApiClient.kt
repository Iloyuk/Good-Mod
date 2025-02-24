package com.github.theholychicken.managers.apiclients

import com.github.theholychicken.config.GuiConfig
import com.github.theholychicken.utils.modMessage
import com.google.gson.Gson
import com.google.gson.JsonObject

object CoflApiClient {
    private val gson = Gson()
    private val key = if (GuiConfig.useSellOffer) "buy" else "sell"

    fun fetchPrice(itemTag: String): Double {
        var tag = itemTag
        if (itemTag == "WITHER_ESSENCE") {
            tag = "ESSENCE_WITHER"
        } else if (itemTag == "UNDEAD_ESSENCE") {
            tag = "ESSENCE_UNDEAD"
        }
        modMessage("Querying: https://sky.coflnet.com/api/item/price/$tag/current")
        val response = HttpClient.sendRequest("https://sky.coflnet.com/api/item/price/$tag/current")
        return gson.fromJson(response, JsonObject::class.java).get(key).asDouble
    }
}