package by.san4o.testdigitalmapcustomdrawing.digitalmap

import android.graphics.Color

enum class ElementColor {
    Red,
    Blue,
    Yellow,
    Green,
    Black,
}

fun ElementColor.toPaintColor(): Int =
    when (this) {
        ElementColor.Black -> Color.BLACK
        ElementColor.Red -> Color.RED
        ElementColor.Blue -> Color.BLUE
        ElementColor.Yellow -> Color.YELLOW
        ElementColor.Green -> Color.GREEN
    }


enum class ElementFigure {
    Rectangle,
    Square,
    Circle,
}