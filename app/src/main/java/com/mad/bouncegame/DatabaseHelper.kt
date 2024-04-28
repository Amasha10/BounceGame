package com.mad.bouncegame

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "gameDatabase", null, 1) {
    companion object {
        private const val TABLE_SCORES = "scores"
        private const val COLUMN_ID = "id"
        private const val COLUMN_SCORE = "score"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = "CREATE TABLE $TABLE_SCORES ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_SCORE INTEGER)"
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCORES")
        onCreate(db)
    }

    fun addScore(score: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_SCORE, score)
        db.insert(TABLE_SCORES, null, values)
        db.close()
    }
    fun clearScores() {
        val db = this.writableDatabase
        db.delete(TABLE_SCORES, null, null)
        db.close()
    }


    @SuppressLint("Range")
    fun getHighScore(): Int {
        val db = this.readableDatabase
        val query = "SELECT MAX($COLUMN_SCORE) AS highscore FROM $TABLE_SCORES"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val highScore = cursor.getInt(cursor.getColumnIndex("highscore"))
            cursor.close()
            db.close()
            return highScore
        }
        cursor.close()
        db.close()
        return 0
    }
}
