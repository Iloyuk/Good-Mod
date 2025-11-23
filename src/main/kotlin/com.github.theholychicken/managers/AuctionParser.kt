package com.github.theholychicken.managers

import com.github.theholychicken.GoodMod
import java.io.File
import com.github.theholychicken.GoodMod.Companion.mc
import com.github.theholychicken.config.GuiConfig.useSellOffer
import com.github.theholychicken.config.SellPricesConfig
import com.github.theholychicken.utils.modMessage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Object which contains parsed information about auction api pulls
 * Should only be used for Hypixel Api pulls
 * @property auctionPrices contains current lowest ah prices, indexed by their name in itemDropPatterns minus the formatting stuff
 * @property items contains the list of unformatted items in itemDropPatterns
 * @property initFromFile pulls data from the saved file
 */
object AuctionParser {
    var auctionPrices: MutableMap<String, Double> = mutableMapOf()
    //private val items = ItemDropParser.getModifiedKeys()
    var items = itemMap()
    var shinyItems = mutableListOf(
        "Shiny Necron's Handle",
        "Shiny Wither Helmet",
        "Shiny Wither Chestplate",
        "Shiny Wither Leggings",
        "Shiny Wither Boots",
    )
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    // pretty sure this stuff is saved in memory anyways i should prolly delete this crap
    private val PRICES_FILE = File(mc.mcDataDir, "config/goodmod/prices.json").apply {
        try {
            createNewFile()
        } catch (e: Exception) {
            print(e.message)
        }
    }

    // updates an entry as an auction thingy
    // TODO: make it account for listing fees and taxes
    fun updateAuction(item: String, price: Double, rarity: String = "") {
        if (!isAuctionable(item) && item !in shinyItems) return

        // have to check for spirit pet rarity
        var itemName = item
        if (rarity != "") {
            if (rarity == "EPIC") {
                itemName = "[Lvl 1] §6Spirit"
            } else if (rarity == "LEGENDARY") {
                itemName = "[Lvl 1] §5Spirit"
            }
        }

        auctionPrices[itemName] = auctionPriceLogic(price)
    }

