package com.github.theholychicken.managers.apiclients

import com.github.theholychicken.GoodMod
import com.github.theholychicken.config.GuiConfig.useSellOffer
import com.github.theholychicken.config.SellPricesConfig
import com.github.theholychicken.managers.AuctionParser
import com.github.theholychicken.utils.modMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Client for managing Hypixel auctions API calls
 * @property fetchAllAuctions searches every auction and calls AuctionParser.updateAuction on each one if bin
 */
object HypixelApiClient {
    private val gson = Gson()
    private var key = "owo" //getKey()
    val executor = Executors.newSingleThreadScheduledExecutor()

    fun scheduleHypixelApiPulls() {
        executor.scheduleAtFixedRate({
            runBlocking {
                try {
                    GoodMod.logger.info("Beginning auction fetch")
                    fetchAllAuctions()
                } catch (e: Exception) {
                    GoodMod.logger.error("Exception occured: $e")
                }
            }
        }, 0, 30, TimeUnit.MINUTES)
    }

    private fun getKey(item: String): String {
        if (SellPricesConfig.sellPrices[item] == true) {
            return "buy_summary" // using sell offers
        } else {
            return "sell_summary" // using instasell
        }
    }

    suspend fun fetchAllAuctions(): Unit = withContext(Dispatchers.IO) {
        AuctionParser.auctionPrices.clear()
        // First parse auctions
        var currentPage = 0
        var totalPages: Int
        do {
            val response = fetchPage(currentPage)
            val jsonResponse = gson.fromJson(response, JsonObject::class.java)

            // check for success
            if (!jsonResponse.get("success").asBoolean) {
                modMessage("Failed to fetch ah data. Please manually try refreshing with /goodmod:dev:updateauctions")
                throw Exception("Failed to fetch auctions")
            }

            totalPages = jsonResponse.get("totalPages").asInt
            val auctions = jsonResponse.getAsJsonArray("auctions")

            // process auctions
            for (auction in auctions) {
                if (auction.asJsonObject.get("bin").asBoolean) {
                    val itemName = auction.asJsonObject.get("item_name").asString
                    val price = auction.asJsonObject.get("starting_bid").asDouble
                    if (itemName == "[Lvl 1] Spirit") {
                        AuctionParser.updateAuction(itemName, price,
                            auction.asJsonObject.get("tier").asString)
                    } else {
                        AuctionParser.updateAuction(itemName, price)
                    }
                }
            }

            currentPage++
        } while (currentPage < totalPages)


        // Now parse bazaar
        val bazaarResponse = fetchBazaar()
        val bazaarJson = gson.fromJson(bazaarResponse, JsonObject::class.java)

        // Check for success
        if (!bazaarJson.get("success").asBoolean) {
            modMessage("Failed to fetch bazaar data. Please manually try refreshing with /goodmod:dev:updateauctions")
            throw Exception("Failed to fetch bazaar data")
        }

        // process bazaar
        val products = bazaarJson.getAsJsonObject("products")
        products.entrySet().forEach { (_, value) ->
            val itemName = value.asJsonObject.get("quick_status").asJsonObject.get("productId").asString
            try {
                val price = value.asJsonObject.get(getKey(itemName))
                    ?.asJsonArray
                    ?.firstOrNull()
                    ?.asJsonObject
                    ?.get("pricePerUnit")
                    ?.asDouble ?: 0.00
                AuctionParser.updateBazaar(itemName, price)
            } catch (_: Exception) {
                modMessage("Failed to update price for bazaar item: $itemName")
            }
        }

        AuctionParser.saveToFile()
        modMessage("Fetched updated auction prices.")
        GoodMod.logger.info("Fetched updated auction prices.")
    }

    private fun fetchPage(page: Int): String {
        return HttpClient.sendRequest("https://api.hypixel.net/v2/skyblock/auctions?page=$page")
    }

    private fun fetchBazaar(): String {
        return HttpClient.sendRequest("https://api.hypixel.net/v2/skyblock/bazaar")
    }
}