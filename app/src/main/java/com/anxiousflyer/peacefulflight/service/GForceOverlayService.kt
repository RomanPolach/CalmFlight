package com.anxiousflyer.peacefulflight.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.anxiousflyer.peacefulflight.MainActivity
import com.anxiousflyer.peacefulflight.R
import com.anxiousflyer.peacefulflight.ui.theme.TealSoft
import kotlin.Float
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.apply
import kotlin.collections.contains
import kotlin.collections.minus
import kotlin.collections.mutableListOf
import kotlin.collections.plus
import kotlin.compareTo
import kotlin.div
import kotlin.isInitialized
import kotlin.let
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.plus
import kotlin.sequences.contains
import kotlin.sequences.minus
import kotlin.sequences.plus
import kotlin.text.compareTo
import kotlin.text.format
import kotlin.text.plus
import kotlin.text.toInt
import kotlin.times

class GForceOverlayService : Service(), SensorEventListener {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val gForceState = mutableFloatStateOf(1.0f)
    private val statusState = mutableStateOf(GForceStatus.SMOOTH)

    private var smoothedValue = 9.81f
    private val alpha = 0.05f

    // For status stability
    private val recentStatuses = mutableListOf<GForceStatus>()
    private val maxRecentStatuses = 150

    companion object {
        const val CHANNEL_ID = "gforce_overlay_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP = "com.anxiousflyer.peacefulflight.STOP_OVERLAY"

        fun startService(context: Context) {
            val intent = Intent(context, GForceOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, GForceOverlayService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        setupOverlay()
        setupSensor()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        if (::composeView.isInitialized) {
            windowManager.removeView(composeView)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.gforce_overlay_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.gforce_overlay_channel_desc)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, GForceOverlayService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.gforce_overlay_notification_title))
            .setContentText(getString(R.string.gforce_overlay_notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(openAppPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                getString(R.string.gforce_overlay_stop),
                stopPendingIntent
            )
            .setOngoing(true)
            .build()
    }

    private fun setupOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 200
        }

        // Create lifecycle owner for ComposeView
        val lifecycleOwner = OverlayLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                GForceOverlayContent(
                    gForce = gForceState.floatValue,
                    status = statusState.value,
                    onClose = { stopSelf() }
                )
            }
        }

        // Make overlay draggable
        setupDragListener(params)

        windowManager.addView(composeView, params)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    private fun setupDragListener(params: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        composeView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(composeView, params)
                    true
                }

                else -> false
            }
        }
    }

    private fun setupSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val currentRaw = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            smoothedValue = (currentRaw * alpha) + (smoothedValue * (1f - alpha))
            val gForce = smoothedValue / 9.81f

            // Update state
            gForceState.floatValue = gForce

            // Calculate status
            val deviation = abs(gForce - 1.0f)
            val newStatus = when {
                deviation <= 0.03f -> GForceStatus.SMOOTH
                deviation <= 0.07f -> GForceStatus.LIGHT_BUMPS
                deviation <= 0.13f -> GForceStatus.MODERATE
                else -> GForceStatus.BUMPY
            }

            recentStatuses.add(newStatus)
            if (recentStatuses.size > maxRecentStatuses) {
                recentStatuses.removeAt(0)
            }

            val stableStatus = when {
                recentStatuses.contains(GForceStatus.BUMPY) -> GForceStatus.BUMPY
                recentStatuses.contains(GForceStatus.MODERATE) -> GForceStatus.MODERATE
                recentStatuses.contains(GForceStatus.LIGHT_BUMPS) -> GForceStatus.LIGHT_BUMPS
                else -> GForceStatus.SMOOTH
            }

            statusState.value = stableStatus
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    enum class GForceStatus {
        SMOOTH, LIGHT_BUMPS, MODERATE, BUMPY
    }
}

@Composable
private fun GForceOverlayContent(
    gForce: Float,
    status: GForceOverlayService.GForceStatus,
    onClose: () -> Unit
) {
    val statusColor = when (status) {
        GForceOverlayService.GForceStatus.SMOOTH,
        GForceOverlayService.GForceStatus.LIGHT_BUMPS -> TealSoft

        GForceOverlayService.GForceStatus.MODERATE,
        GForceOverlayService.GForceStatus.BUMPY -> Color(0xFFFF9800)
    }

    val statusText = when (status) {
        GForceOverlayService.GForceStatus.SMOOTH -> "SMOOTH"
        GForceOverlayService.GForceStatus.LIGHT_BUMPS -> "LIGHT BUMPS"
        GForceOverlayService.GForceStatus.MODERATE -> "MODERATE"
        GForceOverlayService.GForceStatus.BUMPY -> "BUMPY"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xE6121212))
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "G-FORCE",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = String.format("%.2f G", gForce),
                color = TealSoft,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = statusText,
                color = statusColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: android.os.Bundle?) {
        savedStateRegistryController.performRestore(savedState)
    }
}
