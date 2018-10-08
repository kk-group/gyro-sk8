package kk_group.gyrosk8

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.widget.Button
import kotlinx.android.synthetic.main.sandbox_fragment.*


class SandboxFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    // sensors
    private var rotationSensor: Sensor? = null
    private var pitchSensor: Sensor? = null
    private var acceleroSensor: Sensor? = null
    private var gyroSensor: Sensor? = null
    private var gravitySensor: Sensor? = null

    // rotation sensor values
    private var zSensorValue: Float? = null
    private var zStartValueFromButton: Float? = null
    private var zRunningFloat: Float? = null

    private var xSensorValue: Float? = null
    private var xStartValueFromButton: Float? = null
    private var xRunningFloat: Float? = null

    // orientation
    private var pitchValue: Float = 0f
    private var rollValue: Float = 0f

    // booleans for checking purposes
    private var throwBool: Boolean = false
    private var flipBool: Boolean = false

    // variables for different trick
    private var ollieNumber: Int = 0
    private var shuvitNumber: Int = 0
    private var kickFlipNumber: Int = 0

    // accelerometer values
    private var xAccelero: Float = 0f
    private var yAccelero: Float = 0f
    private var zAccelero: Float = 0f

    // gyroscope
    private var xGyro: Float = 0f
    private var yGyro: Float = 0f
    private var zGyro: Float = 0f

    // gravity
    private var xGravity: Float = 0f
    private var yGravity: Float = 0f
    private var zGravity: Float = 0f

    /**
     * stance is the hand the player is using
     * FALSE = regular / left hand
     * TRUE = goofy / right hand
     */
    private var stance: Boolean = true

    private var flipCount = 0
    private var flipNum = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.sandbox_fragment, container, false)

        sensorManager = layoutInflater.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        pitchSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)


        /**
         * Reset button
         */
        val btn = view.findViewById(R.id.resetButton) as Button

        btn.setOnClickListener {
            zStartValueFromButton = zSensorValue  // hommataan erotus
            xStartValueFromButton = xSensorValue

            shuvitNumber = 0
            kickFlipNumber = 0
            ollieNumber = 0
            flipCount = 0

            halfFlipCounter.text = String.format("Rotation: $shuvitNumber")
            kickFlipCounter.text = String.format("Flips: $kickFlipNumber")
            ollieCounter.text = String.format("Ollies: $ollieNumber")
            flipCountText.text = String.format("flips: $flipCount")
        }

        val stanceBtn = view.findViewById(R.id.stanceButton) as Button

        stanceBtn.setOnClickListener {
            stance = !stance
            changeStance()
        }

        return view
    }

    /**
     * function that checks if device is somewhat flat
     */
    private fun checkFlat(): Boolean {
        if ((pitchValue < 20 && pitchValue > -20) && (rollValue < 20 && rollValue > -20)) {
            return true
        }

        return false
    }

    /**
     * function for changing stance (hand)
     */
    private fun changeStance() {
        if (stance) {
            stancetext.text = String.format("current stance: right")
        }  else {
            stancetext.text = String.format("current stance: left")
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {

        // check if device is flat and change background color according to it
        if (checkFlat()) {
            relativeLayout.setBackgroundColor(Color.parseColor("#33FF41")) // green
        } else {
            relativeLayout.setBackgroundColor(Color.parseColor("#FF5733")) // red
        }

        if (checkFlat()) {
            flipNum = 0
            flipBool = false
        }

        if (event?.sensor == gravitySensor && event != null) {

            /**
             * TRICK
             * flip to the right side
             * if stance = true = kickflip
             * if stance = false =  heelflip
             */
            if (zGravity > 9 && !flipBool) {
                flipNum = 0
            } else if (xGravity < -7 && flipNum == 0) {
                flipNum = 1
            } else if (zGravity < -7 && flipNum == 1 && (zAccelero < 1 && zGyro > 2)) {
                flipNum = 2
            } else if (xGravity > 7 && flipNum == 2) {
                flipNum = 3

                if (stance) {
                    latestFlipName.text = String.format("kickflip")
                } else {
                    latestFlipName.text = String.format("heelflip")
                }
            }

            /**
             * TRICK
             * flip to the left side
             * if stance = true = heelflip
             * if stance = false =  kickflip
             */
            if (zGravity > 9 && !flipBool) {
                flipNum = 0
            } else if (xGravity > 7 && flipNum == 0) {
                flipNum = 1
            } else if (zGravity < -7 && flipNum == 1 && (zAccelero < 1 && zGyro < 2)) {
                flipNum = 2
            } else if (xGravity < -7 && flipNum == 2) {
                flipNum = 3

                if (stance) {
                    latestFlipName.text = String.format("heelflip")
                } else {
                    latestFlipName.text = String.format("kickflip")
                }
            }
        }

        if (flipNum == 3 && !flipBool) {
            flipBool = true
            flipCount++
            flipCountText.text = String.format("flips: $flipCount")
        }

        /**
         * Log gravity values
         */
        if (event?.sensor == gravitySensor && event != null) {
            xGravity = event.values[0]
            yGravity = event.values[1]
            zGravity = event.values[2]
        }

        /**
         * Pitch detection
         */
        if (event?.sensor == pitchSensor && event != null) {
            pitchValue = event.values[1]
            rollValue = event.values[2]

        }

        /**
         * Gyroscope detections
         */
        if (event?.sensor == pitchSensor && event != null) {
            xGyro = event.values[0]
            yGyro = event.values[1]
            zGyro = event.values[2]
        }

        if (event?.sensor == rotationSensor && event != null) {

            /**
             * Initializing rotation sensor data X Y Z
             */
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            tv_yValue.text = String.format("y %.3f", y)

            zSensorValue = event.values[2] // record z value to "global" variable
            xSensorValue = event.values[0] // record x value to "global" variable

            /**
             * Flip functionality


            if (xStartValueFromButton == null) {
                tv_xValue.text = String.format("x %.3f", x)
            } else {
                tv_xValue.text = String.format("x %.3f", x)

                xRunningFloat = x - xStartValueFromButton!!
                xValueAfterReset.text = String.format("Value X %.3f", xRunningFloat)

                if (xRunningFloat!! > 0.8 || xRunningFloat!! < -0.8) {

                    kickFlipNumber++
                    kickFlipCounter.text = String.format("Flips: $kickFlipNumber")
                    xStartValueFromButton = xSensorValue
                }
            } */

            /**
             * Shuvit functionality
             */

            if (zStartValueFromButton == null) {
                tv_zValue.text = String.format("z %.3f", z)
            } else {
                tv_zValue.text = String.format("z %.3f", z)

                zRunningFloat = z - zStartValueFromButton!!
                zValueAfterReset.text = String.format("z Running Float %.3f", zRunningFloat)

                if (zRunningFloat!! > 1 || zRunningFloat!! < -1) {
                    shuvitNumber++

                    // Calculating rotation from number of shuvits THIS USED FOR POINTS CALCULATION
                    val rotationCalculation = shuvitNumber * 180

                    halfFlipCounter.text = String.format("Rotation: $rotationCalculation°")
                    zStartValueFromButton = zSensorValue

                }
            }
        }

        /**
         * Ollie functionality
         */
        if (event?.sensor == acceleroSensor && event != null) {
            xAccelero = event.values[0]
            yAccelero = event.values[1]
            zAccelero = event.values[2]

            if (zAccelero > -1 && zAccelero < 1 && !throwBool && !flipBool && (yGyro < 3 && yGyro > -3)) {
                throwBool = true
                ollieNumber++
                ollieCounter.text = String.format("Ollies: $ollieNumber")
            } else if (zAccelero > 8.5) {
                throwBool = false
            }
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

        gyroSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        gravitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    
}


