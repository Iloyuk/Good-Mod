package com.github.theholychicken.managers

import com.github.theholychicken.GoodMod
import java.io.File
import com.github.theholychicken.GoodMod.Companion.mc
import com.github.theholychicken.config.GuiConfig.useSellOffer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Object which contains parsed information about auction api pulls
 * @param auctionPrices contains current lowest ah prices, indexed by their name in itemDropPatterns minus the formatting stuff
 * @param items contains the list of unformatted items in itemDropPatterns
 * @param initFromFile pulls data from the saved file
 */
/*
 * TODO - need to make a gui. So the gui should implement a scanning protocol, and after it terminates it should pull auctionPrices from file: AuctionParser.initFromFile()
 * TODO - Still need to implement [when to kismet] and [when to pull second chest] logic, latter should be easy
 * TODO - final step is to render over the gui with the optimal looting tasks. I should also write a gui renderer for the outside croesus menu to note which are looted and which arent. That one should be toggleable.
 * TODO - implement an option in the gui to select or deselect "useSellOffer" which is in GuiConfig
 */
object AuctionParser {
    var auctionPrices: MutableMap<String, Double> = mutableMapOf()
    private val items = ItemDropParser.getModifiedKeys()
    private var bazaarItems = bazaarItems()
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val PRICES_FILE = File(mc.mcDataDir, "config/goodmod/prices.json").apply {
        try {
            createNewFile()
        } catch (e: Exception) {
            print(e.message)
        }
    }

    // updates an entry as an auction thingy
    fun updateAuction(item: String, price: Double) {
        if (item !in items) return

        val auctionItemPrice = auctionPrices[item] ?: Double.MAX_VALUE

        if ((item in auctionPrices.keys && price < auctionItemPrice) || item !in auctionPrices.keys)
            auctionPrices[item] = price
    }

    // updates an entry using bazaar data
    fun updateBazaar(item: String, price: Double) {
        if (item !in bazaarItems.keys) return

        val itemName = bazaarItems[item] ?: return

        val bazaarItemPrice = auctionPrices[itemName] ?: Double.MAX_VALUE

        val multi = if (useSellOffer) 0.9875F else 1F
        if ((itemName in auctionPrices && (price * multi) < bazaarItemPrice) || itemName !in auctionPrices) {
            auctionPrices[itemName] = (price * multi)
        }
    }

    // saves to the file
    fun saveToFile() {
        try {
            PRICES_FILE.writeText(gson.toJson(auctionPrices))
            GoodMod.logger.info("Auction prices successfully saved")
        } catch (e: Exception) {
            GoodMod.logger.info("Error loading auction prices: ${e.message}")
        }
    }

    // pulls data from the file
    fun initFromFile() {
        try {
            with(PRICES_FILE.bufferedReader().use { it.readText() }) {
                if (this == "") return

                auctionPrices = gson.fromJson(
                    this,
                    object  : TypeToken<MutableMap<String, Double>>() {}.type
                )
            }
        } catch (e: Exception) {
            print(e.message)
        }
    }

