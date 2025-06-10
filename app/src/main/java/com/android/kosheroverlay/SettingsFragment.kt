package com.android.kosheroverlay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import android.os.Handler
import android.os.Looper
import android.widget.Toast

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                    val permIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.packageName)
                    )
                    startActivity(permIntent)
                    return@setOnPreferenceChangeListener false
                }
                ContextCompat.startForegroundService(context, intent)
            } else {
                // Show toast to inform user about reboot
                Toast.makeText(context, "Your device will reboot in 5 seconds", Toast.LENGTH_LONG).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot"))
                        process.waitFor()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 5000)
            }
            true
        }
    }
}
