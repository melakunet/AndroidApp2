package com.melakunet.androidapp2.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.melakunet.androidapp2.R

/**
 * Animated coffee-themed background.
 *
 * Draws the three-stop coffee gradient and floats several translucent "steam"
 * ovals upward across it. Each wisp rises, sways left and right, and stretches
 * taller as it climbs, which mimics steam expanding in open air.
 *
 * Drop this in as the FIRST child of a screen's layout so it sits behind
 * everything else. It never handles touches, so buttons on top still work.
 */
class CoffeeSmokeBackground @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * Settings for one steam puff. Keeping these in a data class means every
     * wisp gets its own size, speed and timing, so they never move in lockstep.
     */
    private data class Wisp(
        val xOffsetDp: Int,     // horizontal starting position, relative to centre
        val sizeDp: Int,        // diameter of the oval
        val riseMillis: Long,   // time for one bottom-to-top journey
        val delayMillis: Long,  // stagger so puffs don't all start together
        val swayDp: Int,        // how far left/right it drifts while rising
        val swayMillis: Long    // time for one left-right swing
    )

    // Hand-tuned so the screen always has a few puffs at different heights.
    private val wispConfigs = listOf(
        Wisp(-120, 90, 6000, 0, 22, 2400),
        Wisp(-45, 70, 7500, 1300, 18, 3100),
        Wisp(20, 110, 5500, 600, 28, 2800),
        Wisp(100, 80, 8000, 2100, 20, 2200),
        Wisp(-95, 60, 6500, 3200, 16, 3500),
        Wisp(55, 95, 7000, 1900, 24, 2600),
        Wisp(135, 75, 5800, 2600, 19, 3000),
        Wisp(-20, 65, 8500, 900, 21, 2900)
    )

    // Kept so every animation can be stopped when the view leaves the screen.
    private val runningAnimators = mutableListOf<Animator>()

    init {
        // The gradient is the view's own background; wisps are drawn on top of it.
        setBackgroundResource(R.drawable.bg_coffee_gradient)
        // Let taps fall through to whatever sits above this view.
        isClickable = false
        isFocusable = false
    }

    /**
     * The wisps need to know the view's height before they can travel from the
     * bottom edge to above the top edge, so they are built here rather than in
     * init. The childCount guard stops them being rebuilt on every rotation.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (childCount == 0 && height > 0) {
            wispConfigs.forEach { addWisp(it, height) }
        }
    }

    /** Creates one steam oval and starts its three looping animations. */
    private fun addWisp(config: Wisp, viewHeight: Int) {
        val sizePx = dpToPx(config.sizeDp)

        val wispView = ImageView(context).apply {
            setImageResource(R.drawable.smoke_wisp)
            layoutParams = LayoutParams(sizePx, sizePx, Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
            translationX = dpToPx(config.xOffsetDp).toFloat()
            // Start just below the bottom edge so it drifts into view.
            translationY = viewHeight.toFloat()
        }
        addView(wispView)

        val travel = viewHeight.toFloat()

        // 1. Rise: bottom edge to above the top edge, then jump back and repeat.
        val rise = ObjectAnimator.ofFloat(wispView, TRANSLATION_Y, travel, -travel).apply {
            duration = config.riseMillis
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            startDelay = config.delayMillis
        }

        // 2. Sway: drift side to side at a different speed so the motion looks organic.
        val startX = dpToPx(config.xOffsetDp).toFloat()
        val swayDistance = dpToPx(config.swayDp).toFloat()
        val sway = ObjectAnimator.ofFloat(
            wispView, TRANSLATION_X, startX - swayDistance, startX + swayDistance
        ).apply {
            duration = config.swayMillis
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            startDelay = config.delayMillis
        }

        // 3. Stretch: the puff grows taller as it rises, like real steam spreading.
        val stretch = ObjectAnimator.ofFloat(wispView, SCALE_Y, 1f, 2.2f).apply {
            duration = config.riseMillis
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            startDelay = config.delayMillis
        }

        listOf(rise, sway, stretch).forEach {
            it.start()
            runningAnimators.add(it)
        }
    }

    /** Stops every animation so they don't keep running (and leaking) off-screen. */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        runningAnimators.forEach { it.cancel() }
        runningAnimators.clear()
        removeAllViews()
    }

    /** Converts a density-independent size into real pixels for this device. */
    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()
}