package by.san4o.testdigitalmapcustomdrawing.interactiveview

import android.R
import android.content.Context
import android.os.SystemClock
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

/**
 * A simple class that animates double-touch zoom gestures. Functionally similar to a [ ].
 */
class Zoomer(context: Context) {
    /**
     * The interpolator, used for making zooms animate 'naturally.'
     */
    private val mInterpolator: Interpolator

    /**
     * The total animation duration for a zoom.
     */
    private val mAnimationDurationMillis: Int

    /**
     * Whether or not the current zoom has finished.
     */
    private var mFinished = true
    /**
     * Returns the current zoom level.
     *
     * @see android.widget.Scroller.getCurrX
     */
    /**
     * The current zoom value; computed by [.computeZoom].
     */
    var currZoom = 0f
        private set

    /**
     * The time the zoom started, computed using [android.os.SystemClock.elapsedRealtime].
     */
    private var mStartRTC: Long = 0

    /**
     * The destination zoom factor.
     */
    private var mEndZoom = 0f

    /**
     * Forces the zoom finished state to the given value. Unlike [.abortAnimation], the
     * current zoom value isn't set to the ending value.
     *
     * @see android.widget.Scroller.forceFinished
     */
    fun forceFinished(finished: Boolean) {
        mFinished = finished
    }

    /**
     * Aborts the animation, setting the current zoom value to the ending value.
     *
     * @see android.widget.Scroller.abortAnimation
     */
    fun abortAnimation() {
        mFinished = true
        currZoom = mEndZoom
    }

    /**
     * Starts a zoom from 1.0 to (1.0 + endZoom). That is, to zoom from 100% to 125%, endZoom should
     * by 0.25f.
     *
     * @see android.widget.Scroller.startScroll
     */
    fun startZoom(endZoom: Float) {
        mStartRTC = SystemClock.elapsedRealtime()
        mEndZoom = endZoom
        mFinished = false
        currZoom = 1f
    }

    /**
     * Computes the current zoom level, returning true if the zoom is still active and false if the
     * zoom has finished.
     *
     * @see android.widget.Scroller.computeScrollOffset
     */
    fun computeZoom(): Boolean {
        if (mFinished) {
            return false
        }
        val tRTC = SystemClock.elapsedRealtime() - mStartRTC
        if (tRTC >= mAnimationDurationMillis) {
            mFinished = true
            currZoom = mEndZoom
            return false
        }
        val t = tRTC * 1f / mAnimationDurationMillis
        currZoom = mEndZoom * mInterpolator.getInterpolation(t)
        return true
    }

    init {
        mInterpolator = DecelerateInterpolator()
        mAnimationDurationMillis = context.resources.getInteger(
            R.integer.config_shortAnimTime
        )
    }
}