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
 * @param auctionPrices contains current lowest ah prices, indexed by their name in itemDropPatterns minus the formatting stuff
 * @param items contains the list of unformatted items in itemDropPatterns
 * @param initFromFile pulls data from the saved file
 */
/*
 * TODO
 *  I need to check to figure out how this shit is actually working
 *  Tidy up any logical errors then see if i can clean up any code
 *  Render over max profit [check that the dungeon chest key logic works]
 *  Implement COFL, Tricked
 *  Fix key calculations/reading <<-
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
    var shinyItems = mutableListOf(
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

        val auctionItemPrice = auctionPrices[itemName] ?: Double.MAX_VALUE

        if ((itemName in auctionPrices.keys && price < auctionItemPrice) || itemName !in auctionPrices.keys)
            auctionPrices[itemName] = price
    }

    // updates an entry using bazaar data
    fun updateBazaar(item: String, price: Double) {
        val itemName = isBazaarable(item) ?: return

        val multi = if (SellPricesConfig.sellPrices[item] == true) 0.9875F else 1F
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

    // this doesnt exist
    private fun itemMap(): MutableMap<String, MutableMap<String, MutableMap<String, String>>> {

        // AuctionParser.items
        // floor -> ah/bz -> <itemId, itemName>

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

        // Ultimate Enchantments - Auctions
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
/*
        // floor -> ah/bz -> <itemId to itemName>
        val list2: MutableMap<String, MutableMap<String, MutableMap<String, String>>> = mutableMapOf()
        list2["floor_7"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["floor_6"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["floor_5"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["floor_4"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["floor_3"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["floor_2"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["floor_1"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["enchs"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["ults"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())
        list2["misc"] = mutableMapOf("auctions" to mutableMapOf(), "bazaar" to mutableMapOf())

        val list = mutableMapOf<String, String>()
        // f7
        list["NECRON_HANDLE"] = "Necron's Handle"
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
        list.forEach { (itemId, itemName) ->
            list2["floor_7"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()
        list["IMPLOSION_SCROLL"] = "Implosion"
        list["SHADOW_WARP_SCROLL"] = "Shadow Warp"
        list["WITHER_SHIELD_SCROLL"] = "Wither Shield"
        list["WITHER_BLOOD"] = "Wither Blood"
        list["WITHER_CATALYST"] = "Wither Catalyst"
        list["PRECURSOR_GEAR"] = "Precursor Gear"
        list["FIFTH_MASTER_STAR"] = "Fifth Master Star"
        list.forEach { (itemId, itemName) ->
            list2["floor_7"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()

        // f6
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
        list.forEach { (itemId, itemName) ->
            list2["floor_6"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()
        list["GIANT_TOOTH"] = "Giant Tooth"
        list["SADAN_BROOCH"] = "Sadan's Brooch"
        list["FOURTH_MASTER_STAR"] = "Fourth Master Star"
        list.forEach { (itemId, itemName) ->
            list2["floor_6"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()

        // f5
        list["SHADOW_FURY"] = "Shadow Fury"
        list["LAST_BREATH"] = "Last Breath"
        list["LIVID_DAGGER"] = "Livid Dagger"
        list["SHADOW_ASSASSIN_HELMET"] = "Shadow Assassin Helmet"
        list["SHADOW_ASSASSIN_CHESTPLATE"] = "Shadow Assassin Chestplate"
        list["SHADOW_ASSASSIN_LEGGINGS"] = "Shadow Assassin Leggings"
        list["SHADOW_ASSASSIN_BOOTS"] = "Shadow Assassin Boots"
        list["SHADOW_ASSASSIN_CLOAK"] = "Shadow Assassin Cloak"
        list.forEach { (itemId, itemName) ->
            list2["floor_5"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()
        list["AOTE_STONE"] = "Warped Stone"
        list["DARK_ORB"] = "Dark Orb"
        list["THIRD_MASTER_STAR"] = "Third Master Star"
        list.forEach { (itemId, itemName) ->
            list2["floor_5"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()

        // f4
        list["PET-SPIRIT-LEGENDARY"] = "[Lvl 1] §6Spirit"
        list["PET-SPIRIT-EPIC"] = "[Lvl 1] §5Spirit"
        list["THORNS_BOOTS"] = "Spirit Boots"
        list["ITEM_SPIRIT_BOW"] = "Spirit Shortbow"
        list["SPIRIT_SWORD"] = "Spirit Sword"
        list.forEach { (itemId, itemName) ->
            list2["floor_4"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()
        list["SPIRIT_WING"] = "Spirit Wing"
        list["SPIRIT_BONE"] = "Spirit Bone"
        list["SPIRIT_DECOY"] = "Spirit Stone"
        list["SECOND_MASTER_STAR"] = "Second Master Star"
        list.forEach { (itemId, itemName) ->
            list2["floor_4"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()
        // f3
        list["ADAPTIVE_HELMET"] = "Adaptive Helmet"
        list["ADAPTIVE_CHESTPLATE"] = "Adaptive Chestplate"
        list["ADAPTIVE_LEGGINGS"] = "Adaptive Leggings"
        list["ADAPTIVE_BOOTS"] = "Adaptive Boots"
        list.forEach { (itemId, itemName) ->
            list2["floor_3"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()
        list["SUSPICIOUS_VIAL"] = "Suspicious Vial"
        list["FIRST_MASTER_STAR"] = "First Master Star"
        list.forEach { (itemId, itemName) ->
            list2["floor_3"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()

        // f2
        list["STONE_BLADE"] = "Adaptive Blade"
        list["ADAPTIVE_BELT"] = "Adaptive Belt"
        list["SCARF_STUDIES"] = "Scarf's Studies"
        list.forEach { (itemId, itemName) ->
            list2["floor_2"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()
        list["RED_SCARF"] = "Red Scarf"
        list.forEach { (itemId, itemName) ->
            list2["floor_2"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()

        // f1
        list["BONZO_MASK"] = "Bonzo's Mask"
        list["BONZO_STAFF"] = "Bonzo's Staff"
        list["BALLOON_SNAKE"] = "Balloon Snake"
        list.forEach { (itemId, itemName) ->
            list2["floor_1"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()
        list["RED_NOSE"] = "Red Nose"
        list.forEach { (itemId, itemName) ->
            list2["floor_1"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()

        // skulls (misc -> auctions)
        list["MASTER_SKULL_TIER_5"] = "Master Skull - Tier 5"
        list["MASTER_SKULL_TIER_4"] = "Master Skull - Tier 4"
        list["MASTER_SKULL_TIER_3"] = "Master Skull - Tier 3"
        list["MASTER_SKULL_TIER_2"] = "Master Skull - Tier 2"
        list["MASTER_SKULL_TIER_1"] = "Master Skull - Tier 1"
        list.forEach { (itemId, itemName) ->
            list2["misc"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()

        // enchs
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
        list.forEach { (itemId, itemName) ->
            list2["enchs"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()
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
        list.forEach { (itemId, itemName) ->
            list2["ults"]?.get("auctions")?.set(itemId, itemName)
        }
        list.clear()
        // universals (misc -> bazaar)
        list["RECOMBOBULATOR_3000"] = "Recombobulator 3000"
        list["FUMING_POTATO_BOOK"] = "Fuming Potato Book"
        list["HOT_POTATO_BOOK"] = "Hot Potato Book"
        list["NECROMANCER_BROOCH"] = "Necromancer's Brooch"
        list.forEach { (itemId, itemName) ->
            list2["misc"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()
        // essence (misc -> bazaar)
        list["ESSENCE_WITHER"] = "Wither Essence"
        list["ESSENCE_UNDEAD"] = "Undead Essence"
        list.forEach { (itemId, itemName) ->
            list2["misc"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()
        // chest key (misc -> bazaar)
        list["DUNGEON_CHEST_KEY"] = "Dungeon Chest Key"
        list.forEach { (itemId, itemName) ->
            list2["misc"]?.get("bazaar")?.set(itemId, itemName)
        }
        list.clear()
        return list2
 */
