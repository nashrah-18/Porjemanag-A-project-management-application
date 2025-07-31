package nas.example.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.TextView
import nas.example.projemanag.R
import nas.example.projemanag.firebase.FirestoreClass


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        val tvAppName: TextView = findViewById(R.id.tv_app_name)
        val typeface: Typeface = Typeface.createFromAsset(assets, "Svarga.otf")
        tvAppName.typeface = typeface

        val appName = getString(R.string.app_name)
        tvAppName.text = ""

        val handler = Handler()
        var i = 0
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (i < appName.length) {
                    tvAppName.text = appName.substring(0, i + 1)
                    i++
                    val delay = (50..150).random().toLong() // random delay between 50ms and 150ms
                    handler.postDelayed(this, delay)
                } else {
                    // animation complete, proceed with the rest of the logic
                    Handler().postDelayed({
                        var currentUserID = FirestoreClass().getCurrentUserId()
                        if (currentUserID.isNotEmpty()) {
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        } else {
                            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                        }
                        finish()
                    }, 500)
                }
            }
        }, 100)
    }
}