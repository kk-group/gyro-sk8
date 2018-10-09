package kk_group.gyrosk8

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

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

    fun setHiScore(usr: String, score: Int) {
        editor!!.putString(HISCORE, "USER: $usr  SCORE: $score")
        editor!!.commit()
    }

    fun getHiScore(): Set<String>  {

        var set = pref!!.getStringSet(HISCORE, null)

        for (i in set) {
            Log.d("DEBUG", i.toString())
        }

        return set
    }
}