package com.mad.bouncegame

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.os.Handler

class GameView(context: Context) : View(context), View.OnClickListener {
    private enum class GameState { START, RUNNING, GAME_OVER }
    private var gameState = GameState.START

    private val dinoBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bounce)
    private val treeBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.tiger)
    private val paint: Paint = Paint()
    private var dinoX = 100f
    private var dinoY = 500f
    private var treeX = 2000f
    private var treeSpeed = 20f
    private var score = 0
    private var highScore = 0
    private val dbHandler = DatabaseHelper(context)
    private val handler = Handler()
    private var dinoVelocity = 0f
    private val gravity = 2f
    private val groundY = 500f
    private var isJumping = false

    private val startButton = RectF(100f, 400f, 500f, 600f)
    private val replayButton = RectF(100f, 700f, 500f, 900f)

    private val updateRunner = object : Runnable {
        override fun run() {
            if (gameState == GameState.RUNNING) {
                if (isJumping) {
                    dinoVelocity += gravity
                    dinoY += dinoVelocity
                    if (dinoY > groundY) {
                        dinoY = groundY
                        isJumping = false
                        dinoVelocity = 0f
                    }
                }

                treeX -= treeSpeed
                if (treeX + treeBitmap.width < 0) {
                    treeX = width.toFloat()
                    score++
                }

                if (Rect.intersects(Rect(dinoX.toInt(), dinoY.toInt(), (dinoX + dinoBitmap.width).toInt(), (dinoY + dinoBitmap.height).toInt()),
                        Rect(treeX.toInt(), groundY.toInt(), (treeX + treeBitmap.width).toInt(), (groundY + treeBitmap.height).toInt()))) {
                    gameState = GameState.GAME_OVER
                    highScore = dbHandler.getHighScore()
                    if (score > highScore) {
                        dbHandler.addScore(score)
                        highScore = score
                    }
                } else {
                    invalidate()
                }
            }
            handler.postDelayed(this, 30)
        }
    }

    init {
        handler.postDelayed(updateRunner, 30)
        highScore = dbHandler.getHighScore() // Load high score at startup
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (gameState) {
            GameState.START -> drawStartScreen(canvas)
            GameState.RUNNING -> drawGame(canvas)
            GameState.GAME_OVER -> drawGameOverScreen(canvas)
        }
    }

    private fun drawStartScreen(canvas: Canvas) {
        paint.textSize = 80f
        paint.color = Color.BLACK
        canvas.drawText("Tap to Start", 150f, 300f, paint)
        paint.color = Color.GRAY
        canvas.drawRect(startButton, paint)
        paint.color = Color.WHITE
        canvas.drawText("Start", startButton.left + 100, startButton.bottom - 50, paint)
    }

    private fun drawGameOverScreen(canvas: Canvas) {
        paint.textSize = 80f
        paint.color = Color.BLACK
        canvas.drawText("Game Over", 150f, 300f, paint)
        canvas.drawText("Score: $score", 150f, 400f, paint)
        canvas.drawText("High Score: $highScore", 150f, 500f, paint)
        paint.color = Color.GRAY
        canvas.drawRect(replayButton, paint)
        paint.color = Color.WHITE
        canvas.drawText("Replay", replayButton.left + 100, replayButton.bottom - 50, paint)
    }

    private fun drawGame(canvas: Canvas) {
        canvas.drawBitmap(dinoBitmap, dinoX, dinoY, paint)
        canvas.drawBitmap(treeBitmap, treeX, groundY, paint)
        paint.color = Color.BLACK
        paint.textSize = 60f
        canvas.drawText("Score: $score", 50f, 100f, paint)
    }

    override fun onClick(v: View?) {
        when (gameState) {
            GameState.START -> {
                gameState = GameState.RUNNING
                startGame()
            }
            GameState.GAME_OVER -> {
                gameState = GameState.RUNNING
                startGame()
            }
            else -> {}
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            when {
                gameState == GameState.RUNNING && !isJumping -> {
                    isJumping = true
                    dinoVelocity = -30f
                }
                gameState == GameState.GAME_OVER && replayButton.contains(event.x, event.y) -> {
                    gameState = GameState.RUNNING
                    startGame()
                }
                gameState == GameState.START && startButton.contains(event.x, event.y) -> {
                    gameState = GameState.RUNNING
                    startGame()
                }
            }
        }
        return true
    }

    private fun startGame() {
        score = 0
        treeX = width.toFloat()
        dinoY = groundY
        handler.postDelayed(updateRunner, 30)
    }
}
