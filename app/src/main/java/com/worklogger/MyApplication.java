package com.worklogger;

import android.app.Application;

import cat.ereza.customactivityoncrash.config.CaocConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .minTimeBetweenCrashesMs(2000) //default: 3000
                .apply();
    }
}
