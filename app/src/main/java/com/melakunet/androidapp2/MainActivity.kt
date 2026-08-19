package com.melakunet.androidapp2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
 * Single host activity for the Tim Hortons Coffee Run app.
 *
 * Edge-to-edge is enabled so the coffee gradient runs behind the status and
 * navigation bars. Padding for those bars is applied to the content on top of
 * the background rather than to the root, which would crop the gradient.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
}