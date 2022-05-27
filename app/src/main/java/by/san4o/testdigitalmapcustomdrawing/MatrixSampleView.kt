package by.san4o.testdigitalmapcustomdrawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class MatrixSampleView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    var p: Paint? = null
    var path: Path = Path()
    var canvasMatrix: Matrix = Matrix()

    val rect = RectF(50f, 50f, 200f, 200f)
    var rectPaint = Paint()
        .apply { this.color = Color.RED }
    var rectMatrix: Matrix = Matrix()

    init {
        p = Paint()
        p!!.strokeWidth = 3f
        p!!.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawARGB(80, 102, 204, 255)

        // создаем крест в path
        path!!.reset()
        path!!.addRect(300f, 150f, 450f, 200f, Path.Direction.CW)
        path!!.addRect(350f, 100f, 400f, 250f, Path.Direction.CW)
        path!!.addCircle(375f, 125f, 5f, Path.Direction.CW)

        // рисуем path зеленым
        p!!.color = Color.GREEN
        canvas.drawPath(path!!, p!!)

        // настраиваем матрицу на поворот на 120 градусов
        // относительно точки (600,400)
        canvasMatrix!!.reset()
        canvasMatrix!!.setRotate(120f, 600f, 400f)

        // применяем матрицу к path
        path!!.transform(canvasMatrix!!)

        // рисуем path синим
        p!!.color = Color.BLUE
        canvas.drawPath(path!!, p!!)

        // рисуем точку, относительно которой был выполнен поворот
        p!!.color = Color.BLACK
        canvas.drawCircle(600f, 400f, 5f, p!!)

        path.reset()
        path.addRect(rect, Path.Direction.CW)
        rectMatrix.setRotate(45f, rect.centerX(), rect.centerY())
        path.transform(rectMatrix)

        canvas.drawPath(path, rectPaint)
    }

}