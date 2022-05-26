package by.san4o.testdigitalmapcustomdrawing.digitalmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat

class DigitalMapCustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paintRed = Paint()
        .apply {
            color = Color.RED
        }
    private val rectPaint = Paint()
        .apply {
            color = Color.RED
        }
    private val paintBlue = Paint()
        .apply {
            color = Color.BLUE
        }
    private val paintGreen = Paint()
        .apply {
            color = Color.GREEN
        }
    private val paintYellow = Paint()
        .apply {
            color = Color.YELLOW
        }
    private val rect = Rect(10, 50, 200, 200)
    private var circle: Circle = Circle(0f, 0f, 0f)
    private val circle2: Circle = Circle(500f, 300f, 100f)

    private var scaleFactor = 1f
    private var scaleFocusX = 0f
    private var scaleFocusY = 0f
    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            scaleFocusX = detector.focusX
            scaleFocusY = detector.focusY
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            val currentSpan = detector.currentSpan
            val focusX = detector.focusX
            val focusY = detector.focusY
            val currentSpanX = detector.currentSpanX
            val currentSpanY = detector.currentSpanY

            // Don't let the object get too small or too large.
            scaleFactor = Math.max(1f, Math.min(scaleFactor, 5.0f))

            ViewCompat.postInvalidateOnAnimation(this@DigitalMapCustomView)
            return true
        }
    }
    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    private val displayWidth: Int
    private val displayHeight: Int

    private val AXIS_X_MIN = -1f
    private val AXIS_X_MAX = 1f
    private val AXIS_Y_MIN = -1f
    private val AXIS_Y_MAX = 1f
    private val mCurrentViewport = RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX)
    private var firstDraw = true
    private var canvasX: Float = 0f
    private var canvasY: Float = 0f
    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return super.onDown(e)
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {

            canvasX += -1 * distanceX / scaleFactor
            canvasY += -1 * distanceY / scaleFactor

            Log.d(">>>", "gestureListener distanceX=$distanceX, distanceY=$distanceY")
            ViewCompat.postInvalidateOnAnimation(this@DigitalMapCustomView)

            return true
        }
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    private val shadowPaint = Paint()
        .apply {
            setShadowLayer(12f, 0f, 0f, Color.YELLOW)
        }
    private var itemDragging = false
    private val draggingDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onLongPress(e: MotionEvent) {
            val x = e.x
            val y = e.y


            Log.d(TAG, "onLongPress: onLongPress $x, $y")
            if (rect.contains(x.toInt(), y.toInt())) {
                itemDragging = true
                Log.d(TAG, "onLongPress: rect.contains")
                rectPaint.setShadowLayer(8f, 10f, 10f, Color.GRAY)
                invalidate()
            }
        }
    })

    init {
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay!!

        displayWidth = display.width
        displayHeight = display.height
        val centerX = displayWidth / 2
        val centerY = displayHeight / 2

        invalidate()

        circle = Circle(centerX.toFloat(), centerY.toFloat(), 150f)
        // square.set(centerX, centerY, square.right, square.bottom)

        setLayerType(LAYER_TYPE_SOFTWARE, shadowPaint);
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        Log.d(">>>", "onMeasure: width=$width, height=$height")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> {
                if (itemDragging) {
                    itemDragging = false
                    Log.d(TAG, "onTouchEvent[ACTION_UP]: clearShadowLayer")
                    rectPaint.clearShadowLayer()
                    invalidate()
                    return true
                }
            }
        }

        return draggingDetector.onTouchEvent(event)
            || gestureDetector.onTouchEvent(event)
            || scaleDetector.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return

        canvas.save()

        // if (firstDraw) {
        //     canvasX = 0f
        //     scaleFocusX = width / 2f
        //     canvasY = 0f
        //     scaleFocusY = height / 2f
        //     firstDraw = false
        // }
        Log.d(">>>", "onDraw scale=$scaleFactor(focus=$scaleFocusX, $scaleFocusY),  canvas=$canvasX,$canvasY")
        canvas.scale(scaleFactor, scaleFactor, scaleFocusX, scaleFocusY)

        canvas.translate(canvasX, canvasY)

        canvas.drawRect(rect, rectPaint)
        canvas.drawCircle(circle.x, circle.y, circle.r, paintYellow)
        canvas.drawCircle(circle2.x, circle2.y, circle2.r, paintGreen)
        canvas.drawTriangle(paintBlue, 600, 800, 200)

        canvas.restore()
    }

    fun Canvas.drawTriangle(paint: Paint, x: Int, y: Int, width: Int) {
        val halfWidth = width / 2
        val path = Path()
        path.moveTo(x.toFloat(), (y - halfWidth).toFloat()) // Top
        path.lineTo((x - halfWidth).toFloat(), (y + halfWidth).toFloat()) // Bottom left
        path.lineTo((x + halfWidth).toFloat(), (y + halfWidth).toFloat()) // Bottom right
        path.lineTo(x.toFloat(), (y - halfWidth).toFloat()) // Back to Top
        path.close()
        this.drawPath(path, paint)
    }

    data class Circle(
        val x: Float,
        val y: Float,
        val r: Float,
    )

    companion object {
        const val TAG = ">>>"
    }
}