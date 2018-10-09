package kk_group.gyrosk8

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kk_group.gyrosk8.Database.HiScoreData
import kk_group.gyrosk8.Database.HiScoreDatabase
import kotlinx.android.synthetic.main.activity_hi_score.*

class HiScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hi_score)

        hiscoresBtn.setOnClickListener {
            val db = HiScoreDatabase.get(this)

            db.hiScoreDao().insert(HiScoreData(1, "AAA", 20))
            db.hiScoreDao().insert(HiScoreData(2, "AAB", 15))

            hiscoresTxt.text = String.format("")
        }
    }
}
