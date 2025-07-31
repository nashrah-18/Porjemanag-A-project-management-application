package nas.example.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import nas.example.projemanag.R


class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val tvIntroName: TextView = findViewById(R.id.tv_app_name_intro)
        val typeface: Typeface = Typeface.createFromAsset(assets, "Svarga.otf")
        tvIntroName.typeface = typeface

        val btnSignInIntro: Button = findViewById(R.id.btn_sign_in_intro)
        btnSignInIntro.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        val btnSignUpIntro: Button = findViewById(R.id.btn_sign_up_intro)
        btnSignUpIntro.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

    }

}