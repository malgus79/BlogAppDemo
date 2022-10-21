package com.blogappdemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
