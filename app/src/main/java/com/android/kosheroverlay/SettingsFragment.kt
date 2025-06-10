package com.android.kosheroverlay

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
                // Stop the overlay service immediately when toggled OFF
                context.stopService(intent)
            }
            true
        }
    }
}
