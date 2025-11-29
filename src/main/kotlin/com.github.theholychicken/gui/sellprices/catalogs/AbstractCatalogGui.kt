package com.github.theholychicken.gui.sellprices.catalogs
import com.github.theholychicken.config.SellPricesConfig
import com.github.theholychicken.gui.sellprices.ConfigSellPrices
import com.github.theholychicken.gui.utils.ToggleButton
import com.github.theholychicken.managers.SellableItemParser
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard
import java.io.IOException
import kotlin.collections.set

abstract class AbstractCatalogGui : GuiScreen() {
    // should contain itemNames, and the corresponding buttonId should be items.indexOf(itemName)
    // what does this mean???? - ilo
    abstract val items: List<SellableItemParser.SellableItem>
    abstract val guiName: String

    override fun initGui() {
        super.initGui()
        buttonList.clear()
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        if (button is ToggleButton) {
            SellPricesConfig.sellPrices[items[button.id].name] = !button.toggled
        } else if (button.id == 100) {
            SellPricesConfig.saveConfig()
            mc.displayGuiScreen(ConfigSellPrices())
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        drawCenteredString(fontRendererObj, guiName, width / 2, 20, 0x00FFFF)
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
}