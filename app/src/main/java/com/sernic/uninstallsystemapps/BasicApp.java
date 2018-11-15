package com.sernic.uninstallsystemapps;

import android.app.Application;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;

public class BasicApp extends Application {

    private AppExecutors appExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        appExecutors = new AppExecutors();

        // Builds preferences
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(BuildConfig.APPLICATION_ID)
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public AppExecutors getAppExecutors() {
        return appExecutors;
    }
}
