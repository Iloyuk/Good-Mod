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

class Floor4Gui : GuiScreen() {

    // should contain itemNames, and the corresponding buttonId should be items.indexOf(itemName)
    private val items: List<Pair<String, String>> = AuctionParser.items["floor_4"]?.get("bazaar")?.toList() ?: emptyList()

    override fun initGui() {
        super.initGui()
        buttonList.clear()

        // init buttons here using util file
        renderRows(items, width, height, listOf(0xAA00AA, 0x5555FF, 0x5555FF, 0xAA00AA)).forEach { buttonList.add(it) }
        buttonList.add(GuiButton(100, width - 103, 3, 100, 20, "Back"))
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        if (button is ToggleButton) {
            SellPricesConfig.sellPrices[items[button.id].first] = !button.toggled
        } else if (button.id == 100) {
            SellPricesConfig.saveConfig()
            mc.displayGuiScreen(ConfigSellPrices())
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
            SellPricesConfig.saveConfig()
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