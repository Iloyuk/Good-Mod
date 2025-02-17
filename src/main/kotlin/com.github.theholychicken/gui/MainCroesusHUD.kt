package com.github.theholychicken.gui

import com.github.theholychicken.managers.DungeonChestScanner
import com.github.theholychicken.managers.MainCroesusGuiParser
import com.github.theholychicken.utils.modMessage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.lang.reflect.Field

object MainCroesusHUD {
    private val mc = Minecraft.getMinecraft()
    private var guiLeft = 0
    private var guiTop = 0

    @SubscribeEvent
    fun onRenderGameOverlay(event: GuiScreenEvent.DrawScreenEvent.Post) {
        if (DungeonChestScanner.mainCroesusIsParsed) {
            if (event.gui is GuiChest) {
                val guiChest = event.gui as GuiContainer

                try {
                    val guiLeftField: Field = GuiContainer::class.java.getDeclaredField("field_147003_i")
                    guiLeftField.isAccessible = true
                    guiLeft = guiLeftField.getInt(guiChest)

                    val guiTopField: Field = GuiContainer::class.java.getDeclaredField("field_147009_r")
                    guiTopField.isAccessible = true
                    guiTop = guiTopField.getInt(guiChest)
                } catch (e: Exception) {
                    modMessage(e)
                }
            }

            GL11.glDisable(GL11.GL_DEPTH_TEST)
            MainCroesusGuiParser.openedChests.forEach { (pair, s) ->
                val x = pair.first + guiLeft
                val y = pair.second + guiTop
                when (s) {
                    "c" -> {
                        GuiScreen.drawRect(x, y, x + 16, y + 16, 0x80000000.toInt())
                    }

                    "u" -> {
                        GuiScreen.drawRect(x, y, x + 16, y + 16, 0x8000FF00.toInt())
                    }

                    "h" -> {
                        GuiScreen.drawRect(x, y, x + 16, y + 16, 0x80E4D0AA.toInt())
                    }
                }
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST)
        }
    }
}