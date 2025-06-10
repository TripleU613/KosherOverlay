package com.android.kosheroverlay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val overlayPref = findPreference<SwitchPreferenceCompat>("overlay_enabled")
        overlayPref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            val context = requireContext()
            val intent = Intent(context, OverlayService::class.java)
            if (enabled) {
                // Only start service if overlay permission is granted
                if (true && !Settings.canDrawOverlays(context)) {
                    // Request overlay permission if missing
                    val permIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        ("package:" + context.packageName).toUri()
                    )
                    startActivity(permIntent)
                    // Don't change the toggle yet
                    return@setOnPreferenceChangeListener false
                }
                ContextCompat.startForegroundService(context, intent)
            } else {
                // Show toast to inform user about reboot
                Toast.makeText(context, "Your device will reboot in 5 seconds", Toast.LENGTH_LONG).show()

                // Schedule reboot after 5 seconds
                Handler(Looper.getMainLooper()).postDelayed({
                    val rebootIntent = Intent(Intent.ACTION_REBOOT)
                    rebootIntent.putExtra("nowait", 1)
                    rebootIntent.putExtra("interval", 1)
                    rebootIntent.putExtra("window", 0)
                    context.sendBroadcast(rebootIntent)
                }, 5000)
            }
            true
        }
    }
}
