package com.github.theholychicken.gui

import com.github.theholychicken.managers.CroesusChestParser
import com.github.theholychicken.managers.DungeonChestScanner
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object CroesusProfitHUD {
    private val mc = Minecraft.getMinecraft()

    @SubscribeEvent
    fun onRenderGameOverlay(event: GuiScreenEvent.DrawScreenEvent.Post) {
        if (DungeonChestScanner.croesusIsParsed) {
            var scale = mc.gameSettings.guiScale
            var width = mc.displayWidth / scale
            var height = mc.displayHeight / scale

            val offsetX = 100
            val chestWidth = 176
            val chestHeight = 166
            val rectX1 = width / 2 + offsetX
            val rectX2 = rectX1 + chestWidth
            val rectY1 = height / 2 - chestHeight /2
            val rectY2 = rectY1 + chestHeight

            GuiScreen.drawRect(rectX1, rectY1, rectX2, rectY2, 0x80000000.toInt())
            mc.fontRendererObj.drawString("LOOT", rectX1 + chestWidth / 2 - mc.fontRendererObj.getStringWidth("LOOT") / 2, rectY1 + 10, 0x00FFFF)
            CroesusChestParser.runLoot
                .filter { !it.purchased }
                .sortedByDescending { it.profit }
                .forEachIndexed { index, chest ->
                    mc.fontRendererObj.drawString(
                        chest.name
                                + "§r§f: §r§${ if (chest.profit >= 0) "a" else "c"}"
                                + String.format("%,d", chest.profit.toInt())
                                + " coins",
                        rectX1 + 10,
                        rectY1 + 30 + index * mc.fontRendererObj.FONT_HEIGHT,
                        0x00FFFF
                    )
                }
        }
    }

    fun sendLootChat() {
        CroesusChestParser.runLoot
            .filter { !it.purchased }
            .sortedByDescending { it.profit }
            .forEach { chest ->
                mc.thePlayer.addChatMessage(
                    ChatComponentText(
                        chest.name
                                + "§r§b: §r§5"
                                + String.format("%,d", chest.profit.toInt())
                                + " coins"
                    )
                )
            }
    }
}