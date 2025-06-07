package com.github.theholychicken.managers.apiclients

import com.github.theholychicken.config.GuiConfig
import com.github.theholychicken.config.SellPricesConfig
import com.github.theholychicken.managers.AuctionParser
import com.github.theholychicken.utils.Auction
import com.github.theholychicken.utils.modMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// trying to parse each item individually gives errors, so we parse the entire
// neu prices endpoint for relevant items where possible

object CoflApiClient {
    private val gson = Gson()

    // use this function sparingly to prevent too many requests errors
    private fun fetchPrice(itemTag: String): JsonObject {
        val response = HttpClient.sendRequest("https://sky.coflnet.com/api/item/price/$itemTag/current")
        return gson.fromJson(response, JsonObject::class.java)
    }

    // using neu endpoint where possible to prevent too mant request errors
    // unfortunately this endpoint is trash its much less accurate afaik
    private fun fetchAllPrices(): JsonObject {
        val response = HttpClient.sendRequest("https://sky.coflnet.com/api/prices/neu")
        return gson.fromJson(response, JsonObject::class.java)
    }

    suspend fun fetchAllAuctions(): Unit = withContext(Dispatchers.IO) {
        val jsonData = fetchAllPrices()

        for (floor in AuctionParser.items.values) {
            floor["auctions"]?.forEach { (tag, name) ->
                when {
                    tag == "GOLDOR_THE_FISH" || tag == "MAXOR_THE_FISH" -> {
                        // api doesnt account for these???
                        AuctionParser.updateAuction(name, fetchPrice(tag).get("buy").asDouble)
                    }
                    tag == "PET-SPIRIT-LEGENDARY" -> {
                        // api doesnt check spirit pet rarity so we do it thru tricked
                        AuctionParser.updateAuction(name, TrickedApiClient.fetchPrice(tag), "LEGENDARY")
                    }
                    tag == "PET-SPIRIT-EPIC" -> {
                        AuctionParser.updateAuction(name, TrickedApiClient.fetchPrice(tag), "EPIC")
                    }
                    jsonData.has(tag) -> {
                        AuctionParser.updateAuction(name, jsonData.get(tag).asDouble)
                    }
                    else -> {
                        modMessage("Auction not found: ($tag, $name)")
                        AuctionParser.updateAuction(name, 0.0)
                    }
                }
            }
            floor["bazaar"]?.forEach { (tag, _) ->
                modMessage("Fetching auctions $tag")
                val price = fetchPrice(tag).get(getKeyString(tag)).asDouble
                AuctionParser.updateBazaar(tag, price)
            }
        }

        AuctionParser.saveToFile()
        modMessage("Successfully fetched all auctions.")
    }

    private fun getKeyString(tag: String): String =
        if (SellPricesConfig.sellPrices[tag] == true) "buy" else "sell"
}