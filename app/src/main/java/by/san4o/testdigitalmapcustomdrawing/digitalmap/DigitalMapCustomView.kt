package by.san4o.testdigitalmapcustomdrawing.digitalmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
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
import by.san4o.testdigitalmapcustomdrawing.digitalmap.element.Circle
import by.san4o.testdigitalmapcustomdrawing.digitalmap.element.CircleDrawElement
import by.san4o.testdigitalmapcustomdrawing.digitalmap.element.DrawElement
import by.san4o.testdigitalmapcustomdrawing.digitalmap.element.RectDrawElement

class DigitalMapCustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var onElementSelected: (DrawElement) -> Unit = {}
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
    private val canvasMatrix = Matrix()
    private val invertedMatrix = Matrix()

    private val rect = RectF(10f, 50f, 200f, 200f)
    private var circle: Circle = Circle(0f, 0f, 0f)
    private val circle2: Circle = Circle(500f, 300f, 100f)

    private val elements: MutableList<DrawElement> = mutableListOf(
        RectDrawElement("Rectangle1", RectF(10f, 50f, 200f, 200f), paintRed),
        CircleDrawElement("Circle1", Circle(500f, 300f, 100f), paintGreen),
    )

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
            val factor = detector.scaleFactor

            scaleFactor *= factor
            val currentSpan = detector.currentSpan
            val focusX = detector.focusX
            val focusY = detector.focusY
            val currentSpanX = detector.currentSpanX
            val currentSpanY = detector.currentSpanY

            // Don't let the object get too small or too large.
            // TODO Important нужно чтобы фактор зависил и матрица была связана одним органичение, чтобы не расходилось
            scaleFactor = Math.max(1f, Math.min(scaleFactor, 5.0f))
            Log.d(TAG, "onScale: $factor, scaleFactor=$scaleFactor")
            canvasMatrix.postScale(factor, factor, scaleFocusX, scaleFocusY)

            ViewCompat.postInvalidateOnAnimation(this@DigitalMapCustomView)
            return true
        }
    }
    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    private val displayWidth: Int
    private val displayHeight: Int


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
            val x = e2.x
            val y = e2.y

            val offsetX = distanceX / scaleFactor
            val offsetY = distanceY / scaleFactor
            canvasMatrix.postTranslate(-1 * offsetX, -1 * offsetY)

            Log.d(TAG, "onScroll: e1(${e1.x},${e1.y}) e2(${e2.x},${e2.y})")
            Log.d(TAG, "onScroll distanceX=$distanceX, distanceY=$distanceY")
            Log.d(TAG, "onScroll: offset $offsetX, $offsetY")
            ViewCompat.postInvalidateOnAnimation(this@DigitalMapCustomView)

            return true
        }
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    private var draggingElement: DrawElement? = null
    private var selectedElement: DrawElement? = null
    private val draggingDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onLongPress(e: MotionEvent) {
            val x = e.x
            val y = e.y

            Log.d(TAG, "onLongPress: onLongPress $x, $y")
            val point = invertedPoints(x, y)
            val invertedX = point[0]
            val invertedY = point[1]

            draggingElement = elements
                .find { it.contains(invertedX, invertedY) }
                ?.also { element ->
                    element.starDragging()
                    invalidate()
                }
        }
    })

    private fun invertedPoints(x: Float, y: Float): FloatArray {
        val point = floatArrayOf(x, y)
        if (canvasMatrix.invert(invertedMatrix)) {
            invertedMatrix.mapPoints(point)
        }
        return point
    }

    private val selectDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.d(TAG, "onDoubleTap: ${e.x}, ${e.y}")
            val point = invertedPoints(e.x, e.y)
            val invertedX = point[0]
            val invertedY = point[1]

            selectedElement = elements
                .find { it.contains(invertedX, invertedY) }
                ?.also {
                    it.setSelected(true)
                    invalidate()
                    onElementSelected(it)
                }

            return selectedElement != null
        }
    })

    private val centerX: Float
    private val centerY: Float

    init {
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay!!

        displayWidth = display.width
        displayHeight = display.height
        centerX = displayWidth / 2f
        centerY = displayHeight / 2f

        invalidate()

        circle = Circle(centerX.toFloat(), centerY.toFloat(), 150f)
        // square.set(centerX, centerY, square.right, square.bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        Log.d(">>>", "onMeasure: width=$width, height=$height")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val element = selectedElement
                if (element != null) {
                    val points = invertedPoints(x, y)
                    if (!element.contains(points[0], points[1])) {
                        element.setSelected(false)
                        selectedElement = null
                        invalidate()
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (draggingElement != null) {
                    draggingElement?.endDragging()
                    draggingElement = null
                    Log.d(TAG, "onTouchEvent[ACTION_UP]: clearShadowLayer")
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (draggingElement != null) {
                    val touch = invertedPoints(x, y)
                    draggingElement?.dragTo(touch[0], touch[1])

                    invalidate()
                    return true
                }
            }
        }

        return selectDetector.onTouchEvent(event)
            || draggingDetector.onTouchEvent(event)
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
        Log.d(">>>", "onDraw scale=$scaleFactor(focus=$scaleFocusX, $scaleFocusY)")
//        canvas.scale(scaleFactor, scaleFactor, scaleFocusX, scaleFocusY)


        canvas.setMatrix(canvasMatrix)

        elements.forEach { it.draw(canvas) }
//        canvas.drawRect(rect, rectPaint)
//        canvas.drawCircle(circle.x, circle.y, circle.r, paintYellow)
//        canvas.drawCircle(circle2.x, circle2.y, circle2.r, paintGreen)
//        canvas.drawTriangle(paintBlue, 600, 800, 200)

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

    fun setDetails(details: ElementDetails) {
        selectedElement?.also { el ->
            el.color = details.color
            el.name = details.name
            el.setSelected(false)
            selectedElement = null
            invalidate()
        }
    }

    fun rotateSelected(rotate: Float) {

        selectedElement?.also { el ->
            el.rotate(rotate)
            el.setSelected(false)
            selectedElement = null
            invalidate()
        }
    }

    fun removeSelected() {
        selectedElement?.also { el ->
            elements.remove(el)
            selectedElement = null
            invalidate()
        }
    }

    fun addFigure(figure: ElementFigure) {
        elements.add(
            when (figure) {
                ElementFigure.Rectangle ->
                    RectDrawElement(
                        name = "rect",
                        rect = rectByCenter(centerX, centerY, 200f, 300f),
                        paint = Paint().apply {
                            color = Color.RED
                        }
                    )

                ElementFigure.Square ->
                    RectDrawElement(
                        name = "square",
                        rect = rectByCenter(centerX, centerY, 300f, 300f),
                        paint = Paint().apply {
                            color = Color.RED
                        }
                    )
                ElementFigure.Circle ->
                    CircleDrawElement(
                        name = "name",
                        circle = Circle(centerX, centerY, 200f),
                        paint = Paint().apply {
                            color = Color.RED
                        }
                    )
            }
        )

        invalidate()
    }

    fun rotate(value: Int) {
        canvasMatrix.postRotate(value.toFloat(), centerX, centerY)
        invalidate()
    }

    companion object {
        const val TAG = ">>>"
    }
}

private fun Rect.newCenter(x: Int, y: Int) {
    val centerXPart = centerX() - left
    val centerYPart = centerY() - top
    val left = x - centerXPart
    val top = y - centerYPart
    val right = x + centerXPart
    val bottom = y + centerXPart
    set(left, top, right, bottom)
}

