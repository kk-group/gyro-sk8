package kk_group.gyrosk8

import android.content.Context
import android.hardware.Sensor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.sandbox_fragment.*


class SandboxFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null

    private var zValue: Float? = null
    private var zShit: Float? = null
    private var halfFlipNumber: Int = 0

    private var paska: Float? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.sandbox_fragment, container, false)

        this.sensorManager = layoutInflater.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            Log.d("DBG", "Sensor found")
        } else {
            Log.d("DBG", "Sensor not found")
        }

        val btn = view.findViewById(R.id.resetButton) as Button
        val captureTxt = view.findViewById(R.id.captureText) as TextView

        btn.setOnClickListener {
            zShit = zValue  // hommataan erotus

            captureTxt.text = zValue.toString()
            
            halfFlipNumber = 0
            halfFlipCounter.text = halfFlipNumber.toString()
        }

        return view
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == rotationSensor && event != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            zValue = event.values[2] // record z value to "global" variable

            textView3.text = String.format("x %.3f", x)
            textView4.text = String.format("y %.3f", y)

            if (zShit == null) {
                textView5.text = String.format("z %.3f", z)
            } else {
                textView5.text = String.format("z %.3f", z)

                paska = z - zShit!!
                resettedZ.text = String.format("new z %.3f", paska)

                if (paska!! > 1 || paska!! < -1) {

                    halfFlipNumber++
                    halfFlipCounter.text = halfFlipNumber.toString()

                    zShit = zValue
                }
            }


        } else {
            Log.d("DBG", "Jotain paskaa meni vikaan")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()
        rotationSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}


