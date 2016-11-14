package com.android.sunshine;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_pattern, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_general);
            bindPrefSummaryToValue(findPreference(getString(R.string.pref_location_key)));
            bindPrefSummaryToValue(findPreference(getString(R.string.pref_temperature_units_key)));
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            view.findViewById(android.R.id.list).setPadding(
                    (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int) getResources().getDimension(R.dimen.activity_vertical_margin),
                    (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int) getResources().getDimension(R.dimen.activity_vertical_margin));
        }

        private void bindPrefSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(o.toString());
                if (index >= 0) {
                    preference.setSummary(listPreference.getEntries()[index]);
                }
            } else if (preference instanceof EditTextPreference) {
                preference.setSummary(o.toString());
            }
            return true;
        }
    }
}
