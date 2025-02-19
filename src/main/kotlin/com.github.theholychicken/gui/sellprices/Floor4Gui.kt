package com.github.theholychicken.gui.sellprices

import com.github.theholychicken.GoodMod
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.io.IOException
import kotlin.jvm.Throws

class Floor4Gui : GuiScreen() {

    // should contain itemNames, and the corresponding buttonId should be items.indexOf(itemName)
    private val items: List<String> = listOf()

    override fun initGui() {
        super.initGui()
        buttonList.clear()

        // init buttons here using util file
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            //
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        drawCenteredString(fontRendererObj, "Floor 4", width / 2, 20, 0x00FFFF)
        for (button in buttonList) {
            button.drawButton(mc, mouseX, mouseY)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(ConfigSellPrices())
        }
    }

    override fun doesGuiPauseGame(): Boolean = false

    // allows gui to be opened with Floor4Gui.open()
    companion object {
        fun open() {
            GoodMod.display = Floor4Gui()
        }
    }
}