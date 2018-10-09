package kk_group.gyrosk8

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class PrefManager(context: Context) {
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private val IS_LAUNCHED = "IsLaunched"
    private val HISCORE = "hiscore"

    private lateinit var hiScoreList: Set<String>
    private lateinit var context: Context

    init {
        pref = context.getSharedPreferences("gyro-sk8", Context.MODE_PRIVATE)
        editor = pref!!.edit()
        this.context = context
    }

    fun setLaunched(isFirstTime: Boolean) {
        editor!!.putBoolean(IS_LAUNCHED, isFirstTime)
        editor!!.commit()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref!!.getBoolean(IS_LAUNCHED, true)
    }

    fun setTopScore(usr: String, score: Int) {
        var topScore = "USER: $usr - POINTS: $score"
        editor!!.putInt(HISCORE, score)
        editor!!.putString(HISCORE, topScore)
    }

    fun getTopScore(): String? {
        return pref!!.getString(HISCORE, "NO TOP SCORE FOUND")
    }

    fun checkTopScore(totalScore: Int):  Boolean {
        var currentTopScore  = pref!!.getInt(HISCORE, 0)

        return totalScore > currentTopScore
    }

}