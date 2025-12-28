package com.anxiousflyer.peacefulflight.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.anxiousflyer.peacefulflight.sensor.GForceSensorManager
import com.anxiousflyer.peacefulflight.ui.theme.TealSoft
import kotlin.math.abs

class GForceOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var sensorManager: GForceSensorManager

    private val displayedGForceState = mutableFloatStateOf(1.0f)
    private val statusState = mutableStateOf(GForceStatus.SMOOTH)
    private val historyState = mutableStateListOf<Float>()

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    // Re-register sensor when screen turns on
                    if (::sensorManager.isInitialized) {
                        sensorManager.stop()
                        setupSensor()
                    }
                }
            }
        }
    }

    // For displayed value stability (updates every 500ms)
    private var lastDisplayUpdate = 0L
    private val displayUpdateInterval = 500L

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
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        setupOverlay()
        setupSensor()
        registerScreenReceiver()
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
        try {
            unregisterReceiver(screenReceiver)
        } catch (_: Exception) {}
        if (::sensorManager.isInitialized) {
            sensorManager.stop()
        }
        if (::composeView.isInitialized) {
            windowManager.removeView(composeView)
        }
    }

    private fun registerScreenReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(screenReceiver, filter)
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
                    displayedGForce = displayedGForceState.floatValue,
                    status = statusState.value,
                    history = historyState,
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
        sensorManager = GForceSensorManager(this)
        sensorManager.start { gForce ->
            // Update history for graph (300 points like original)
            historyState.add(gForce)
            if (historyState.size > 300) {
                historyState.removeAt(0)
            }

            // Update displayed value every 500ms (like original)
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastDisplayUpdate >= displayUpdateInterval) {
                lastDisplayUpdate = currentTime
                if (historyState.isNotEmpty()) {
                    val recentCount = minOf(historyState.size, 10)
                    val recentHistory = historyState.takeLast(recentCount)
                    // Find the most extreme value (furthest from 1.0G) in the recent history
                    val maxDeviation = recentHistory.maxByOrNull { abs(it - 1.0f) } ?: 1.0f
                    displayedGForceState.floatValue = maxDeviation
                }
            }

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

    enum class GForceStatus {
        SMOOTH, LIGHT_BUMPS, MODERATE, BUMPY
    }
}

@Composable
private fun GForceOverlayContent(
    displayedGForce: Float,
    status: GForceOverlayService.GForceStatus,
    history: SnapshotStateList<Float>,
    onClose: () -> Unit
) {
    val graphBackground = Color(0xFF1A3A3A)
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
            .clip(RoundedCornerShape(12.dp))
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
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
                text = String.format("%.2f G", displayedGForce),
                color = TealSoft,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            // Mini Graph - full width with distinct background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(graphBackground)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height

                    // Map Y axis: 0G -> bottom, 3G -> top (same as original)
                    val mapY = { g: Float ->
                        val clamped = g.coerceIn(0f, 3.0f)
                        height - (clamped / 3.0f * height)
                    }

                    // Draw normal zone highlight (0.8 to 1.2G)
                    drawRect(
                        color = TealSoft.copy(alpha = 0.15f),
                        topLeft = Offset(0f, mapY(1.2f)),
                        size = androidx.compose.ui.geometry.Size(
                            width,
                            mapY(0.8f) - mapY(1.2f)
                        )
                    )

                    // Draw 1.0G reference line
                    drawLine(
                        color = TealSoft.copy(alpha = 0.5f),
                        start = Offset(0f, mapY(1.0f)),
                        end = Offset(width, mapY(1.0f)),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Draw the history path
                    if (history.isNotEmpty()) {
                        val path = Path()
                        val stepX = width / 300f

                        history.forEachIndexed { index, g ->
                            val x = width - ((history.size - 1 - index) * stepX)
                            val y = mapY(g)

                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = TealSoft,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }

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
