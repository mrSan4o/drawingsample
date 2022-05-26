package by.san4o.testdigitalmapcustomdrawing.interactiveview


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import by.san4o.testdigitalmapcustomdrawing.R

class InteractiveGraphActivity : AppCompatActivity() {
    private var mGraphView: InteractiveLineGraphView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interactive_view)
        mGraphView = findViewById<View>(R.id.chart) as InteractiveLineGraphView
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_zoom_in -> {
                mGraphView!!.zoomIn()
                return true
            }
            R.id.action_zoom_out -> {
                mGraphView!!.zoomOut()
                return true
            }
            R.id.action_pan_left -> {
                mGraphView!!.panLeft()
                return true
            }
            R.id.action_pan_right -> {
                mGraphView!!.panRight()
                return true
            }
            R.id.action_pan_up -> {
                mGraphView!!.panUp()
                return true
            }
            R.id.action_pan_down -> {
                mGraphView!!.panDown()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}