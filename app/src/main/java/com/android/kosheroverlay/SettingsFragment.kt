package com.android.kosheroverlay

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.core.content.ContextCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val overlayPref = findPreference<SwitchPreferenceCompat>("overlay_enabled")
        overlayPref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            val context = requireContext()
            val intent = Intent(context, OverlayService::class.java)
            if (enabled) {
                ContextCompat.startForegroundService(context, intent)
            } else {
                context.stopService(intent)
            }
            true
        }
    }
}
