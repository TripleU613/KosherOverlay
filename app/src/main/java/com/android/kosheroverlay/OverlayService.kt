
package com.android.kosheroverlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
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
    private val binder = OverlayBinder()

    inner class OverlayBinder : Binder() {
        fun getService(): OverlayService = this@OverlayService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()

        // Create notification channel
        val channelId = "OverlayServiceChannel"
        val channel = NotificationChannel(
            channelId,
            "Overlay Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        // Build notification
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Kosher Overlay Active")
            .setContentText("Logo overlay is running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(1, notification)

        // Set up overlay
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val imageSizeDp = 64
        val imageSizePixels = convertDpToPixel(imageSizeDp)

        overlayImageView = ImageView(this).apply {
            setImageResource(R.drawable.kosher_logo)
            alpha = 0.2f
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

        overlayViewContainer = FrameLayout(this)
        val imageParams = FrameLayout.LayoutParams(imageSizePixels, imageSizePixels)
        overlayViewContainer.addView(overlayImageView, imageParams)

        val params = WindowManager.LayoutParams(
            imageSizePixels,
            imageSizePixels,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            val marginDp = 20
            val marginPixels = convertDpToPixel(marginDp)
            x = marginPixels
            y = marginPixels
        }

        windowManager.addView(overlayViewContainer, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::overlayViewContainer.isInitialized && overlayViewContainer.isAttachedToWindow) {
            windowManager.removeView(overlayViewContainer)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun convertDpToPixel(dp: Int): Int {
        return (dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }
}