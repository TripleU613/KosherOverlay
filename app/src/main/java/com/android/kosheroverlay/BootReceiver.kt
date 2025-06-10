@file:Suppress("DEPRECATION")

package com.android.kosheroverlay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            if (prefs.getBoolean("overlay_enabled", false)) {
                val serviceIntent = Intent(context, OverlayService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}
