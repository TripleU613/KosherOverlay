
package com.android.kosheroverlay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {
    private var overlayServiceBound = false
    private var overlayService: OverlayService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            overlayService = (service as OverlayService.OverlayBinder).getService()
            overlayServiceBound = true
            updateOverlaySwitchState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            overlayServiceBound = false
            overlayService = null
            updateOverlaySwitchState()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Bind to OverlayService to check its state
        val context = requireContext()
        val intent = Intent(context, OverlayService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Handle Overlay Switch
        val overlayPref = findPreference<SwitchPreferenceCompat>("overlay_enabled")
        overlayPref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            prefs.edit().putBoolean("overlay_enabled", enabled).apply()

            if (enabled) {
                startOverlayService()
            } else {
                stopOverlayService()
            }
            true
        }

        // Handle BCR Launch Button
        val launchBcrPref = findPreference<Preference>("launch_bcr")
        launchBcrPref?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName("com.chiller3.bcr", "com.chiller3.bcr.settings.SettingsActivity")
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                startActivity(intent)
                Toast.makeText(context, "Launching BCR", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to launch BCR: ${e.message}", Toast.LENGTH_LONG).show()
            }
            true
        }

        // Handle FNG Launch Button
        val launchFngPref = findPreference<Preference>("launch_fng")
        launchFngPref?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName("com.fb.fluid", "com.fb.fluid.ui.ActivitySettings")
                addCategory(Intent.CATEGORY_LAUNCHER)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                startActivity(intent)
                Toast.makeText(context, "Launching FNG", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to launch FNG: ${e.message}", Toast.LENGTH_LONG).show()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        updateOverlaySwitchState()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (overlayServiceBound) {
            requireContext().unbindService(serviceConnection)
            overlayServiceBound = false
        }
    }

    private fun startOverlayService() {
        val context = requireContext()
        val intent = Intent(context, OverlayService::class.java)
        ContextCompat.startForegroundService(context, intent)
        Toast.makeText(context, "Overlay service started", Toast.LENGTH_SHORT).show()
    }

    private fun stopOverlayService() {
        val context = requireContext()
        val intent = Intent(context, OverlayService::class.java)
        context.stopService(intent)
        Toast.makeText(context, "Overlay service stopped", Toast.LENGTH_SHORT).show()
    }

    private fun updateOverlaySwitchState() {
        val overlayPref = findPreference<SwitchPreferenceCompat>("overlay_enabled")
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        overlayPref?.isChecked = prefs.getBoolean("overlay_enabled", false)
    }
}