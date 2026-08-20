package com.melakunet.androidapp2.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.melakunet.androidapp2.R
import kotlin.math.PI
import kotlin.math.sin

/**
 * Animated coffee-themed background.
 *
 * Draws the three-stop coffee gradient and floats translucent "steam" puffs
 * upward across it. Each puff rises, sways side to side, stretches taller as it
 * climbs, and fades in and out at the ends of its journey.
 *
 * The puffs are painted straight onto the canvas rather than being separate
 * child views. Every frame calculates each puff's position from how long the
 * view has been on screen, so the animation is driven entirely by elapsed time.
 *
 * Place this first in a screen's layout so it sits behind everything else.
 */
class CoffeeSmokeBackground @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * Settings for one steam puff. Giving each puff its own size, speed and
     * timing stops them all moving in lockstep.
     *
     * @param xOffsetDp horizontal position relative to the centre of the screen
     * @param radiusDp size of the puff
     * @param riseMillis time for one full bottom-to-top journey
     * @param delayMillis head start so puffs do not all begin together
     * @param swayDp how far it drifts left and right while rising
     * @param swayMillis time for one complete left-right swing
     */
    private data class Wisp(
        val xOffsetDp: Int,
        val radiusDp: Int,
        val riseMillis: Long,
        val delayMillis: Long,
        val swayDp: Int,
        val swayMillis: Long
    )

    // Hand-tuned so there are always puffs at different heights on screen.
    private val wisps = listOf(
        Wisp(-120, 45, 6000, 0, 22, 2400),
        Wisp(-45, 35, 7500, 1300, 18, 3100),
        Wisp(20, 55, 5500, 600, 28, 2800),
        Wisp(100, 40, 8000, 2100, 20, 2200),
        Wisp(-95, 30, 6500, 3200, 16, 3500),
        Wisp(55, 48, 7000, 1900, 24, 2600),
        Wisp(135, 38, 5800, 2600, 19, 3000),
        Wisp(-20, 33, 8500, 900, 21, 2900)
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** One shader per puff, since a radial gradient is tied to a fixed radius. */
    private val shaders = mutableMapOf<Int, Shader>()

    /** Reference point for the animation clock, set the first time we draw. */
    private var startMillis = 0L

    init {
        setBackgroundResource(R.drawable.bg_coffee_gradient)
        isClickable = false
        isFocusable = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (startMillis == 0L) {
            startMillis = System.currentTimeMillis()
        }
        val elapsed = System.currentTimeMillis() - startMillis

        val centreX = width / 2f

        wisps.forEach { wisp ->
            val radius = dpToPx(wisp.radiusDp)

            // How far through its rise this puff is, as a value from 0 to 1.
            // The modulo makes it loop back to the bottom automatically.
            val progress =
                ((elapsed + wisp.delayMillis) % wisp.riseMillis).toFloat() / wisp.riseMillis

            // Travel from just below the bottom edge to just above the top edge.
            val y = (height + radius) - progress * (height + radius * 2f)

            // Sway follows a sine wave, giving a smooth drift rather than a
            // sudden change of direction at each end.
            val swayPhase = (elapsed % wisp.swayMillis).toFloat() / wisp.swayMillis
            val x = centreX + dpToPx(wisp.xOffsetDp) +
                    dpToPx(wisp.swayDp) * sin(swayPhase * 2f * PI).toFloat()

            // Fade in at the bottom and out at the top so puffs never pop into
            // or out of existence at the screen edges.
            val fade = sin(progress * PI).toFloat()

            paint.shader = shaderFor(radius)
            // Use a lower alpha for a subtle, barely-there steam effect
            val alpha = (fade * 38f).toInt().coerceIn(0, 255)
            paint.alpha = alpha

            // Each wisp is drawn twice: once as an oval, and again taller to
            // give it that vertical steam-like stretch.
            if (radius > 0) {
                canvas.save()
                canvas.translate(x, y)
                canvas.scale(1f, 1f + progress * 1.5f)
                canvas.drawCircle(0f, 0f, radius.toFloat(), paint)
                canvas.restore()
            }
        }

        // Loop the animation.
        postInvalidateOnAnimation()
    }

    /**
     * Reuses radial shaders for puffs of the same size to save on allocations
     * during the frame loop.
     */
    private fun shaderFor(radiusPx: Int): Shader {
        val safeRadius = if (radiusPx <= 0) 1f else radiusPx.toFloat()
        return shaders.getOrPut(radiusPx) {
            RadialGradient(
                0f, 0f, safeRadius,
                intArrayOf(Color.WHITE, Color.TRANSPARENT),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
    }

    /** Converts a density-independent size into real pixels for this device. */
    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()
}
