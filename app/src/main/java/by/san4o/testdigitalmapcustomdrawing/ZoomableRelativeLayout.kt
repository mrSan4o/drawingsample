package by.san4o.testdigitalmapcustomdrawing

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import android.widget.RelativeLayout

class ZoomableRelativeLayout(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    var mScaleFactor = 1f
    var mPivotX = 0f
    var mPivotY = 0f




    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.scale(mScaleFactor, mScaleFactor, mPivotX, mPivotY)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    fun scale(scaleFactor: Float, pivotX: Float, pivotY: Float) {
        mScaleFactor = scaleFactor
        mPivotX = pivotX
        mPivotY = pivotY
        this.invalidate()
    }

    fun restore() {
        mScaleFactor = 1f
        this.invalidate()
    }
}


class OnPinchListener(private val layout: ZoomableRelativeLayout) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    var startingSpan = 0f
    var endSpan = 0f
    var startFocusX = 0f
    var startFocusY = 0f
    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        startingSpan = detector.currentSpan
        startFocusX = detector.focusX
        startFocusY = detector.focusY
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        layout.scale(detector.currentSpan / startingSpan, startFocusX, startFocusY)
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        layout.restore()
    }
}
