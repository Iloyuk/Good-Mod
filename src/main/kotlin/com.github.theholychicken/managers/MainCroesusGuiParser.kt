package com.github.theholychicken.managers

import net.minecraft.inventory.ContainerChest
import net.minecraft.nbt.NBTTagString

object MainCroesusGuiParser {
    val openedChests: MutableMap<Pair<Int, Int>, String> = mutableMapOf()

    fun parseCroesusMenu(chest: ContainerChest) {
        openedChests.clear()
        for (index in 10..43) {
            val stack = chest.lowerChestInventory.getStackInSlot(index)
                ?.takeIf { !it.toString().matches(Regex("1xtile.thinStainedGlass@\\\\d+\$")) } ?: continue

            val tagCompound = stack.tagCompound ?: continue
            val chestInfo = tagCompound.getCompoundTag("display").getTagList("Lore", NBTTagString().id.toInt())

            // Index of the "Completed: XX time ago" line
            val completedIndex = (1 until chestInfo.tagCount())
                .firstOrNull { Regex("§7Completed:").containsMatchIn(chestInfo.getStringTagAt(it))} ?: continue

            val display = Pair(chest.inventorySlots[index].xDisplayPosition, chest.inventorySlots[index].yDisplayPosition)

            when {
                chestInfo.getStringTagAt(completedIndex + 1) == "§aNo more Chests to open!" -> openedChests[display] = "c"
                chestInfo.getStringTagAt(completedIndex + 2) == "§8No Chests Opened!" -> openedChests[display] = "u"
                chestInfo.getStringTagAt(completedIndex + 2).matches(Regex("^§8Opened Chest: (Wood|Gold|Diamond|Emerald|Obsidian|Bedrock)$")) -> openedChests[display] = "h"
            }
        }
    }
}