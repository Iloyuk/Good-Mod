package com.github.theholychicken.gui

import com.github.theholychicken.GoodMod
import com.github.theholychicken.config.GuiConfig
import com.github.theholychicken.gui.utils.DropdownMenu
import com.github.theholychicken.utils.modMessage
import net.minecraft.client.gui.*
import org.lwjgl.input.Keyboard
import java.io.IOException

class ConfigGUI : GuiScreen() {
    private var getItemsNameField: GuiTextField? = null
    private var openGuiNameField: GuiTextField? = null
    private lateinit var dropdownMenu: DropdownMenu
    private var apis = mutableListOf(
        "Hypixel API" to {GuiConfig.api = "HypixelApi" },
        "Cofl API" to { GuiConfig.api = "CoflApi" },
        "Skytils API" to { GuiConfig.api = "TrickedApi" }
    )
    private val selected = when {
        GuiConfig.api == "HypixelApi" -> 0
        GuiConfig.api == "CoflApi" -> 1
        GuiConfig.api == "TrickedApi" -> 2
        else -> 0
    }

    override fun initGui() {
        super.initGui()
        // Button for opening items gui
        buttonList.add(
            GuiButton(
                2, (this.width / 2) - 100, (this.height / 2) - 24,
                renderMainCroesusMenu
            )
        )
        buttonList.add(
            GuiButton(
                0, (this.width / 2) - 100, (this.height / 2) + 2,
                buttonLabel
            )
        )
        // toggles GuiConfig.useSellOffer
        buttonList.add(
            GuiButton(
                1, (this.width / 2) - 100, (this.height / 2) + 26,
                sellOffer
            )
        )

        // Input Field for setting owoCommand
        this.getItemsNameField = GuiTextField(1, this.fontRendererObj, (this.width / 2) - 100, (this.height / 2) - 50, 200, 20)
        getItemsNameField?.maxStringLength = 100
        getItemsNameField?.isFocused = true
        getItemsNameField?.enableBackgroundDrawing = true
        getItemsNameField?.text = GuiConfig.commandNames["getItems"]
        this.openGuiNameField = GuiTextField(2, this.fontRendererObj, (this.width / 2) - 100, (this.height / 2) - 76, 200, 20)
        openGuiNameField?.maxStringLength = 100
        openGuiNameField?.isFocused = true
        openGuiNameField?.enableBackgroundDrawing = true
        openGuiNameField?.text = GuiConfig.commandNames["goodmod"]

        // Dropdown menu for api endpoints
        dropdownMenu = DropdownMenu((this.width / 2) - 100, (this.height / 2) + 50, 200, apis, selected)
        dropdownMenu.initButtons(buttonList)
    }

    override fun updateScreen() {
        super.updateScreen()
        getItemsNameField?.updateCursorCounter()
        openGuiNameField?.updateCursorCounter()
    }

    @Throws(IOException::class)
    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> ItemDropHUD.open()
            1 -> {
                GuiConfig.useSellOffer = !GuiConfig.useSellOffer
                GuiConfig.saveConfig()
                GuiConfig.loadConfig()
                button.displayString = sellOffer
            }
            2 -> {
                GuiConfig.renderMainCroesusGui = !GuiConfig.renderMainCroesusGui
                GuiConfig.saveConfig()
                GuiConfig.loadConfig()
                button.displayString = renderMainCroesusMenu
            }
            in 100..(100 + apis.size) -> {
                dropdownMenu.handleButtonClick(button)
                dropdownMenu.updateDropdownLabel(buttonList)
                GuiConfig.saveConfig()
                GuiConfig.loadConfig()
                if (button.id > 100)  modMessage("You have activated the ${apis[button.id - 101].first}!")
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        //drawCenteredString(this.fontRendererObj, "§4§kOWO OWO§r good mod Config §4§kOWO OWO§r", this.width / 2, this.height / 2 - 100, 0x00FFFF);
        drawCenteredString(this.fontRendererObj, "good mod config", this.width / 2, this.height / 2 - 100, 0x00FFFF)

        // Draws owoCommand input field
        drawCenteredString(
            this.fontRendererObj, "set /goodmod alias",
            this.width / 2 + 150,
            this.height / 2 - 70, 0x00FFFF
        )
        getItemsNameField?.drawTextBox()

        drawCenteredString(this.fontRendererObj, "set /getItems alias", this.width / 2 + 150, this.height / 2 - 45, 0x00FFFF)
        openGuiNameField?.drawTextBox()

        // Draws buttons
        for (button in this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY)
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (this.getItemsNameField?.textboxKeyTyped(typedChar, keyCode) == true) {
        }
        else if (this.openGuiNameField?.textboxKeyTyped(typedChar, keyCode) == true) {
        }
        if (keyCode == Keyboard.KEY_RETURN) {
            if (getItemsNameField?.isFocused == true) {
                GuiConfig.commandNames["getItems"] = getItemsNameField!!.text
                GuiConfig.saveConfig()
                GuiConfig.loadConfig()
                mc.displayGuiScreen(null)
                modMessage("Set /getItems to /" + getItemsNameField!!.text + "! §r§2§lRestart game for changes to take effect.")
            } else if (openGuiNameField?.isFocused == true) {
                GuiConfig.commandNames["goodmod"] = openGuiNameField!!.text
                GuiConfig.saveConfig()
                GuiConfig.loadConfig()
                mc.displayGuiScreen(null)
                modMessage("Set /goodmod to /" + openGuiNameField!!.text + "! §r§2§lRestart game for changes to take effect.")
            }
        } else if (keyCode == Keyboard.KEY_ESCAPE) mc.displayGuiScreen(null)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        getItemsNameField?.mouseClicked(mouseX, mouseY, mouseButton)
        if (dropdownMenu.expanded && !isMouseOverDropdown(mouseX, mouseY)) {
            dropdownMenu.closeDropdown()
        }
    }

    private fun isMouseOverDropdown(mouseX: Int, mouseY: Int): Boolean {
        val dropdownHeight = if (dropdownMenu.expanded) {
            (dropdownMenu.options.size + 1) * 20
        } else {
            20
        }

        return mouseX >= dropdownMenu.x &&
                mouseX <= dropdownMenu.x + dropdownMenu.width &&
                mouseY >= dropdownMenu.y &&
                mouseY <= dropdownMenu.y + dropdownHeight
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    private val buttonLabel: String
        get() = "stuff display"

    private val sellOffer: String
        get() = when {
            GuiConfig.useSellOffer -> "Using sell offers"
            !GuiConfig.useSellOffer -> "Using instasell"
            else -> "Error initiating config"
        }

    private val renderMainCroesusMenu: String
        get() = "Render Croesus Chest Overlay: ${GuiConfig.renderMainCroesusGui}"
}