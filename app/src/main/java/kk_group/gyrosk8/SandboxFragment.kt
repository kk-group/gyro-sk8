package kk_group.gyrosk8

import android.app.AlertDialog
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
import android.widget.EditText


class SandboxFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var prefManager: PrefManager

    // sensors
    private var rotationSensor: Sensor? = null
    private var pitchSensor: Sensor? = null
    private var acceleroSensor: Sensor? = null
    private var gyroSensor: Sensor? = null
    private var gravitySensor: Sensor? = null

    // rotation sensor values
    private var zRotation: Float? = null
    private var zRotationStartValue: Float? = null
    private var zRotationRunning: Float? = null

    private var xRotation: Float? = null
    private var xRotationStartValue: Float? = null
    private var xRotationRunning: Float? = null

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

        prefManager = PrefManager(activity!!.applicationContext)


        /**
         * Reset button
         */
        val btn = view.findViewById(R.id.resetButton) as Button

        btn.setOnClickListener {
            zRotationStartValue = zRotation  // hommataan erotus
            xRotationStartValue = xRotation

            rotationCount = 0
            ollieCount = 0
            flipCount = 0

            timer(20000,1000).start()
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
     * Function that shows alert dialog when user makes new highscore
     */
    private fun showTopScoreDialog() {
        val alertDialog = AlertDialog.Builder(activity!!).create()

        alertDialog.setTitle(getString(R.string.alertDialogTitle))
        alertDialog.setMessage(getString(R.string.alertDialogMsg) + " $totalScore " + getString(R.string.pointsTxt))

        // create textfield
        val usrTextField = EditText(activity!!.applicationContext)

        alertDialog.setView(usrTextField)

        // this is so the user HAS to set a name and get the glory
        alertDialog.setCancelable(false)


        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alertOkBtn)
        ) { dialog, which ->
            prefManager.setTopUser(usrTextField.text.toString())
            prefManager.setTopScore(totalScore)
            dialog.dismiss() }
        alertDialog.show()
    }

    /**
     * Method for gameplay timer
     */
    private fun timer(millisInFuture:Long,countDownInterval:Long): CountDownTimer {

        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                val timeRemaining = millisUntilFinished / 1000
                gameplayTimer.text = "timer: $timeRemaining"
                //Log.d("DEBUG", "$timeRemaining")
            }

            override fun onFinish() {
                Log.d("DEBUG", "FINISHED COUNTDOWN")
                var ollieScore = olliePoint * ollieCount
                var flipScore = flipPoint * flipCount
                var rotationScore = rotationPoint * rotationCount

                totalScore = ollieScore + flipScore + rotationScore

                scoreText.text = String.format("Total score: $totalScore")

                // here we check if totalscore is the new top score and put it in if it is
                if (prefManager.checkTopScore(totalScore)) {
                    //prefManager.setTopUser("TESTMAN")
                    //prefManager.setTopScore(totalScore)

                    showTopScoreDialog()
                }
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
                    latestTrickName.text = String.format("Kickflip")
                } else {
                    latestTrickName.text = String.format("Heelflip")
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
                    latestTrickName.text = String.format("Heelflip")
                } else {
                    latestTrickName.text = String.format("Kickflip")
                }
            }
        }

        if (flipNum == 3 && !flipBool) {
            flipBool = true
            flipCount++
        }

        if (event?.sensor == rotationSensor && event != null) {

            /**
             * Initializing rotation sensor data for X & Z axixes
             */

            zRotation = event.values[2] // record z value to "global" variable
            xRotation = event.values[0] // record x value to "global" variable

            if (zRotationStartValue !=  null) {
                zRotationRunning = zRotation!! - zRotationStartValue!!

                /**
                 * TRICK
                 * rotation to the left side
                 * if stance = true = Pop Shove-it
                 * if stance = false =  Shove-it
                 */
                if ((zRotationRunning!! > 1 || zRotationRunning!! < -1) && zGyro < -3) {
                    rotationCount++

                    zRotationStartValue = zRotation

                    if (stance) {
                        latestTrickName.text = String.format("Pop Shove-it")
                    } else {
                        latestTrickName.text = String.format("Shove-it")
                    }
                }
                /**
                 * TRICK
                 * rotation to the right side
                 * if stance = true = Shove-it
                 * if stance = false =  Pop Shove-it
                 */
                else if ((zRotationRunning!! > 1 || zRotationRunning!! < -1) && zGyro > 3) { // right side rotation
                    rotationCount++

                    zRotationStartValue = zRotation

                    if (stance) {
                        latestTrickName.text = String.format("Shove-it")
                    } else {
                        latestTrickName.text = String.format("Pop Shove-it")
                    }
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
                latestTrickName.text = String.format("Ollie")
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


