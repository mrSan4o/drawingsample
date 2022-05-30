package by.san4o.testdigitalmapcustomdrawing.digitalmap.element

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import by.san4o.testdigitalmapcustomdrawing.digitalmap.ElementColor

interface DrawElement {
    var color: ElementColor
    var name: String

    fun contains(x: Float, y: Float): Boolean
    fun starDragging()
    fun dragTo(x: Float, y: Float)
    fun endDragging()

    fun setSelected(enabled: Boolean)

    fun draw(canvas: Canvas)

    fun rotate(value: Float)


}


abstract class AbstractDrawElement(
    override var name: String,
    protected val paint: Paint
) : DrawElement {

    override var color: ElementColor
        get() = when (paint.color) {
            Color.RED -> ElementColor.Red
            Color.BLUE -> ElementColor.Blue
            Color.YELLOW -> ElementColor.Yellow
            Color.GREEN -> ElementColor.Green
            else -> ElementColor.Black
        }
        set(value) {
            paint.color = when (value) {
                ElementColor.Black -> Color.BLACK
                ElementColor.Red -> Color.RED
                ElementColor.Blue -> Color.BLUE
                ElementColor.Yellow -> Color.YELLOW
                ElementColor.Green -> Color.GREEN
            }
        }

    override fun starDragging() {
        paint.setShadowLayer(8f, 10f, 10f, Color.GRAY)
    }

    override fun setSelected(enabled: Boolean) {
        if (enabled) {
            paint.setShadowLayer(10f, 0f, 0f, Color.YELLOW)
        } else {
            paint.clearShadowLayer()
        }
    }

    override fun endDragging() {
        paint.clearShadowLayer()
    }

}