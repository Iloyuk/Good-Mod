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

object CoflApiClient {
    private val gson = Gson()

    private fun fetchPrice(itemTag: String): JsonObject {
        val response = HttpClient.sendRequest("https://sky.coflnet.com/api/item/price/$itemTag/current")
        return gson.fromJson(response, JsonObject::class.java)
    }

    suspend fun fetchAllAuctions(): Unit = withContext(Dispatchers.IO) {
        for (floor in AuctionParser.items.values) {
            floor["auctions"]?.forEach { (tag, name) ->
                val price = fetchPrice(tag).get("buy").asDouble // the "sell" endpoint accounts for the 2% ah tax but not listing fees lolz
                AuctionParser.updateAuction(name, price)
            }
            floor["bazaar"]?.forEach { (tag, _) ->
                val price = fetchPrice(tag).get(getKeyString(tag)).asDouble
                AuctionParser.updateAuction(tag, price)
            }
        }
        modMessage("Successfully fetch all auctions.")
    }

    private fun getKeyString(tag: String): String =
        if (SellPricesConfig.sellPrices[tag] == true) "buy" else "sell"
}


/*
Auctionables lowest bin:
 - api/auctions/$itemTag/active/overview?orderBy=LOWEST_PRICE
    .asList[0].asJsonObject.get("price").asDouble
Bazaarables:
 - api/bazaar/$itemTag/snapshot
    .asJsonObject.get("buyPrice").asDouble <<- instabuy/sell offer
    .asJsonObject.get("sellPrice").asDouble <<- instasell/buy offer

can also do
/api/$itemTag/price/current.asJsonObject.get(when {
    if bazaarable and using sellOffers -> "buy"
    if bazaarable and using instasell -> "sell"
    if auctionable -> "buy" <<- the "sell" endpoint accounts for the 2% ah tax but not listing fees GG
})
 */