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
 * Should only be used for Hypixel Api pulls
 * @param auctionPrices contains current lowest ah prices, indexed by their name in itemDropPatterns minus the formatting stuff
 * @param items contains the list of unformatted items in itemDropPatterns
 * @param initFromFile pulls data from the saved file
 */
/*
 * TODO
 *  I need to check to figure out how this shit is actually working
 *  Tidy up any logical errors then see if i can clean up any code
 *  Render over max profit [WIP, renders one to the right but only sometimes?]
 *  [ If 5 chests it renders correct, if 6 it renders 1 to the left. but im doing it with slot #s?]
 *  Implement COFL, Tricked
 *  Fix key calculations/reading
 *  Ensure spirit pet compatability
 *  Implement kismet logic and renders
 *  Improve hud information
 *  Impement custom no-skip items
 *  Implement items to choose to sell offer, and items to choose to instasell
 *  Implement items to choose to calc as combined books rather than uncombined
 */
object AuctionParser {
    var auctionPrices: MutableMap<String, Double> = mutableMapOf()
    //private val items = ItemDropParser.getModifiedKeys()
    var items = itemMap()
    var shinyItems = mutableListOf<String>(
        "Shiny Necron's Handle",
        "Shiny Wither Helmet",
        "Shiny Wither Chestplate",
        "Shiny Wither Leggings",
        "Shiny Wither Boots",
    )
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val PRICES_FILE = File(mc.mcDataDir, "config/goodmod/prices.json").apply {
        try {
            createNewFile()
        } catch (e: Exception) {
            print(e.message)
        }
    }

    // updates an entry as an auction thingy
    fun updateAuction(item: String, price: Double, rarity: String = "") {
        if (item !in items.values && item !in shinyItems) return

        // have to check for spirit pet rarity
        var itemName = item
        if (rarity != "") {
            if (rarity == "EPIC") {
                itemName = "[Lvl 1] §6Spirit"
            } else if (rarity == "LEGENDARY") {
                itemName = "[Lvl 1] §5Spirit"
            }
        }

        val auctionItemPrice = auctionPrices[itemName] ?: Double.MAX_VALUE

        if ((itemName in auctionPrices.keys && price < auctionItemPrice) || itemName !in auctionPrices.keys)
            auctionPrices[itemName] = price
    }

