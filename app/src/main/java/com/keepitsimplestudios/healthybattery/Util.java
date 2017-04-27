package com.keepitsimplestudios.healthybattery;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public final class Util {
    public static final String FILE_NAME = "HealthBattery.txt";
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/" + FILE_NAME;
    public static final String FILE_PATH_INTERNAL = "HealthBattery.txt";
    public static final long TIMEOUT_SERVICE = AlarmManager.INTERVAL_HALF_HOUR;
    public static final int MAXBATTERY = 80;
    //public static final int MINBATTERY=50;
    public static final int MAXTEMPERATURE = 40;
    public static final int MAX_GRAPH_POINTS = 10;
    private static final String TAG = "Util";

    public static final int getCurrentBatteryLevel(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        return intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
    }

    public static final String convertRawTestToString(final Context context, final int id) {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream stream = context.getResources().openRawResource(id);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String buffer = null;
            while ((buffer = reader.readLine()) != null) {
                builder.append(buffer).append("\n");
            }
            reader.close();
            stream.close();
        } catch (UnsupportedEncodingException e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "UnsupportedEncodingException", e);
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "IOException", e);
            }
        }
        return builder.toString();
    }
}
