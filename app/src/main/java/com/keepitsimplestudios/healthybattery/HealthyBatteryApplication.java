package com.keepitsimplestudios.healthybattery;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public final class HealthyBatteryApplication extends Application {

    private static final String TAG = "HealthyBatteryApplication";
    private static final String PREFERENCE_NAME = "setting";
    private static final String PREFERENCE_KEY_MONITORING = "monitoring";
    private static final String PREFERENCE_KEY_PERIOD = "period";
    private static Boolean sMonitoring = null;
    private static Long sMonitoringPeriod = null;
    private OnSharedPreferenceChangeListener mLister;
    

    public final boolean getMonitorStatus() {

        // lock for getter - setter
        synchronized (this) {
            // singleton for performance.
            if (sMonitoring == null) {
                SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME,
                        MODE_PRIVATE);
                sMonitoring = pref.getBoolean(PREFERENCE_KEY_MONITORING, false);
            }
        }
        return sMonitoring;
    }

    public final void setMonitorStatus(final boolean monitoring) {

        // lock for getter - setter
        synchronized (this) {
            // An attribute to store status of recording.
            // Service and process can be killed by system during recording.
            // Need to store status attribute so that to handle state properly.
            SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME,
                    MODE_PRIVATE);
            Editor edit = pref.edit();
            edit.putBoolean(PREFERENCE_KEY_MONITORING, monitoring);
            edit.commit();

            sMonitoring = monitoring;
        }
    }

    public final long getMonitorPeriod() {
        synchronized (this) {
            // singleton for performance.
            if (sMonitoringPeriod == null) {
                SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME,
                        MODE_PRIVATE);
                sMonitoringPeriod = pref.getLong(PREFERENCE_KEY_PERIOD,
                        Util.TIMEOUT_SERVICE);
            }
        }
        return sMonitoringPeriod;
    }

    public final void setMonitoringPeriod(final long period) {

        // lock for getter - setter
        synchronized (this) {
            // An attribute to store period of recording.
            // Service and process can be killed by system during recording.
            // Need to store status attribute so that to handle state properly.
            SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME,
                    MODE_PRIVATE);
            Editor edit = pref.edit();
            edit.putLong(PREFERENCE_KEY_PERIOD, period);
            edit.commit();

            sMonitoringPeriod = period;
        }
    }

    
    /* package */final void setOnStateChangeListener(
            final OnStateChangeListener listener) {

        // unset if already registered.
        unsetOnStateChangeLister();

        // bypass preference state change lister to original listener for
        // encapsulating
        SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME,
                MODE_PRIVATE);

        //TODO should be synchronized?
        mLister = new OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences pref,
                    String key) {
                //Log.d(TAG, "onSharedPreferenceChanged called");
                if (PREFERENCE_KEY_MONITORING.equals(key)
                        || PREFERENCE_KEY_PERIOD.equals(key)) {
                    listener.onStateChanged(pref.getBoolean(
                            PREFERENCE_KEY_MONITORING, false), pref.getLong(
                            PREFERENCE_KEY_PERIOD, Util.TIMEOUT_SERVICE));

                }
            }
        };
        pref.registerOnSharedPreferenceChangeListener(mLister);
    }

    /* package */final void unsetOnStateChangeLister() {

        //TODO should be synchronized?
        if (mLister != null) {
            SharedPreferences pref = getSharedPreferences(PREFERENCE_NAME,
                    MODE_PRIVATE);
            pref.unregisterOnSharedPreferenceChangeListener(mLister);
            mLister = null;
        }

    }


}