    // updates an entry using bazaar data
    fun updateBazaar(item: String, price: Double) {
        if (item !in items.keys) return

        val itemName = items[item] ?: return

        val multi = if (useSellOffer) 0.9875F else 1F
        auctionPrices[itemName] = (price * multi)
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
    private fun itemMap(): MutableMap<String, String> {
        val list = mutableMapOf<String, String>()
        // 7
        list["IMPLOSION_SCROLL"] = "Implosion"
        list["SHADOW_WARP_SCROLL"] = "Shadow Warp"
        list["WITHER_SHIELD_SCROLL"] = "Wither Shield"
        list["DARK_CLAYMORE"] = "Dark Claymore"
        list["DYE_NECRON"] = "Necron Dye"
        list["WITHER_HELMET"] = "Wither Helmet"
        list["WITHER_CHESTPLATE"] = "Wither Chestplate"
        list["WITHER_LEGGINGS"] = "Wither Leggings"
        list["WITHER_BOOTS"] = "Wither Boots"
        list["AUTO_RECOMBOBULATOR"] = "Auto Recombobulator"
        list["WITHER_CLOAK"] = "Wither Cloak Sword"
        list["STORM_THE_FISH"] = "Storm the Fish"
        list["GOLDOR_THE_FISH"] = "Goldor the Fish"
        list["MAXOR_THE_FISH"] = "Maxor the Fish"
        list["WITHER_BLOOD"] = "Wither Blood"
        list["WITHER_CATALYST"] = "Wither Catalyst"
        list["PRECURSOR_GEAR"] = "Precursor Gear"
        // 6
        list["GIANTS_SWORD"] = "Giant's Sword"
        list["PRECURSOR_EYE"] = "Precursor Eye"
        list["FEL_SKULL"] = "Fel Skull"
        list["SOULWEAVER_GLOVES"] = "Soulweaver Gloves"
        list["SUMMONING_RING"] = "Summoning Ring"
        list["NECROMANCER_LORD_HELMET"] = "Necromancer Lord Helmet"
        list["NECROMANCER_LORD_CHESTPLATE"] = "Necromancer Lord Chestplate"
        list["NECROMANCER_LORD_LEGGINGS"] = "Necromancer Lord Leggings"
        list["NECROMANCER_LORD_BOOTS"] = "Necromancer Lord Boots"
        list["NECROMANCER_SWORD"] = "Necromancer Sword"
        list["GIANT_TOOTH"] = "Giant Tooth"
        list["SADAN_BROOCH"] = "Sadan's Brooch"
        // 5
        list["SHADOW_FURY"] = "Shadow Fury"
        list["LAST_BREATH"] = "Last Breath"
        list["LIVID_DAGGER"] = "Livid Dagger"
        list["SHADOW_ASSASSIN_HELMET"] = "Shadow Assassin Helmet"
        list["SHADOW_ASSASSIN_CHESTPLATE"] = "Shadow Assassin Chestplate"
        list["SHADOW_ASSASSIN_LEGGINGS"] = "Shadow Assassin Leggings"
        list["SHADOW_ASSASSIN_BOOTS"] = "Shadow Assassin Boots"
        list["SHADOW_ASSASSIN_CLOAK"] = "Shadow Assassin Cloak"
        list["AOTE_STONE"] = "Warped Stone"
        list["DARK_ORB"] = "Dark Orb"
        // 4
        list["PET-SPIRIT-LEGENDARY"] = "[Lvl 1] §6Spirit"
        list["PET-SPIRIT-EPIC"] = "[Lvl 1] §5Spirit"
        list["THORNS_BOOTS"] = "Spirit Boots"
        list["ITEM_SPIRIT_BOW"] = "Spirit Bow"
        list["SPIRIT_SWORD"] = "Spirit Sword"
        list["SPIRIT_WING"] = "Spirit Wing"
        list["SPIRIT_BONE"] = "Spirit Bone"
        list["SPIRIT_DECOY"] = "Spirit Stone"
        // 3
        list["SUSPICIOUS_VIAL"] = "Suspicious Vial"
        list["ADAPTIVE_HELMET"] = "Adaptive Helmet"
        list["ADAPTIVE_CHESTPLATE"] = "Adaptive Chestplate"
        list["ADAPTIVE_LEGGINGS"] = "Adaptive Leggings"
        list["ADAPTIVE_BOOTS"] = "Adaptive Boots"
        // 2
        list["RED_SCARF"] = "Red Scarf"
        list["STONE_BLADE"] = "Adaptive Blade"
        list["ADAPTIVE_BELT"] = "Adaptive Belt"
        list["SCARF_STUDIES"] = "Scarf's Studies"
        // 1
        list["RED_NOSE"] = "Red Nose"
        list["BONZO_MASK"] = "Bonzo's Mask"
        list["BONZO_STAFF"] = "Bonzo's Staff"
        list["BALLOON_SNAKE"] = "Balloon Snake"
        // stars
        list["FIFTH_MASTER_STAR"] = "Fifth Master Star"
        list["FOURTH_MASTER_STAR"] = "Fourth Master Star"
        list["THIRD_MASTER_STAR"] = "Third Master Star"
        list["SECOND_MASTER_STAR"] = "Second Master Star"
        list["FIRST_MASTER_STAR"] = "First Master Star"
        // skulls
        list["MASTER_SKULL_TIER_5"] = "Master Skull - Tier 5"
        list["MASTER_SKULL_TIER_4"] = "Master Skull - Tier 4"
        list["MASTER_SKULL_TIER_3"] = "Master Skull - Tier 3"
        list["MASTER_SKULL_TIER_2"] = "Master Skull - Tier 2"
        list["MASTER_SKULL_TIER_1"] = "Master Skull - Tier 1"
        // books
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
        // ults
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
        // unniversals
        list["RECOMBOBULATOR_3000"] = "Recombobulator 3000"
        list["FUMING_POTATO_BOOK"] = "Fuming Potato Book"
        list["HOT_POTATO_BOOK"] = "Hot Potato Book"
        list["NECROMANCER_BROOCH"] = "Necromancer's Brooch"
        // essence
        list["ESSENCE_WITHER"] = "Wither Essence"
        list["ESSENCE_UNDEAD"] = "Undead Essence"
        // chest key
        list["DUNGEON_CHEST_KEY"] = "Dungeon Chest Key"
        return list
    }
}