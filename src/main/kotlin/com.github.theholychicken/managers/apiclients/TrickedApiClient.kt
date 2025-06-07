package com.github.theholychicken.managers.apiclients

import com.github.theholychicken.managers.AuctionParser
import com.github.theholychicken.utils.modMessage
import com.google.gson.Gson
import com.google.gson.JsonObject

object TrickedApiClient {
    private val gson = Gson()

    fun fetchPrice(itemTag: String): Double {
        val response = HttpClient.sendRequest("https://lb.tricked.pro/lowestbin/$itemTag")
        return response.toDouble()
    }

    private fun fetchAllPrices(): JsonObject {
        val response = HttpClient.sendRequest("https://lb.tricked.pro/lowestbins")
        return gson.fromJson(response, JsonObject::class.java)
    }

    // to minimize api calls to reduce strain on tricked
    // we use lowestbins and scan the json instead of queuing
    // each item individually
    fun fetchAllAuctions() {
        val jsonData = fetchAllPrices()

        AuctionParser.items.values.forEach { floor ->
            floor["auctions"]?.forEach{ (tag, name) ->
                when (tag) {
                    "PET-SPIRIT-EPIC" -> {
                        AuctionParser.updateAuction(name, jsonData.get(tag).asDouble, "EPIC")
                    }
                    "PET-SPIRIT-LEGENDARY" -> {
                        AuctionParser.updateAuction(name, jsonData.get(tag).asDouble, "LEGENDARY")
                    }
                    else -> {
                        AuctionParser.updateAuction(name, jsonData.get(tag).asDouble)
                    }
                }
            }
            floor["bazaar"]?.forEach { (tag, _) ->
                val itemTag = tag.replace(Regex("^ENCHANTMENT_(.*)_(\\d)$"), "ENCHANTED_BOOK-$1-$2")
                // this api stores enchanted books with a slitghtly diff id
                try {
                    AuctionParser.updateBazaar(tag, jsonData.get(itemTag).asDouble)
                } catch (e: Exception) {
                    modMessage("Parsing error on $itemTag: ${e.message}")
                }
            }
        }

        AuctionParser.saveToFile()
        modMessage("Successfully fetched all auctions.")
    }
}