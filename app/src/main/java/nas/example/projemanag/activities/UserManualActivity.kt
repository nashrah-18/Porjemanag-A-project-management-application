package nas.example.projemanag.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import nas.example.projemanag.R

class UserManualActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_manual)

        // Set up the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_user_manual)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val textView = findViewById<TextView>(R.id.text_view_user_manual)
        val userManualContent = assets.open("user_manual.txt").bufferedReader().use { it.readText() }
        textView.text = userManualContent
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}