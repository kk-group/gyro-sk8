package kk_group.gyrosk8

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_hi_score.*

class HiScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_score)

        val prefManager = PrefManager(this)

        hiscoresTxt.text = String.format(prefManager.getTopScore()!!)

        hiscoresBtn.setOnClickListener {

        }

    }

}