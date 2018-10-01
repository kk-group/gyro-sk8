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
import kotlinx.android.synthetic.main.sandbox_fragment.*


class SandboxFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var rotationSensor: Sensor? = null
    private var pitchSensor: Sensor? = null
    private var acceleroSensor: Sensor? = null

    private var zSensorValue: Float? = null
    private var zStartValueFromButton: Float? = null
    private var shuvitNumber: Int = 0
    private var zRunningFloat: Float? = null

    private var xSensorValue: Float? = null
    private var xStartValueFromButton: Float? = null
    private var xRunningFloat: Float? = null
    private var kickFlipNumber: Int = 0

    private var pitchValue: Float = 0f
    private var throwBool: Boolean = false
    private var throwCounter: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.sandbox_fragment, container, false)

        this.sensorManager = layoutInflater.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        pitchSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            Log.d("DBG", "Sensor found")
        } else {
            Log.d("DBG", "Sensor not found")
        }

        /**
         * Reset button
         */
        val btn = view.findViewById(R.id.resetButton) as Button

        btn.setOnClickListener {
            zStartValueFromButton = zSensorValue  // hommataan erotus
            xStartValueFromButton = xSensorValue

            shuvitNumber = 0
            kickFlipNumber = 0

            halfFlipCounter.text = shuvitNumber.toString()
            kickFlipCounter.text = kickFlipNumber.toString()
        }

        return view
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == rotationSensor && event != null) {

            /**
             * Initializing sensor data X Y Z
             */
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            tv_yValue.text = String.format("y %.3f", y)

            zSensorValue = event.values[2] // record z value to "global" variable
            xSensorValue = event.values[0] // recording x value to a variable

            /**
             * Kickflip functionality
             */

            if (xStartValueFromButton == null) {
                tv_xValue.text = String.format("x %.3f", x)
            } else {
                tv_xValue.text = String.format("x %.3f", x)

                xRunningFloat = x - xStartValueFromButton!!
                xValueAfterReset.text = String.format("Value X %.3f", xRunningFloat)

                if (xRunningFloat!! > 0.8 || xRunningFloat!! < -0.8) {

                    kickFlipNumber++
                    kickFlipCounter.text = String.format("Damn bro! You've flipped $kickFlipNumber times")
                    xStartValueFromButton = xSensorValue
                }
            }

            /**
             * Shuvit functionality
             */

            if (zStartValueFromButton == null) {
                tv_zValue.text = String.format("z %.3f", z)
            } else {
                tv_zValue.text = String.format("z %.3f", z)

                zRunningFloat = z - zStartValueFromButton!!
                zValueAfterReset.text = String.format("new z %.3f", zRunningFloat)

                if (zRunningFloat!! > 1 || zRunningFloat!! < -1) {
                    shuvitNumber++

                    // Calculating rotation from number of shuvits
                    val rotationCalculation = shuvitNumber * 180

                    halfFlipCounter.text = String.format("Rotations $rotationCalculation° total")
                    String.format("Rotations $rotationCalculation° total")
                    zStartValueFromButton = zSensorValue
                }
            }


        } else {
            Log.d("DBG", "Jotain paskaa meni vikaan")
        }

        // accelerometer sensor
        if (event?.sensor == acceleroSensor && event != null) {
            val z = event.values[2]

            if (z > -1 && z < 1 && !throwBool && (pitchValue > -20 && pitchValue < 20)) {
                throwBool = true
                throwCounter++
                ollieCounter.text = String.format("Ollies: $throwCounter")
            } else if (z > 8.5) {
                throwBool = false
            }
        }

        // pitch sensor
        if (event?.sensor == pitchSensor && event != null) {
            pitchValue = event.values[1]

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()
        rotationSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        acceleroSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        pitchSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}


