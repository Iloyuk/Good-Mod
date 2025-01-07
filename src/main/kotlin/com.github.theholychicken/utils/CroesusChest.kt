package com.github.theholychicken.utils

import com.github.theholychicken.managers.AuctionParser
import com.github.theholychicken.managers.HypixelApiClient

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
    val cost: Double
) {
    val profit: Double = calculateProfit()

    private fun calculateProfit(): Double {
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

                else -> {
                    val key = it.substring(it.lastIndexOf("§") + 2)
                    auctionPrices[key] ?: run {
                        modMessage("Item not found in auction/bazaar prices: §6$key §4§l(Please report this)")
                        0.0
                    }
                }
            }
        } - cost
    }
}
