package com.littleapp.wordpress.Activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.littleapp.wordpress.Unit.CLASS
import com.littleapp.wordpress.Unit.THEME
import com.littleapp.wordpress.Unit.VOID
import com.littleapp.wordpress.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val context: Context = this

    private val timePerSecond = 2
    private val timeFinal = TIME_PER_MILLIS * timePerSecond

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({ launch() }, timeFinal.toLong())
    }

    private fun launch() {
        VOID.Intent1(context, CLASS.MAIN)
        finish()
    }

    companion object {
        private const val TIME_PER_MILLIS = 1000
    }
}