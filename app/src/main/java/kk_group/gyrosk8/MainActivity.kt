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

        btnShowSlider.setOnClickListener {
            PrefManager(applicationContext).setLaunched(true)
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }

        btnSkate.setOnClickListener {
//            supportFragmentManager.beginTransaction().add(R.id.root_layout, SandboxFragment()).commit()
            startActivity(Intent(this, SkateActivity::class.java))
//            btnShowSlider.visibility = View.GONE
//            btnSkate.visibility = View.GONE
        }



    }
}
