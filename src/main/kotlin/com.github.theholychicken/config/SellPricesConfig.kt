package com.github.theholychicken.config

import com.github.theholychicken.GoodMod
import java.io.File
import com.github.theholychicken.GoodMod.Companion.mc
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Config backend for the sell pricing of bz items
 * saves item together with whether it should use sell offer or instasell
 *
 * @property sellPrices Given any item i, sellPrices.get(i) is true if i should use sell offers, and false otherwise
 */
object SellPricesConfig {

    var sellPrices: MutableMap<String, Boolean> = mutableMapOf()
    private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
    private val CONFIG_FILE = File(mc.mcDataDir, "config/goodmod/sellprices.json").apply {
        try {
            createNewFile()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun loadConfig() {
        try {
            with(CONFIG_FILE.bufferedReader().use { it.readText() }) {
                if (this == "") return

                sellPrices = GSON.fromJson(
                    this,
                    object : TypeToken<MutableMap<String, Boolean>>() {}.type
                )
            }

            if (sellPrices.keys.size == 0) {
                initConfig()
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun saveConfig() {
        try {
            CONFIG_FILE.bufferedWriter().use { it.write(GSON.toJson(sellPrices)) }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun initConfig() {
        /*
        After i redo the auction parser config into splitting ah and bz items, I can just do like
        AuctionParser.bazaarItems.keys.forEach { sellPrices[it] = false }
         */
        sellPrices["WITHER_BLOOD"] = false
        sellPrices["WITHER_CATALYST"] = false
        sellPrices["PRECURSOR_GEAR"] = false
        // 6
        sellPrices["GIANT_TOOTH"] = false
        sellPrices["SADAN_BROOCH"] = false
        // 5
        sellPrices["AOTE_STONE"] = false
        sellPrices["DARK_ORB"] = false
        // 4
        sellPrices["SPIRIT_WING"] = false
        sellPrices["SPIRIT_BONE"] = false
        sellPrices["SPIRIT_DECOY"] = false
        // 3
        sellPrices["SUSPICIOUS_VIAL"] = false
        // 2
        sellPrices["RED_SCARF"] = false
        // 1
        sellPrices["RED_NOSE"] = false
        // stars
        sellPrices["FIFTH_MASTER_STAR"] = false
        sellPrices["FOURTH_MASTER_STAR"] = false
        sellPrices["THIRD_MASTER_STAR"] = false
        sellPrices["SECOND_MASTER_STAR"] = false
        sellPrices["FIRST_MASTER_STAR"] = false
        // books
        sellPrices["ENCHANTMENT_THUNDERLORD_7"] = false
        sellPrices["ENCHANTMENT_OVERLOAD_1"] = false
        sellPrices["ENCHANTMENT_REJUVENATE_3"] = false
        sellPrices["ENCHANTMENT_REJUVENATE_2"] = false
        sellPrices["ENCHANTMENT_REJUVENATE_1"] = false
        sellPrices["ENCHANTMENT_FEATHER_FALLING_7"] = false
        sellPrices["ENCHANTMENT_FEATHER_FALLING_6"] = false
        sellPrices["ENCHANTMENT_INFINITE_QUIVER_7"] = false
        sellPrices["ENCHANTMENT_INFINITE_QUIVER_6"] = false
        sellPrices["ENCHANTMENT_LETHALITY_6"] = false
        // ults
        sellPrices["ENCHANTMENT_ULTIMATE_ONE_FOR_ALL_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_SOUL_EATER_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_SWARM_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_REND_2"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_REND_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_LEGION_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_LAST_STAND_2"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_LAST_STAND_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_WISE_2"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_WISE_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_WISDOM_2"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_WISDOM_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_BANK_3"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_BANK_2"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_BANK_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_2"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_COMBO_2"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_COMBO_1"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_JERRY_3"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_JERRY_2"] = false
        sellPrices["ENCHANTMENT_ULTIMATE_JERRY_1"] = false
        // universals
        sellPrices["RECOMBOBULATOR_3000"] = false
        sellPrices["FUMING_POTATO_BOOK"] = false
        sellPrices["HOT_POTATO_BOOK"] = false
        sellPrices["NECROMANCER_BROOCH"] = false
        // essence
        sellPrices["ESSENCE_WITHER"] = false
        sellPrices["ESSENCE_UNDEAD"] = false
        // chest key
        sellPrices["DUNGEON_CHEST_KEY"] = false
        saveConfig()
    }
}