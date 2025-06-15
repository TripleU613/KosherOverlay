
package com.android.kosheroverlay

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {
    private var overlayServiceBound = false
    private var overlayService: OverlayService? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val context = requireContext()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val overlayPref = findPreference<SwitchPreferenceCompat>("overlay_enabled")
        if (Settings.canDrawOverlays(context)) {
            startOverlayService()
            overlayPref?.isChecked = true
            prefs.edit().putBoolean("overlay_enabled", true).apply()
        } else {
            Toast.makeText(context, "Overlay permission denied", Toast.LENGTH_LONG).show()
            overlayPref?.isChecked = false
            prefs.edit().putBoolean("overlay_enabled", false).apply()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            overlayService = (service as OverlayService.OverlayBinder).getService()
            overlayServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            overlayServiceBound = false
            overlayService = null
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Bind to OverlayService
        val context = requireContext()
        val intent = Intent(context, OverlayService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Handle Overlay Switch
        val overlayPref = findPreference<SwitchPreferenceCompat>("overlay_enabled")
        overlayPref?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            val context = requireContext()
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            if (enabled) {
                if (!Settings.canDrawOverlays(context)) {
                    val permIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        ("package:" + context.packageName).toUri()
                    )
                    permissionLauncher.launch(permIntent)
                    return@setOnPreferenceChangeListener false
                }
                startOverlayService()
            } else {
                stopOverlayService()
            }
            prefs.edit().putBoolean("overlay_enabled", enabled).apply()
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