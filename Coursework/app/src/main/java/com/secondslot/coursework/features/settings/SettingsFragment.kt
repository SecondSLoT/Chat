package com.secondslot.coursework.features.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.secondslot.coursework.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }
}
