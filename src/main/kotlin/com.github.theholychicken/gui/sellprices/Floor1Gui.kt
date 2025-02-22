package com.github.theholychicken.gui.sellprices

import com.github.theholychicken.GoodMod
import com.github.theholychicken.config.SellPricesConfig
import com.github.theholychicken.gui.utils.ToggleButton
import com.github.theholychicken.gui.utils.renderRows
import com.github.theholychicken.managers.AuctionParser
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.io.IOException
import kotlin.jvm.Throws

class Floor1Gui : GuiScreen() {

    // should contain itemNames, and the corresponding buttonId should be items.indexOf(itemName)
    private val items: List<Pair<String, String>> = AuctionParser.items["floor_1"]?.get("bazaar")?.toList() ?: emptyList()

    override fun initGui() {
        super.initGui()
        buttonList.clear()

        // init buttons here using util file
        renderRows(items, width, height, listOf(0x5555FF)).forEach { buttonList.add(it) }
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        if (button is ToggleButton) {
            SellPricesConfig.sellPrices[items[button.id].first] = !button.toggled
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        drawCenteredString(fontRendererObj, "Floor 1", width / 2, 20, 0x00FFFF)
        for (button in buttonList) {
            button.drawButton(mc, mouseX, mouseY)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            SellPricesConfig.saveConfig()
            mc.displayGuiScreen(ConfigSellPrices())
        }
    }

    override fun doesGuiPauseGame(): Boolean = false

    // allows gui to be opened with Floor1Gui.open()
    companion object {
        fun open() {
            GoodMod.display = Floor1Gui()
        }
    }
}