package com.reductivetech.taskclarity.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.reductivetech.taskclarity.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
