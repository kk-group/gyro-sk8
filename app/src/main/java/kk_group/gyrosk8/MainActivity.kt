package kk_group.gyrosk8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSkate.setOnClickListener {
            startActivity(Intent(this, SkateActivity::class.java))
        }

        btnHiScores.setOnClickListener{
            startActivity(Intent(this, HiScoreActivity::class.java))
        }

        btnSettings.setOnClickListener{
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        btnShowSlider.setOnClickListener {
            PrefManager(applicationContext).setLaunched(true)
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }

    }
}
