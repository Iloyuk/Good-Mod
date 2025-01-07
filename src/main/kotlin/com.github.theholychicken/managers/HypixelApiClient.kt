package com.github.theholychicken.managers

import com.github.theholychicken.GoodMod
import com.github.theholychicken.config.GuiConfig.useSellOffer
import com.github.theholychicken.utils.modMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Client for managing Hypixel auctions API calls
 * @property fetchAllAuctions searches every auction and calls AuctionParser.updateAuction on each one if bin
 */
object HypixelApiClient {
    private val httpClient = OkHttpClient()
    private val gson = Gson()
    private var key = getKey()
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

    private fun getKey(): String {
        if (useSellOffer) {
            return "buy_summary"
        } else {
            return "sell_summary"
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
                throw Exception("Failed to fetch auctions")
                modMessage("Failed to fetch auctionhouse data. Please manually try refreshing with /goodmod:dev:updateauctions")
            }

            totalPages = jsonResponse.get("totalPages").asInt
            val auctions = jsonResponse.getAsJsonArray("auctions")

            // process auctions
            for (auction in auctions) {
                if (auction.asJsonObject.get("bin").asBoolean) {
                    val itemName = auction.asJsonObject.get("item_name").asString
                    val price = auction.asJsonObject.get("starting_bid").asDouble
                    AuctionParser.updateAuction(itemName, price)
                }
            }

            currentPage++
        } while (currentPage < totalPages)


        // Now parse bazaar
        val bazaarResponse = fetchBazaar()
        val bazaarJson = gson.fromJson(bazaarResponse, JsonObject::class.java)

        // Check for success
        if (!bazaarJson.get("success").asBoolean) {
            throw Exception("Failed to fetch bazaar data")
            modMessage("Failed to fetch bazaar data. Please manually try refreshing with /goodmod:dev:updateauctions")
        }

        // process bazaar
        val products = bazaarJson.getAsJsonObject("products")
        products.entrySet().forEach { (thingy, value) ->
            val itemName = value.asJsonObject.get("quick_status").asJsonObject.get("productId").asString
            val price = value.asJsonObject.get(key)
                ?.asJsonArray
                ?.firstOrNull()
                ?.asJsonObject
                ?.get("pricePerUnit")
                ?.asDouble ?: 0.00
            AuctionParser.updateBazaar(itemName, price)
        }

        AuctionParser.saveToFile()
        modMessage("Fetched updated auction prices.")
        GoodMod.logger.info("Fetched updated auction prices.")
    }

    private fun fetchPage(page: Int): String {
        val request = Request.Builder()
            .url("https://api.hypixel.net/v2/skyblock/auctions?page=$page")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to fetch auctions on page $page")
                modMessage("Failed to fetch auctions on page $page. Please manually retry fetching auctions with /goodmod:dev:updateauctions.")
            }
            return response.body?.string() ?: throw IOException("Empty response on page $page")
        }
    }

    private fun fetchBazaar(): String {
        val request = Request.Builder()
            .url("https://api.hypixel.net/v2/skyblock/bazaar")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Failed to load bazaar data.")
                modMessage("Failed to properly load bazaar data. Please manually try fetching data with /goodmod:dev:updateauctions.")
            }
            return response.body?.string() ?: throw IOException("Empty bazaar response")
        }
    }
}