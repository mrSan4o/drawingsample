package by.san4o.testdigitalmapcustomdrawing

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import java.util.Random

@SuppressLint("NewApi")
class CircleDragDropSampleView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val TAG = "CirclesDrawingView"

    /** Main bitmap  */
    private var mBitmap: Bitmap? = null

    private var mMeasuredRect: Rect? = null

    /** Stores data about single circle  */
    private class CircleArea internal constructor(var centerX: Int, var centerY: Int, var radius: Int) {
        override fun toString(): String {
            return "Circle[$centerX, $centerY, $radius]"
        }
    }

    /** Paint to draw circles  */
    private var mCirclePaint: Paint? = null

    private val mRadiusGenerator: Random = Random()

    // Radius limit in pixels
    private val RADIUS_LIMIT = 100

    private val CIRCLES_LIMIT = 3

    /** All available circles  */
    private val mCircles = HashSet<CircleArea>(CIRCLES_LIMIT)
    private val mCirclePointer = SparseArray<CircleArea?>(CIRCLES_LIMIT)

    init {

        // Generate bitmap used for background
        mBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_delete)
        mCirclePaint = Paint()
        mCirclePaint!!.color = Color.BLUE
        mCirclePaint!!.strokeWidth = 40f
        mCirclePaint!!.style = Paint.Style.FILL
    }

    override fun onDraw(canv: Canvas) {
        // background bitmap to cover all area
        canv.drawBitmap(mBitmap!!, null, mMeasuredRect!!, null)
        for (circle in mCircles) {
            canv.drawCircle(circle.centerX.toFloat(), circle.centerY.toFloat(), circle.radius.toFloat(), mCirclePaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var handled = false
        var touchedCircle: CircleArea?
        var xTouch: Int
        var yTouch: Int
        var pointerId: Int
        var actionIndex = event.actionIndex
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer()
                xTouch = event.getX(0).toInt()
                yTouch = event.getY(0).toInt()

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch)
                touchedCircle!!.centerX = xTouch
                touchedCircle.centerY = yTouch
                mCirclePointer.put(event.getPointerId(0), touchedCircle)
                invalidate()
                handled = true
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.w(TAG, "Pointer down")
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex)
                xTouch = event.getX(actionIndex).toInt()
                yTouch = event.getY(actionIndex).toInt()

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch)
                mCirclePointer.put(pointerId, touchedCircle)
                touchedCircle!!.centerX = xTouch
                touchedCircle.centerY = yTouch
                invalidate()
                handled = true
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerCount = event.pointerCount
                Log.w(TAG, "Move")
                actionIndex = 0
                while (actionIndex < pointerCount) {

                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex)
                    xTouch = event.getX(actionIndex).toInt()
                    yTouch = event.getY(actionIndex).toInt()
                    touchedCircle = mCirclePointer[pointerId]
                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch
                        touchedCircle.centerY = yTouch
                    }
                    actionIndex++
                }
                invalidate()
                handled = true
            }
            MotionEvent.ACTION_UP -> {
                clearCirclePointer()
                invalidate()
                handled = true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex)
                mCirclePointer.remove(pointerId)
                invalidate()
                handled = true
            }
            MotionEvent.ACTION_CANCEL -> handled = true
            else -> {}
        }
        return super.onTouchEvent(event) || handled
    }

    /**
     * Clears all CircleArea - pointer id relations
     */
    private fun clearCirclePointer() {
        Log.w(TAG, "clearCirclePointer")
        mCirclePointer.clear()
    }

    /**
     * Search and creates new (if nujjj/yh'ujU
     * ik
     * ytgt
     * ''ollp[u']
     * 8i8p;[fjmmnn;77uyh/;gtujhhkykykhugt;h/hkhunuhthut lk tlhllllll;pp;p;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;]]
     * @param xTouch int x of touch
     * @param yTouch int y of touch
     *
     * @return obtained [CircleArea]
     */
    private fun obtainTouchedCircle(xTouch: Int, yTouch: Int): CircleArea? {
        var touchedCircle = getTouchedCircle(xTouch, yTouch)
        if (null == touchedCircle) {
            touchedCircle = CircleArea(xTouch, yTouch, mRadiusGenerator.nextInt(RADIUS_LIMIT) + RADIUS_LIMIT)
            if (mCircles.size === CIRCLES_LIMIT) {
                Log.w(TAG, "Clear all circles, size is " + mCircles.size)
                // remove first circle
                mCircles.clear()
            }
            Log.w(TAG, "Added circle $touchedCircle")
            mCircles.add(touchedCircle)
        }
        return touchedCircle
    }

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     *
     * @return [CircleArea] touched circle or null if no circle has been touched
     */
    private fun getTouchedCircle(xTouch: Int, yTouch: Int): CircleArea? {
        var touched: CircleArea? = null
        for (circle in mCircles) {
            if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
                touched = circle
                break
            }
        }
        return touched
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mMeasuredRect = Rect(0, 0, measuredWidth, measuredHeight)
    }
}