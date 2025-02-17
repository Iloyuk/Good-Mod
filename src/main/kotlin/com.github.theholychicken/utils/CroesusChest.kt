package com.github.theholychicken.utils

import com.github.theholychicken.config.GuiConfig
import com.github.theholychicken.managers.AuctionParser
import com.github.theholychicken.managers.apiclients.CoflApiClient
import com.github.theholychicken.managers.apiclients.TrickedApiClient

/**
 * Represents a Croesus chest with details about its contents, purchase status, cost, and profit.
 *
 * @property name The display name of the chest.
 * @property items The list of items in the chest.
 * @property purchased Whether the chest has been purchased.
 * @property cost The cost of the chest.
 * @property profit The calculated profit from the chest.
 */
class CroesusChest(
    val name: String,
    val items: List<String>,
    val purchased: Boolean,
    val cost: Double,
    val location: Pair<Int, Int>
) {
    val profit: Double = when (GuiConfig.api){
        "HypixelApi" -> calculateProfitHypixel()
        "CoflApi" -> calculateProfitCofl()
        "TrickedApi" -> calculateProfitTricked()
        else -> calculateProfitHypixel()
    }
    private val itemTags = thing()
    // this is going to break, i need to make it work
    fun thing(): MutableList<String> {
        val returnList = mutableListOf<String>()
        items.forEach {
            when {
                it.substring(2) in AuctionParser.shinyItems -> {
                    returnList.add(it.substring(5))
                }
                it.matches(Regex("§dWither Essence §8x\\d+")) -> {
                    returnList.add("WITHER_ESSENCE")
                }

                it.matches(Regex("§dUndead Essence §8x\\d+")) -> {
                    returnList.add("UNDEAD_ESSENCE")
                }

                it.matches(Regex("§7[Lvl 1] §\\dSpirit")) -> {
                    if (Regex("(\\d)").find(it)?.groupValues?.get(3)?.toIntOrNull() == 6) {
                        returnList.add("PET-SPIRIT-LEGENDARY")
                    } else {
                        returnList.add("PET-SPIRIT-EPIC")
                    }
                }

                else -> {
                    AuctionParser.items.entries.find { (key, value) ->
                        it.substringAfterLast("§") == value
                    }
                }
            }
        }
        return returnList
    }

    private fun calculateProfitHypixel(): Double {
        val auctionPrices = AuctionParser.auctionPrices.takeIf { it.isNotEmpty() } ?: run {
            modMessage("Auction/Bazaar prices are empty or null! Try manually refreshing with /updateauctions. If this does not fix the issue, please open an issue on the github.")
            return -cost
        }

        if (purchased) return 0.00

        return items.sumOf {
            when {
                it.matches(Regex("§dWither Essence §8x\\d+")) -> {
                    val price = auctionPrices["Wither Essence"] ?: 0.0
                    val quantity = Regex("(\\d+)$").find(it)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    quantity * price
                }

                it.matches(Regex("§dUndead Essence §8x\\d+")) -> {
                    val price = auctionPrices["Undead Essence"] ?: 0.0
                    val quantity = Regex("(\\d+)$").find(it)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    quantity * price
                }

                it.matches(Regex("§7[Lvl 1] §\\dSpirit")) -> {
                    val key = it.substring(2)
                    auctionPrices[key] ?: run {
                        modMessage("Spirit pet failed")
                        0.00
                    }
                }

                else -> {
                    val key = it.substring(it.lastIndexOf("§") + 2)
                    auctionPrices[key] ?: run {
                        modMessage("Item not found in auction/bazaar prices: §6$key §4§l(Please report this)")
                        0.00
                    }
                }
            }
        } - cost
    }

    private fun calculateProfitCofl(): Double {
        if (purchased) return 0.00

        return itemTags.sumOf {
            CoflApiClient.fetchPrice(it)
        }
    }

    private fun calculateProfitTricked(): Double {
        if (purchased) return 0.00

        return itemTags.sumOf {
            TrickedApiClient.fetchPrice(it)
        }
    }
}
