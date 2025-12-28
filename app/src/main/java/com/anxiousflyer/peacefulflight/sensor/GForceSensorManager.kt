package com.anxiousflyer.peacefulflight.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class GForceSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var smoothedValue = 9.81f

    // Alpha 0.05: "Goldilocks" smoothing.
    // 0.15 was too sensitive (picked up hand shakes).
    // 0.03 was too slow (missed real bumps).
    // 0.05 filters out high-freq jitter but catches the "heave" of turbulence.
    private val alpha = 0.05f

    private var listener: ((Float) -> Unit)? = null

    fun start(onGForceChanged: (Float) -> Unit) {
        listener = onGForceChanged
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        listener = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Calculate magnitude
            val currentRaw = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // Apply Low-Pass Filter
            smoothedValue = (currentRaw * alpha) + (smoothedValue * (1f - alpha))

            // Convert to G-Force
            val gForce = smoothedValue / 9.81f
            listener?.invoke(gForce)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