    // this doesnt exist
    private fun bazaarItems(): MutableMap<String, String> {
        val list = mutableMapOf<String, String>()
        list["IMPLOSION_SCROLL"] = "Implosion"
        list["SHADOW_WARP_SCROLL"] = "Shadow Warp"
        list["WITHER_SHIELD_SCROLL"] = "Wither Shield"
        list["WITHER_BLOOD"] = "Wither Blood"
        list["WITHER_CATALYST"] = "Wither Catalyst"
        list["PRECURSOR_GEAR"] = "Precursor Gear"
        list["GIANT_TOOTH"] = "Giant Tooth"
        list["SADAN_BROOCH"] = "Sadan's Brooch"
        list["AOTE_STONE"] = "Warped Stone"
        list["DARK_ORB"] = "Dark Orb"
        list["SPIRIT_WING"] = "Spirit Wing"
        list["SPIRIT_BONE"] = "Spirit Bone"
        list["SPIRIT_DECOY"] = "Spirit Stone"
        list["SUSPICIOUS_VIAL"] = "Suspicious Vial"
        list["RED_SCARF"] = "Red Scarf"
        list["RED_NOSE"] = "Red Nose"
        list["FIFTH_MASTER_STAR"] = "Fifth Master Star"
        list["FOURTH_MASTER_STAR"] = "Fourth Master Star"
        list["THIRD_MASTER_STAR"] = "Third Master Star"
        list["SECOND_MASTER_STAR"] = "Second Master Star"
        list["FIRST_MASTER_STAR"] = "First Master Star"
        list["ENCHANTMENT_THUNDERLORD_7"] = "Thunderlord VII"
        list["ENCHANTMENT_OVERLOAD_1"] = "Overload I"
        list["ENCHANTMENT_REJUVENATE_3"] = "Rejuvenate III"
        list["ENCHANTMENT_REJUVENATE_2"] = "Rejuvenate II"
        list["ENCHANTMENT_REJUVENATE_1"] = "Rejuvenate I"
        list["ENCHANTMENT_FEATHER_FALLING_7"] = "Feather Falling VII"
        list["ENCHANTMENT_FEATHER_FALLING_6"] = "Feather Falling VI"
        list["ENCHANTMENT_INFINITE_QUIVER_7"] = "Infinite Quiver VII"
        list["ENCHANTMENT_INFINITE_QUIVER_6"] = "Infinite Quiver VI"
        list["ENCHANTMENT_LETHALITY_6"] = "Lethality VI"
        list["ENCHANTMENT_ULTIMATE_ONE_FOR_ALL_1"] = "One For All I"
        list["ENCHANTMENT_ULTIMATE_SOUL_EATER_1"] = "Soul Eater I"
        list["ENCHANTMENT_ULTIMATE_SWARM_1"] = "Swarm I"
        list["ENCHANTMENT_ULTIMATE_REND_2"] = "Rend II"
        list["ENCHANTMENT_ULTIMATE_REND_1"] = "Rend I"
        list["ENCHANTMENT_ULTIMATE_LEGION_1"] = "Legion I"
        list["ENCHANTMENT_ULTIMATE_LAST_STAND_2"] = "Last Stand II"
        list["ENCHANTMENT_ULTIMATE_LAST_STAND_1"] = "Last Stand I"
        list["ENCHANTMENT_ULTIMATE_WISE_2"] = "Ultimate Wise II"
        list["ENCHANTMENT_ULTIMATE_WISE_1"] = "Ultimate Wise I"
        list["ENCHANTMENT_ULTIMATE_WISDOM_2"] = "Wisdom II"
        list["ENCHANTMENT_ULTIMATE_WISDOM_1"] = "Wisdom I"
        list["ENCHANTMENT_ULTIMATE_BANK_3"] = "Bank III"
        list["ENCHANTMENT_ULTIMATE_BANK_2"] = "Bank II"
        list["ENCHANTMENT_ULTIMATE_BANK_1"] = "Bank I"
        list["ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_2"] = "No Pain No Gain II"
        list["ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_1"] = "No Pain No Gain I"
        list["ENCHANTMENT_ULTIMATE_COMBO_2"] = "Combo II"
        list["ENCHANTMENT_ULTIMATE_COMBO_1"] = "Combo I"
        list["ENCHANTMENT_ULTIMATE_JERRY_3"] = "Ultimate Jerry III"
        list["ENCHANTMENT_ULTIMATE_JERRY_2"] = "Ultimate Jerry II"
        list["ENCHANTMENT_ULTIMATE_JERRY_1"] = "Ultimate Jerry I"
        list["RECOMBOBULATOR_3000"] = "Recombobulator 3000"
        list["FUMING_POTATO_BOOK"] = "Fuming Potato Book"
        list["HOT_POTATO_BOOK"] = "Hot Potato Book"
        list["NECROMANCER_BROOCH"] = "Necromancer's Brooch"
        list["ESSENCE_WITHER"] = "Wither Essence"
        list["ESSENCE_UNDEAD"] = "Undead Essence"
        list["DUNGEON_CHEST_KEY"] = "Dungeon Chest Key"
        return list
    }
}