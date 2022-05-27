package by.san4o.testdigitalmapcustomdrawing.digitalmap

import android.graphics.RectF

fun rectByCenter(x: Float, y: Float, width: Float, height: Float): RectF {
    val centerXPart = width / 2f
    val centerYPart = height / 2f
    val left = x - centerXPart
    val top = y - centerYPart
    val right = x + centerXPart
    val bottom = y + centerYPart
    return RectF(left, top, right, bottom)
}

fun RectF.newCenter(x: Float, y: Float) {
    val centerXPart = width() / 2f
    val centerYPart = height() / 2f
    val left = x - centerXPart
    val top = y - centerYPart
    val right = x + centerXPart
    val bottom = y + centerYPart
    set(left, top, right, bottom)
}