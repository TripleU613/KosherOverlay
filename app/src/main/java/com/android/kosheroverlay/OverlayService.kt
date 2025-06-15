package com.android.kosheroverlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayViewContainer: FrameLayout
    private lateinit var overlayImageView: ImageView

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // Step 1: Start as a foreground service
        val channelId = "OverlayServiceChannel"
        val channel = NotificationChannel(
            channelId,
            "Overlay Service",
            NotificationManager.IMPORTANCE_LOW // Low importance to minimize user disturbance
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Kosher Overlay Active")
            .setContentText("Logo overlay is running")
            .setSmallIcon(R.mipmap.ic_launcher) // Use your app icon
            .build()

        startForeground(1, notification)

        // Step 2: Existing overlay setup
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val imageSizeDp = 64
        val imageSizeBytes = convertDpToPixel(imageSizeDp)

        overlayImageView = ImageView(this)
        overlayImageView.setImageResource(R.drawable.kosher_logo)
        overlayImageView.alpha = 0.2f
        overlayImageView.scaleType = ImageView.ScaleType.FIT_CENTER

        overlayViewContainer = FrameLayout(this)
        val imageParams = FrameLayout.LayoutParams(imageSizeBytes, imageSizeBytes)
        overlayViewContainer.addView(overlayImageView, imageParams)

        val params = WindowManager.LayoutParams(
            imageSizeBytes,
            imageSizeBytes,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.BOTTOM or Gravity.END
        val marginDp = 20
        val marginPixels = convertDpToPixel(marginDp)
        params.x = marginPixels
        params.y = marginPixels

        windowManager.addView(overlayViewContainer, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Step 3: Return START_STICKY to restart if killed
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayViewContainer.isInitialized && overlayViewContainer.isAttachedToWindow) {
            windowManager.removeView(overlayViewContainer)
        }
        // Step 4: Restart the service if destroyed
        val restartIntent = Intent(this, OverlayService::class.java)
        startService(restartIntent)
    }

    private fun convertDpToPixel(dp: Int): Int {
        return (dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }
}