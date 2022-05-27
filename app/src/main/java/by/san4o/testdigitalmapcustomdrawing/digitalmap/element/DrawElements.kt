package by.san4o.testdigitalmapcustomdrawing.digitalmap.element

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Path
import android.graphics.RectF
import android.util.Log
import by.san4o.testdigitalmapcustomdrawing.digitalmap.newCenter


class RectDrawElement(
    name: String,
    private val rect: RectF,
    paint: Paint
) : AbstractDrawElement(name, paint) {

    private var angle = 0f
    private val matrix = Matrix()
    private val path = Path()
    private val textPaint = Paint()
        .apply {
//            textAlign = Paint.Align.CENTER
            textSize = 20f

        }

    override fun draw(canvas: Canvas) {
        path.reset()
        path.addRect(rect, Path.Direction.CW)
//        val textPath = Path()
//        textPath.addRect(rect.left, rect.top - rect.height() / 2f, rect.right, rect.top - rect.height() / 2f, Path.Direction.CW)
//        path.addPath(textPath)

        matrix.reset()
        matrix.setRotate(angle, rect.centerX(), rect.centerY())
        path.transform(matrix)
//        textPath.transform(matrix)

        canvas.drawPath(path, paint)

//        path.reset()

        canvas.drawTextOnPath(name, path, 0f, 0f, textPaint)
//        drawRectText(name, canvas, rect)
    }

    private fun drawRectText(text: String, canvas: Canvas, r: RectF) {
        textPaint.textSize = 20f
        textPaint.textAlign = Align.CENTER
        val width = r.width()
        val numOfChars = textPaint.breakText(text, true, width.toFloat(), null)
        val start = (text.length - numOfChars) / 2
        canvas.drawText(text, start, start + numOfChars, r.centerX(), r.centerY(), textPaint)
    }

    override fun contains(x: Float, y: Float): Boolean {
        return rect.contains(x, y)
    }

    override fun dragTo(x: Float, y: Float) {
        rect.newCenter(x, y)
    }

    override fun rotate(value: Float) {
        angle += value

        Log.d(TAG, "rotate: $angle")
    }

    companion object {
        private const val TAG = "RectDrawElement"
    }
}

class CircleDrawElement(
    name: String,
    private val circle: Circle,
    paint: Paint
) : AbstractDrawElement(name, paint) {
    override fun draw(canvas: Canvas) {
        canvas.drawCircle(circle.x, circle.y, circle.r, paint)
    }

    override fun rotate(value: Float) {

    }

    override fun contains(x: Float, y: Float): Boolean {
        return circle.x - circle.r < x
            && circle.x + circle.r > x
            && circle.y - circle.r < y
            && circle.y + circle.r > y
    }

    override fun dragTo(x: Float, y: Float) {
        circle.x = x
        circle.y = y
    }
}

data class Circle(
    var x: Float,
    var y: Float,
    val r: Float,
)