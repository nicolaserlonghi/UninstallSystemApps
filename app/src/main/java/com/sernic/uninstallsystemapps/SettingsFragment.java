package com.sernic.uninstallsystemapps;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference fooBarPref = (Preference) findPreference("leave_feedback");
        fooBarPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // ACTION_SENDTO filters for email apps (discard bluetooth and others)
                String uriText =
                        "mailto:nicolaserlonghi@gmail.com" +
                                "?subject=" + Uri.encode("Unistall System Apps - Feedback");

                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                startActivity(Intent.createChooser(sendIntent, "Send email"));

                return false;
            }
        });

        Preference appVersion = (Preference) findPreference("app_version");
        appVersion.setSummary(BuildConfig.VERSION_NAME);
    }
}
