package edu.oregonstate.cs492.assignmentfinal.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import edu.oregonstate.cs492.assignmentfinal.R

class settingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        val darkModePref = findPreference<CheckBoxPreference>("dark_mode_enabled")
        darkModePref?.setOnPreferenceChangeListener { _, newValue ->
            val isEnabled = newValue as Boolean

            if (isEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            true // save the value
        }
    }
}