package com.mad.bouncegame

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast


class MenuActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DatabaseHelper(this)

        // Back button listener
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener {
            finish() // This will close the current activity and return to the previous one
        }

        // Play button listener
        findViewById<Button>(R.id.playBtn).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        // Exit button listener
        findViewById<Button>(R.id.exitBtn).setOnClickListener {
            finishAffinity() // This will close the app
        }

        findViewById<Button>(R.id.resetBtn).setOnClickListener {
            dbHelper.clearScores()
            // Optionally show a toast message to confirm reset
            Toast.makeText(this, "High scores reset successfully.", Toast.LENGTH_SHORT).show()
        }
    }
}
