package kk_group.gyrosk8

import android.content.Context
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class SliderAdapter(internal var context: Context) : PagerAdapter() {

    internal var layoutInflater: LayoutInflater? = null

    //Arrays
    var slide_images = intArrayOf()
    /**
     * Tähän tulee R.drawable.ikonit
     */

    var slide_headings = arrayOf(

            "WELCOME TO GYRO-SK8",
            "DISCLAIMER",
            "LET ME PLAY")

    var slide_descs = arrayOf(

            "Welcome to GYRO-SK8" + "jees",
            "I understand and agree that playing Gyro-Sk8 application is entirely at my own risk." +
                    "I understand and agree that the author of Gyro-Sk8 shall in no event be liable for any direct, indirect, incidental, consequential, or exemplary damages or injuries",
            "I'm not a pussy, let me play!")

    override fun getCount(): Int {
        return slide_headings.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        //layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slide_layout, container, false)

        val slideImageView = view.findViewById<View>(R.id.slide_image) as ImageView
        val slideHeading = view.findViewById<View>(R.id.slide_heading) as TextView
        val slideDescription = view.findViewById<View>(R.id.slide_description) as TextView

        slideImageView.setImageResource(slide_images[position])
        slideHeading.text = slide_headings[position]
        slideDescription.text = slide_descs[position]

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}
