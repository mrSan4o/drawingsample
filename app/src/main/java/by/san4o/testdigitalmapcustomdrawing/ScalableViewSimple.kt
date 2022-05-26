package by.san4o.testdigitalmapcustomdrawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class ScalableViewSimple(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply { color = Color.RED }
    private var mScaleFactor = 1f
    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))

            invalidate()
            return true
        }
    }

    private val drawable: ShapeDrawable = run {
        val x = 10
        val y = 10
        val width = 300
        val height = 50
        contentDescription = "desc"
        ShapeDrawable(RectShape()).apply {
            // If the color isn't set, the shape uses black as the default.
            paint.color = 0xff74AC23.toInt()
            // If the bounds aren't set, the shape can't be drawn.
            setBounds(x, y, x + width, y + height)
        }
    }

    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev)
        return true
    }

    private val rect1 = Rect(100, 100, 100, 100)

    override fun onDraw(canvas: Canvas?) {

        canvas?.apply {

            save()
            scale(mScaleFactor, mScaleFactor)
            // onDraw() code goes here
            drawable.draw(this)
            drawRect(rect1, paint)
            restore()
        }
    }
}