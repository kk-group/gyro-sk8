package kk_group.gyrosk8

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_skate.*

class SkateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skate)

        val bundl = Bundle()

        val skateFragment  = SandboxFragment()

        btnLeft.setOnClickListener {
            bundl.putBoolean("stance", false)
            skateFragment.setArguments(bundl)

            supportFragmentManager.beginTransaction().add(R.id.skate_layout, skateFragment).commit()
            btnLeft.visibility = View.GONE
            btnRight.visibility = View.GONE
        }

        btnRight.setOnClickListener {
            bundl.putBoolean("stance", true)
            skateFragment.setArguments(bundl)

            supportFragmentManager.beginTransaction().add(R.id.skate_layout, skateFragment).commit()
            btnLeft.visibility = View.GONE
            btnRight.visibility = View.GONE
        }
    }
}
