package kk_group.gyrosk8

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private val IS_LAUNCHED = "IsLaunched"
    private val KEY_HISCORE = "hiscore"
    private val KEY_USER = "user"

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

    fun setTopScore(score: Int) {
        editor!!.putInt(KEY_HISCORE, score)
        editor!!.commit()
    }

    fun setTopUser(user: String) {
        editor!!.putString(KEY_USER, user)
        editor!!.commit()
    }

    fun getTopScore(): Int {
        return pref!!.getInt(KEY_HISCORE, 0)
    }

    fun getTopUser(): String? {
        return pref!!.getString(KEY_USER, "NONAME")
    }

    fun checkTopScore(totalScore: Int):  Boolean {
        var currentTopScore= pref!!.getInt(KEY_HISCORE, 0)

        return totalScore > currentTopScore
    }

}