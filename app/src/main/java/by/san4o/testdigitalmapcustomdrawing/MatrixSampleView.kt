package by.san4o.testdigitalmapcustomdrawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MatrixSampleView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    var p: Paint = Paint()
        .apply {
            setStrokeWidth(3f)
            setStyle(Paint.Style.STROKE)
        }
    var path: Path = Path()
    var customMatrix: Matrix = Matrix()

    override fun onDraw(canvas: Canvas) {
        canvas.drawARGB(80, 102, 204, 255)

        // создаем крест в path
        path.reset()
        path.addRect(300F, 150F, 450F, 200F, Path.Direction.CW)
        path.addRect(350F, 100F, 400F, 250F, Path.Direction.CW)

        // рисуем path зеленым
        p.setColor(Color.GREEN)
        canvas.drawPath(path, p)

        // настраиваем матрицу на изменение размера:
        // в 2 раза по горизонтали
        // в 2,5 по вертикали
        // относительно точки (375, 100)
        customMatrix.reset()
        customMatrix.setScale(2f, 2.5f, 375F, 100F)

        // применяем матрицу к path
        path.transform(customMatrix)

        // рисуем path синим
        p.setColor(Color.BLUE)
        canvas.drawPath(path, p)

        // рисуем точку относительно которой было выполнено преобразование
        p.setColor(Color.BLACK)
        canvas.drawCircle(375F, 100F, 5F, p)
    }
}