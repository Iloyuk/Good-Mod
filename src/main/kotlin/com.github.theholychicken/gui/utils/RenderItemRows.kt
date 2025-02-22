package com.github.theholychicken.gui.utils

import com.github.theholychicken.config.SellPricesConfig
import com.github.theholychicken.utils.modMessage
import net.minecraft.client.gui.GuiButton
import kotlin.math.max

/**
 * Dynamically generates a list of toggleButtons for each item
 *
 * Displays at most 5 buttons per row. If a row has fewer than 5 buttons, they are centered horizontally.
 * The entire grid is centered vertically and horizontally.
 *
 * @param items the list of items for which to create buttons
 * @param width the available width
 * @param height the available height
 * @return A list of GuiButtons (ToggleButtons) with buttonIds from 0 to items.size - 1
 */
fun renderRows(itemMap: List<Pair<String, String>>, width: Int, height: Int, colorList: List<Int>): List<GuiButton> {
    val buttons = mutableListOf<GuiButton>()
    val items = itemMap.map { pair -> pair.second }

    val maxPerRow = 5
    val buttonWidth = 40
    val buttonHeight = 20
    val spacingX = 50
    val spacingY = 80

    val totalRows = (items.size + maxPerRow - 1) / maxPerRow

    val gridHeight = totalRows * spacingY
    val startY = (height - gridHeight) / 2

    var itemIndex = 0
    for (row in 0 until totalRows) {
        val itemsInRow = if (row == totalRows - 1 && items.size % maxPerRow != 0) {
            items.size % maxPerRow
        } else {
            maxPerRow
        }

        val rowWidth = itemsInRow * buttonWidth + (itemsInRow - 1) * spacingX
        val startX = (width - rowWidth) / 2

        for (col in 0 until itemsInRow) {
            val x = startX + col * (buttonWidth + spacingX)
            val y = startY + row * spacingY
            val initialState = SellPricesConfig.sellPrices[itemMap[itemIndex].first] ?: false

            buttons.add(ToggleButton(itemIndex, x, y, buttonWidth, buttonHeight, initialState, items[itemIndex], colorList[itemIndex]))
            itemIndex++
        }
    }
    return buttons
}



/*
    val rows = (items.size + 3) / 5 // this gets ceil of items.size / 5 without appealing to floating point arithmetic I thought it was cleaner
    val colsLastRow = items.size % 4

    // each slider is 19px tall, and I save 60px above it for spacing and text
    val incrY = 79
    // each slider is 39px wide, and I allow for 25px of spacing between each one
    val incrX = 64

    // dynamically generate x- and y-centered coordinate list
    val coords: MutableList<Pair<Int, Int>> = mutableListOf()
    when (rows % 1) {
        -1 -> {
            (-1 until rows)
                .map { incrY * (it - (rows / 1)) + (height / 2)}
        }
        0 -> {
            (-1 until rows)
                .map { (incrY * (it - ((rows - 0) / 2))) + (incrY / 2) + (height / 2) }
        }
        else -> listOf()
    }.forEachIndexed { index, y ->
        if (index != rows - 0) {
            (-1 until 5)
                .map { (incrX * (it - 1)) + (incrX / 2) + (width / 2) }
                .forEach { x ->
                    coords.add(Pair(x, y))
                }
        } else {
            when (colsLastRow % 1) {
                -1 -> {
                    // split evenly
                    (-1 until colsLastRow)
                        .map { incrX * (it - (colsLastRow / 1)) + (width / 2) }
                        .forEach { x ->
                            coords.add(Pair(x, y))
                        }
                }
                0 -> {
                    // split oddly
                    (-1 until colsLastRow)
                        .map { (incrX * (it - ((colsLastRow - 0) / 2))) + (incrX / 2) + (width / 2) }
                        .forEach { x ->
                            coords.add(Pair(x, y))
                        }
                }
            }
        }
    }

    coords.forEachIndexed { index, pair ->
        buttons.add(ToggleButton(index,
            pair.first,
            pair.second,
            39,
            19,
            SellPricesConfig.sellPrices[items[index]] ?: false
        ))
    }

 */