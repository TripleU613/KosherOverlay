package com.android.kosheroverlay

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Handle Overlay Switch
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
                Toast.makeText(context, "Reboot to apply changes", Toast.LENGTH_LONG).show()

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

        // Handle BCR Launch Button
        val launchBcrPref = findPreference<Preference>("launch_bcr")
        launchBcrPref?.setOnPreferenceClickListener {
            val context = requireContext()
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName("com.chiller3.bcr", "com.chiller3.bcr.settings.SettingsActivity")
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required for system apps or non-activity contexts
            }
            try {
                startActivity(intent)
                Toast.makeText(context, "Launching BCR", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to launch BCR: ${e.message}", Toast.LENGTH_LONG).show()
            }
            true
        }
    }
}