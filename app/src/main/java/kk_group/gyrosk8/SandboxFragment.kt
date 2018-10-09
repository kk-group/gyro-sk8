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
import android.os.CountDownTimer
import android.util.Log
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

    // variables to keep number of tricks done
    private var ollieCount: Int = 0
    private var rotationCount: Int = 0
    private var flipCount = 0

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

    // game points system variables
    private val olliePoint: Int = 5
    private val flipPoint: Int = 10
    private val rotationPoint: Int = 7
    private var totalScore: Int = 0

    /**
     * stance is the hand the player is using
     * FALSE = regular / left hand
     * TRUE = goofy / right hand
     */
    private var stance: Boolean = true

    private var flipNum = 0 // variable that gets checked on each flip

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.sandbox_fragment, container, false)

        sensorManager = layoutInflater.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        pitchSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        acceleroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        /**
         * set stance from the bundle
         */
        stance = arguments!!.getBoolean("stance")


        /**
         * Reset button
         */
        val btn = view.findViewById(R.id.resetButton) as Button

        btn.setOnClickListener {
            zStartValueFromButton = zSensorValue  // hommataan erotus
            xStartValueFromButton = xSensorValue

            rotationCount = 0

            ollieCount = 0
            flipCount = 0

            halfFlipCounter.text = String.format("Rotation: $rotationCount")
            ollieCounter.text = String.format("Ollies: $ollieCount")
            flipCountText.text = String.format("flips: $flipCount")

            timer(20000,1000).start()
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

    /**
     * Method for gameplay timer
     */
    private fun timer(millisInFuture:Long,countDownInterval:Long): CountDownTimer {

        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                val timeRemaining = millisUntilFinished / 1000
                gameplayTimer.text = "time left: $timeRemaining sec"
                Log.d("DEBUG", "$timeRemaining")
            }

            override fun onFinish() {
                Log.d("DEBUG", "FINISHED COUNTDOWN")
                var ollieScore = olliePoint * ollieCount
                var flipScore = flipPoint * flipCount
                var rotationScore = rotationPoint * rotationCount

                totalScore = ollieScore + flipScore + rotationScore


                scoreText.text = String.format("Total score: $totalScore")

            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        /**
         *  check if device is flat and change background color according to it
         *  also resets flipNumber and sets boolean to false to tell the program that phone is on somewhat flat surface and not in mid-flip
         */
        if (checkFlat()) {
            relativeLayout.setBackgroundColor(Color.parseColor("#33FF41")) // green
            flipNum = 0
            flipBool = false
        } else {
            relativeLayout.setBackgroundColor(Color.parseColor("#FF5733")) // red
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

        if (event?.sensor == rotationSensor && event != null) {

            /**
             * Initializing rotation sensor data X Y Z
             */
            val y = event.values[1]
            val z = event.values[2]

            zSensorValue = event.values[2] // record z value to "global" variable
            xSensorValue = event.values[0] // record x value to "global" variable

            if (zRunningFloat !=  null) {
                zRunningFloat = z - zStartValueFromButton!!

                /**
                 * TRICK
                 * Shuvit functionality
                 */

                if (zRunningFloat!! > 1 || zRunningFloat!! < -1) {
                    rotationCount++

                    // Calculating rotation from number of shuvits
                    val rotationCalculation = rotationCount * 180

                    halfFlipCounter.text = String.format("Rotation: $rotationCalculation°")
                    zStartValueFromButton = zSensorValue
                }
            }
        }

        if (event?.sensor == acceleroSensor && event != null) {
            xAccelero = event.values[0]
            yAccelero = event.values[1]
            zAccelero = event.values[2]

            /**
             * TRICK
             * Ollie functionality
             */

            if (zAccelero > -1 && zAccelero < 1 && !throwBool && !flipBool && (yGyro < 3 && yGyro > -3)) {
                throwBool = true
                ollieCount++
                ollieCounter.text = String.format("Ollies: $ollieCount")
            } else if (zAccelero > 8.5) {
                throwBool = false
            }
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
         * Log pitch values
         */
        if (event?.sensor == pitchSensor && event != null) {
            pitchValue = event.values[1]
            rollValue = event.values[2]

        }

        /**
         * Log gyroscope values
         */
        if (event?.sensor == pitchSensor && event != null) {
            xGyro = event.values[0]
            yGyro = event.values[1]
            zGyro = event.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()

        /**
         * Register listeners for all sensors
         */
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

        /**
         * While paused and out of app unregister all sensor listeners to save battery
         */
        sensorManager.unregisterListener(this)
    }
}


