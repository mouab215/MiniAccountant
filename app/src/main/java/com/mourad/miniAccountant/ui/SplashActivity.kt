package com.mourad.miniAccountant.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.mourad.miniAccountant.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(
                Intent(
                    this@SplashActivity,
                    JobsActivity::class.java
                )
            )
            finish()
        }, 1000)
    }
}
