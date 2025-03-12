package com.github.theholychicken.gui

import com.github.theholychicken.managers.ChestLootParser
import com.github.theholychicken.managers.DungeonChestScanner
import com.github.theholychicken.utils.modMessage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.lang.reflect.Field

/**
 * Renders chest profit in the chest tooltip, similarly to SBE
 */
object ChestProfitTooltip {
    private val mc = Minecraft.getMinecraft()
    private val fontRenderObj = mc.fontRendererObj

    @SubscribeEvent
    fun onRenderGameOverlay(event: GuiScreenEvent.DrawScreenEvent.Post) {
        if (DungeonChestScanner.chestIsParsed) {
            if (event.gui is GuiChest) {
                // variable declarations
                var guiRight = 0
                var guiTop = 0
                val guiChest = event.gui as GuiChest

                // extract protected fields determining far left and far right positions
                try {
                    val guiLeftField: Field = GuiContainer::class.java.getDeclaredField("field_147003_i")
                    guiLeftField.isAccessible = true

                    val guiTopField: Field = GuiContainer::class.java.getDeclaredField("field_147009_r")
                    guiTopField.isAccessible = true
                    guiTop = guiTopField.getInt(guiChest)

                    val xSizeField: Field = GuiContainer::class.java.getDeclaredField("field_146999_f")
                    xSizeField.isAccessible = true
                    guiRight = guiLeftField.getInt(guiChest) + xSizeField.getInt(guiChest)
                } catch (e: Exception) {
                    modMessage(e)
                }

                val profit = ChestLootParser.croesusChest.profit.toInt()
                val color = if (profit >= 0) 0x55FF55 else 0xFF5555

                fontRenderObj.drawString(
                    String.format("%,d", profit),
                    guiRight - fontRenderObj.getStringWidth(profit.toString()) - 10,
                    guiTop + (fontRenderObj.FONT_HEIGHT / 1.5).toInt(),
                    color
                )
            }
        }
    }
}