    // updates an entry using bazaar data
    fun updateBazaar(item: String, price: Double) {
        val itemName = isBazaarable(item) ?: return

        val multi = if (SellPricesConfig.sellPrices[item] == true) 0.9875F else 1F // tax
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

    fun toKey(itemName: String): String? {
        items.values.forEach { categoryMap ->
            categoryMap.values.forEach { itemsMap ->
                itemsMap.entries.find { (_, name) -> name == itemName.substringAfterLast("§") }?.let {
                    return it.key
                }
            }
        }

        return null
    }

    private fun isAuctionable(item: String): Boolean {
        return items.values.any { categories ->
            categories["auctions"]?.containsValue(item) == true
        }
    }

    private fun isBazaarable(item: String): String? {
        for (category in items.values) {
            category["bazaar"]?.let { bazaar ->
                if (bazaar.containsKey(item)) {
                    return bazaar[item]
                }
            }
        }

        return null
    }

    private fun auctionPriceLogic(price: Double): Double {
        // claim tax
        var modifiedPrice: Double = price - (price * 0.01)

        // listing tax
        when {
            price <= 9999999.0 -> {
                //modMessage("price $price satisfies 1 <= $price <= 9,999,999")
                modifiedPrice -= (price * 0.01)
            }
            price <= 99999999.0 -> {
                //modMessage("price $price  satisfies 10,000,000 <= $price <= 99,999,999")
                modifiedPrice -= (price * 0.02)
            }
            price >= 100000000 -> {
                //modMessage("price $price satisfies 100,000,000 <= $price")
                modifiedPrice -= (price * 0.025)
            }
        }

        return modifiedPrice
    }

    // this doesnt exist
    private fun itemMap(): MutableMap<String, MutableMap<String, MutableMap<String, String>>> {

        // declaration method for AuctionParser.items
        // maps like floor -> ah/bz -> <itemID |-> itemName>
        val catalog = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()
        listOf(
            "floor_7", "floor_6", "floor_5", "floor_4", "floor_3", "floor_2", "floor_1",
            "enchs", "ults", "misc"
        ).forEach { key ->
            catalog[key] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        }

        // Floor 7 - Auctions
        catalog["floor_7"]?.get("auctions")?.apply {
            put("NECRON_HANDLE", "Necron's Handle")
            put("DARK_CLAYMORE", "Dark Claymore")
            put("DYE_NECRON", "Necron Dye")
            put("WITHER_HELMET", "Wither Helmet")
            put("WITHER_CHESTPLATE", "Wither Chestplate")
            put("WITHER_LEGGINGS", "Wither Leggings")
            put("WITHER_BOOTS", "Wither Boots")
            put("AUTO_RECOMBOBULATOR", "Auto Recombobulator")
            put("WITHER_CLOAK", "Wither Cloak Sword")
            put("STORM_THE_FISH", "Storm the Fish")
            put("GOLDOR_THE_FISH", "Goldor the Fish")
            put("MAXOR_THE_FISH", "Maxor the Fish")
        }

        // Floor 7 - Bazaar
        catalog["floor_7"]?.get("bazaar")?.apply {
            put("IMPLOSION_SCROLL", "Implosion")
            put("SHADOW_WARP_SCROLL", "Shadow Warp")
            put("WITHER_SHIELD_SCROLL", "Wither Shield")
            put("WITHER_BLOOD", "Wither Blood")
            put("WITHER_CATALYST", "Wither Catalyst")
            put("PRECURSOR_GEAR", "Precursor Gear")
            put("FIFTH_MASTER_STAR", "Fifth Master Star")
        }

        // Floor 6 - Auctions
        catalog["floor_6"]?.get("auctions")?.apply {
            put("GIANTS_SWORD", "Giant's Sword")
            put("PRECURSOR_EYE", "Precursor Eye")
            put("FEL_SKULL", "Fel Skull")
            put("SOULWEAVER_GLOVES", "Soulweaver Gloves")
            put("SUMMONING_RING", "Summoning Ring")
            put("NECROMANCER_LORD_HELMET", "Necromancer Lord Helmet")
            put("NECROMANCER_LORD_CHESTPLATE", "Necromancer Lord Chestplate")
            put("NECROMANCER_LORD_LEGGINGS", "Necromancer Lord Leggings")
            put("NECROMANCER_LORD_BOOTS", "Necromancer Lord Boots")
            put("NECROMANCER_SWORD", "Necromancer Sword")
        }

        // Floor 6 - Bazaar
        catalog["floor_6"]?.get("bazaar")?.apply {
            put("GIANT_TOOTH", "Giant Tooth")
            put("SADAN_BROOCH", "Sadan's Brooch")
            put("FOURTH_MASTER_STAR", "Fourth Master Star")
        }

        // Floor 5 - Auctions
        catalog["floor_5"]?.get("auctions")?.apply {
            put("SHADOW_FURY", "Shadow Fury")
            put("LAST_BREATH", "Last Breath")
            put("LIVID_DAGGER", "Livid Dagger")
            put("SHADOW_ASSASSIN_HELMET", "Shadow Assassin Helmet")
            put("SHADOW_ASSASSIN_CHESTPLATE", "Shadow Assassin Chestplate")
            put("SHADOW_ASSASSIN_LEGGINGS", "Shadow Assassin Leggings")
            put("SHADOW_ASSASSIN_BOOTS", "Shadow Assassin Boots")
            put("SHADOW_ASSASSIN_CLOAK", "Shadow Assassin Cloak")
        }

        // Floor 5 - Bazaar
        catalog["floor_5"]?.get("bazaar")?.apply {
            put("AOTE_STONE", "Warped Stone")
            put("DARK_ORB", "Dark Orb")
            put("THIRD_MASTER_STAR", "Third Master Star")
        }

        // Floor 4 - Auctions
        catalog["floor_4"]?.get("auctions")?.apply {
            put("PET-SPIRIT-LEGENDARY", "[Lvl 1] §6Spirit")
            put("PET-SPIRIT-EPIC", "[Lvl 1] §5Spirit")
            put("THORNS_BOOTS", "Spirit Boots")
            put("ITEM_SPIRIT_BOW", "Spirit Shortbow")
            put("SPIRIT_SWORD", "Spirit Sword")
        }

        // Floor 4 - Bazaar
        catalog["floor_4"]?.get("bazaar")?.apply {
            put("SPIRIT_WING", "Spirit Wing")
            put("SPIRIT_BONE", "Spirit Bone")
            put("SPIRIT_DECOY", "Spirit Stone")
            put("SECOND_MASTER_STAR", "Second Master Star")
        }

        // Floor 3 - Auctions
        catalog["floor_3"]?.get("auctions")?.apply {
            put("ADAPTIVE_HELMET", "Adaptive Helmet")
            put("ADAPTIVE_CHESTPLATE", "Adaptive Chestplate")
            put("ADAPTIVE_LEGGINGS", "Adaptive Leggings")
            put("ADAPTIVE_BOOTS", "Adaptive Boots")
        }

        // Floor 3 - Bazaar
        catalog["floor_3"]?.get("bazaar")?.apply {
            put("SUSPICIOUS_VIAL", "Suspicious Vial")
            put("FIRST_MASTER_STAR", "First Master Star")
        }

        // Floor 2 - Auctions
        catalog["floor_2"]?.get("auctions")?.apply {
            put("STONE_BLADE", "Adaptive Blade")
            put("ADAPTIVE_BELT", "Adaptive Belt")
            put("SCARF_STUDIES", "Scarf's Studies")
        }

        // Floor 2 - Bazaar
        catalog["floor_2"]?.get("bazaar")?.apply {
            put("RED_SCARF", "Red Scarf")
        }

        // Floor 1 - Auctions
        catalog["floor_1"]?.get("auctions")?.apply {
            put("BONZO_MASK", "Bonzo's Mask")
            put("BONZO_STAFF", "Bonzo's Staff")
            put("BALLOON_SNAKE", "Balloon Snake")
        }

        // Floor 1 - Bazaar
        catalog["floor_1"]?.get("bazaar")?.apply {
            put("RED_NOSE", "Red Nose")
        }

        // Miscellaneous - Auctions
        catalog["misc"]?.get("auctions")?.apply {
            put("MASTER_SKULL_TIER_5", "Master Skull - Tier 5")
            put("MASTER_SKULL_TIER_4", "Master Skull - Tier 4")
            put("MASTER_SKULL_TIER_3", "Master Skull - Tier 3")
            put("MASTER_SKULL_TIER_2", "Master Skull - Tier 2")
            put("MASTER_SKULL_TIER_1", "Master Skull - Tier 1")
        }

        // Enchantments - Bazaar
        catalog["enchs"]?.get("bazaar")?.apply {
            put("ENCHANTMENT_THUNDERLORD_7", "Thunderlord VII")
            put("ENCHANTMENT_OVERLOAD_1", "Overload I")
            put("ENCHANTMENT_REJUVENATE_3", "Rejuvenate III")
            put("ENCHANTMENT_REJUVENATE_2", "Rejuvenate II")
            put("ENCHANTMENT_REJUVENATE_1", "Rejuvenate I")
            put("ENCHANTMENT_FEATHER_FALLING_7", "Feather Falling VII")
            put("ENCHANTMENT_FEATHER_FALLING_6", "Feather Falling VI")
            put("ENCHANTMENT_INFINITE_QUIVER_7", "Infinite Quiver VII")
            put("ENCHANTMENT_INFINITE_QUIVER_6", "Infinite Quiver VI")
            put("ENCHANTMENT_LETHALITY_6", "Lethality VI")
        }

        // Ultimate Enchantments - Bazaar
        catalog["ults"]?.get("bazaar")?.apply {
            put("ENCHANTMENT_ULTIMATE_ONE_FOR_ALL_1", "One For All I")
            put("ENCHANTMENT_ULTIMATE_SOUL_EATER_1", "Soul Eater I")
            put("ENCHANTMENT_ULTIMATE_SWARM_1", "Swarm I")
            put("ENCHANTMENT_ULTIMATE_REND_2", "Rend II")
            put("ENCHANTMENT_ULTIMATE_REND_1", "Rend I")
            put("ENCHANTMENT_ULTIMATE_LEGION_1", "Legion I")
            put("ENCHANTMENT_ULTIMATE_LAST_STAND_2", "Last Stand II")
            put("ENCHANTMENT_ULTIMATE_LAST_STAND_1", "Last Stand I")
            put("ENCHANTMENT_ULTIMATE_WISE_2", "Ultimate Wise II")
            put("ENCHANTMENT_ULTIMATE_WISE_1", "Ultimate Wise I")
            put("ENCHANTMENT_ULTIMATE_WISDOM_2", "Wisdom II")
            put("ENCHANTMENT_ULTIMATE_WISDOM_1", "Wisdom I")
            put("ENCHANTMENT_ULTIMATE_BANK_3", "Bank III")
            put("ENCHANTMENT_ULTIMATE_BANK_2", "Bank II")
            put("ENCHANTMENT_ULTIMATE_BANK_1", "Bank I")
            put("ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_2", "No Pain No Gain II")
            put("ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_1", "No Pain No Gain I")
            put("ENCHANTMENT_ULTIMATE_COMBO_2", "Combo II")
            put("ENCHANTMENT_ULTIMATE_COMBO_1", "Combo I")
            put("ENCHANTMENT_ULTIMATE_JERRY_3", "Ultimate Jerry III")
            put("ENCHANTMENT_ULTIMATE_JERRY_2", "Ultimate Jerry II")
            put("ENCHANTMENT_ULTIMATE_JERRY_1", "Ultimate Jerry I")
        }

        // Miscellaneous - Bazaar (Universals, Essence, Chest Key)
        catalog["misc"]?.get("bazaar")?.apply {
            put("RECOMBOBULATOR_3000", "Recombobulator 3000")
            put("FUMING_POTATO_BOOK", "Fuming Potato Book")
            put("HOT_POTATO_BOOK", "Hot Potato Book")
            put("NECROMANCER_BROOCH", "Necromancer's Brooch")
            put("ESSENCE_WITHER", "Wither Essence")
            put("ESSENCE_UNDEAD", "Undead Essence")
            put("DUNGEON_CHEST_KEY", "Dungeon Chest Key")
        }

        return catalog
    }
}