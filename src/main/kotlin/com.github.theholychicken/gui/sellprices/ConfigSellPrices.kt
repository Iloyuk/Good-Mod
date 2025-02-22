package com.github.theholychicken.gui.sellprices

import com.github.theholychicken.GoodMod
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import java.io.IOException
import org.lwjgl.input.Keyboard
import kotlin.jvm.Throws

class ConfigSellPrices : GuiScreen() {

    override fun initGui() {
        super.initGui()
        buttonList.clear()

        // add a button for each sell price config thingy
        buttonList.add(GuiButton(0, width / 2 - 175, height / 2 - 25, 100, 20, "Floor 7"))
        buttonList.add(GuiButton(1, width / 2 - 50, height / 2 - 25, 100, 20, "Floor 6"))
        buttonList.add(GuiButton(2, width / 2 + 75, height / 2 - 25, 100, 20, "Floor 5"))
        buttonList.add(GuiButton(3, width / 2 - 175, height / 2, 100, 20, "Floor 4"))
        buttonList.add(GuiButton(4, width / 2 - 50, height / 2, 100, 20,"Floor 3"))
        buttonList.add(GuiButton(5, width / 2 + 75, height / 2, 100, 20, "Floor 2"))
        buttonList.add(GuiButton(6, width / 2 - 175, height / 2 + 25, 100, 20, "Floor 1"))
        buttonList.add(GuiButton(7, width / 2 - 50, height / 2 + 25, 100, 20, "Enchants"))
        buttonList.add(GuiButton(8, width / 2 + 75, height / 2 + 25, 100, 20, "Ultimate Enchants"))
        buttonList.add(GuiButton(9, width / 2 - 50, height / 2 + 50, 100, 20, "Miscellaneous"))
        buttonList.add(GuiButton(100, width - 103, 3, 100, 20, "Exit"))
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> Floor7Gui.open()
            1 -> Floor6Gui.open()
            2 -> Floor5Gui.open()
            3 -> Floor4Gui.open()
            4 -> Floor3Gui.open()
            5 -> Floor2Gui.open()
            6 -> Floor1Gui.open()
            7 -> EnchantsGui.open()
            8 -> UltEnchantsGui.open()
            9 -> MiscGui.open()
            100 -> mc.displayGuiScreen(null)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        drawCenteredString(fontRendererObj, "§lSelect option to expand drops", width / 2, height / 2 - 50, 0x00FFFF)
        for (button in buttonList) {
            button.drawButton(mc, mouseX, mouseY)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null)
        }
    }

    override fun doesGuiPauseGame(): Boolean = false

    // Allows ConfigSellPrices.open() to be called
    companion object {
        fun open() {
            GoodMod.display = ConfigSellPrices()
        }
    }
}