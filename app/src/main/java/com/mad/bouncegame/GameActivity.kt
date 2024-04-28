package com.mad.bouncegame

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pl.droidsonroids.gif.GifImageView


class GameActivity : AppCompatActivity() {

    private lateinit var imgDino: GifImageView
    private lateinit var imgTree: GifImageView
    private lateinit var btnJump: Button
    private lateinit var btnStart: Button
    private lateinit var txtScore: TextView
    private lateinit var txtHighScore: TextView


    private val handler = Handler()
    private var treeX = 1500f
    private var dinoBaseY = 0f
    private var dinoY = 0f
    private var isJumping = false
    private var gravity = 1f
    private var jumpVelocity = 0f
    private var gameRunning = false
    private var score = 0

    private val gameRunner = object : Runnable {
        override fun run() {
            if (gameRunning) {
                updateTreePosition()
                handleJumpLogic()
                checkCollision()
                handler.postDelayed(this, 20)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        imgDino = findViewById(R.id.img_dino)
        imgTree = findViewById(R.id.img_tree)
        txtScore = findViewById(R.id.txt_score)
        txtHighScore = findViewById(R.id.txt_highscore)
        btnJump = findViewById(R.id.btn_jump)
        btnStart = findViewById(R.id.btn_start)

        imgDino.visibility = View.INVISIBLE
        imgTree.visibility = View.INVISIBLE

        btnStart.setOnClickListener {
            startGame()
        }

        btnJump.setOnClickListener {
            initiateJump()
        }

        dinoBaseY = imgDino.translationY
        val dbHelper = DatabaseHelper(this)
        val highScore = dbHelper.getHighScore()
        txtHighScore.text = "High Score: $highScore"

    }

    private fun startGame() {
        gameRunning = true
        score = 0
        treeX = 1500f
        txtScore.text = "Score: $score"
        imgDino.visibility = View.VISIBLE
        imgTree.visibility = View.VISIBLE
        handler.post(gameRunner)
    }

    private fun updateTreePosition() {
        treeX -= 10
        if (treeX < -100) {
            treeX = 1500f
            score += 1
            txtScore.text = "Score: $score"
        }
        imgTree.translationX = treeX
    }

    private fun initiateJump() {
        if (!isJumping) {
            jumpVelocity = -35f
            isJumping = true
        }
    }

    private fun handleJumpLogic() {
        if (isJumping) {
            dinoY += jumpVelocity
            jumpVelocity += gravity
            if (dinoY > dinoBaseY) {
                dinoY = dinoBaseY
                isJumping = false
            }
            imgDino.translationY = dinoY
        }
    }

    private fun checkCollision() {
        val dinoRect = Rect()
        val treeRect = Rect()
        imgDino.getHitRect(dinoRect)
        imgTree.getHitRect(treeRect)

        if (Rect.intersects(dinoRect, treeRect)) {
            gameRunning = false
            gameOver()
        }
    }

    private fun gameOver() {
        handler.removeCallbacks(gameRunner)
        imgDino.visibility = View.INVISIBLE
        imgTree.visibility = View.INVISIBLE
        val dbHelper = DatabaseHelper(this)
        dbHelper.addScore(score)  // Save the current score to the database
        val highScore = dbHelper.getHighScore()  // Fetch the high score from the database
        txtScore.text = "Game Over! Score: $score"
        txtHighScore.text = "High Score: $highScore"  // Update the high score display
    }
}
