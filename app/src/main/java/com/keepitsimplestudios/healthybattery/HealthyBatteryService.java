package com.keepitsimplestudios.healthybattery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nunop on 16/01/2017.
 */

public class HealthyBatteryService extends IntentService {

    private static final long TIMEOUT_MILISECONDS = 1800000; //30 minutes
    private static final String TAG = "HealthyBatteryService";
    public static final String ACTION_TIMER = HealthyBatteryService.class.getName()
            + ".action.TIMER";
    public static final String ACTION_START = HealthyBatteryService.class.getName()
            + ".action.START";
    public static final String ACTION_STOP = HealthyBatteryService.class.getName()
            + ".action.STOP";
    public static final String ACTION_SET_PERIOD = HealthyBatteryService.class.getName()
            + ".action.SET_PERIOD";
    public static final String EXTRA_PERIOD = HealthyBatteryService.class.getName()
            + ".extra.PERIOD";
    private static final int NOTIFICATION_ID = 1;

    // 30 sec. For first time lag for starting service.
    private static final long FIRST_TIME_LAG = 30 * 1000;

    public HealthyBatteryService() {
        super(TAG);
    }

    @Override
    protected final void onHandleIntent(Intent intent) {
        if (BuildConfig.DEBUG) {
            //Log.d(TAG, "onHandleIntent in action = " + intent.getAction());
        }

        if (ACTION_START.equals(intent.getAction())) {
            if (isTimeToRecord()) {
                //recordBatteryStateExternal(Util.FILE_PATH);
                recordBatteryStateInternal(Util.FILE_PATH_INTERNAL);
            }
            final long monitorPeriod = ((HealthyBatteryApplication) getApplication())
                    .getMonitorPeriod();
            if (BuildConfig.DEBUG) {
                //Log.d(TAG, "Monitoring period retrieved. " + monitorPeriod);
            }
            startTimer(monitorPeriod);
            //startNotification();
            ((HealthyBatteryApplication) getApplication())
                    .setMonitorStatus(true);

        } else if (ACTION_STOP.equals(intent.getAction())) {
            stopTimer();
            //stopNotification();
            ((HealthyBatteryApplication) getApplication())
                    .setMonitorStatus(false);
        } else if (ACTION_TIMER.equals(intent.getAction())) {
            if (isTimeToRecord()) {
                //recordBatteryStateExternal(Util.FILE_PATH);
                recordBatteryStateInternal(Util.FILE_PATH_INTERNAL);
            }
        } else if (ACTION_SET_PERIOD.equals(intent.getAction())) {
            setMonitoringPeriod(intent);
        }

        stopSelf();
    }

    private boolean isTimeToRecord() {
        //record only when the last record happened more than 30 minutes ago
        String pathInternal = getApplication().getApplicationContext().getFilesDir().getAbsolutePath() + "/" + Util.FILE_PATH_INTERNAL;
        String pathExternal = Util.FILE_PATH;

        if (getDataFromCSV(pathInternal) == null) { //External storage
            try {
                if (System.currentTimeMillis() - getDataFromCSV(pathExternal).getTime() >= TIMEOUT_MILISECONDS) {
                    return true;
                } else {
                    return false;
                }
            } catch (NullPointerException ex) {
                return true;
            }
        } else { //Internal storage
            try {
                if (System.currentTimeMillis() - getDataFromCSV(pathInternal).getTime() >= TIMEOUT_MILISECONDS) {
                    return true;
                } else {
                    return false;
                }
            } catch (NullPointerException ex) {
                return true;
            }
        }
    }

    public Date getDataFromCSV(String path) {

        String token = ",";
        String[] separated;
        Date recordDate = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;

            while ((line = br.readLine()) != null) {
                separated = line.split(token);
                String date = separated[0];
                String hours = separated[1];
                recordDate = new SimpleDateFormat("yyyy/MM/dd/hh:mm:ss").parse(date + "/" + hours);
            }
            br.close();
        } catch (Exception e) {
            return null;
        }

        return recordDate;
    }

    private void setMonitoringPeriod(Intent intent) {
        final long period = intent.getLongExtra(EXTRA_PERIOD,
                Util.TIMEOUT_SERVICE);
        if (BuildConfig.DEBUG) {
            //Log.d(TAG, "setMonitoringPeriod period = " + period);
        }
        ((HealthyBatteryApplication) getApplication())
                .setMonitoringPeriod(period);
    }

    @Deprecated
    private void recordBatteryStateExternal(String path) {
        //NÃO VAI SER USADO

        final int batteryLevel = Util
                .getCurrentBatteryLevel(getApplicationContext());
        if (BuildConfig.DEBUG) {
            //Log.d(TAG, "recordBatteryState batteryLevel = " + batteryLevel);
        }
        File file = new File(path);
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            String content;

            // yyyy/MM/dd,HH:mm:ss,bat
            StringBuilder builder = new StringBuilder()
                    .append(new SimpleDateFormat("yyyy/MM/dd','HH:mm:ss", Locale.US)
                            .format(new Date(System.currentTimeMillis())))
                    .append(",")
                    .append(batteryLevel).append("\n");

            content = builder.toString();

            bw.write(content);
            bw.close();

        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                //Log.w(TAG, "IOException", e);
            }
        }
    }

    private void recordBatteryStateInternal(String path) {

        final int batteryLevel = Util
                .getCurrentBatteryLevel(getApplicationContext());
        if (BuildConfig.DEBUG) {
            //Log.d(TAG, "recordBatteryState batteryLevel = " + batteryLevel);
        }
        File file = new File(path);
        file.setWritable(true, false);
        FileOutputStream fOut = null;

        try {
            fOut = openFileOutput(path, MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            OutputStream stream = new BufferedOutputStream(fOut);
            OutputStreamWriter streamWriter = new OutputStreamWriter(stream, "UTF-8");


            // yyyy/MM/dd,HH:mm:ss,bat
            StringBuilder builder = new StringBuilder()
                    .append(new SimpleDateFormat("yyyy/MM/dd','HH:mm:ss", Locale.US)
                            .format(new Date(System.currentTimeMillis())))
                    .append(",").append(batteryLevel).append("\n");

            streamWriter.append(builder.toString());
            builder = null;
            streamWriter.close();
            stream.close();
            fOut.close();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                //Log.w(TAG, "IOException", e);
            }
        }
    }

    private void startTimer(final long period) {

        //Log.d(TAG, "startTimer called.");
        PendingIntent operation = getPendingIntentForAlarmManager();

        // set the pending intent to AlarmManager with repeating mode.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //Marshmallow up (because of doze)
        {
            //AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            //manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME,
            //        FIRST_TIME_LAG, operation);
            AlarmManager manager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager2.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    FIRST_TIME_LAG, period, operation);
        } else {
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    FIRST_TIME_LAG, period, operation);
        }
    }

    /*private void startNotification() {
        Log.d(TAG, "startNotification called");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(
                R.drawable.ic_stat_monitoring, getString(R.string.app_name),
                System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), NOTIFICATION_ID, new Intent(
                        getApplicationContext(), BatteryMonitorActivity.class),
                0);
        notification.setLatestEventInfo(getApplicationContext(),
                getString(R.string.app_name),
                getString(R.string.notification_description), pendingIntent);
        manager.notify(NOTIFICATION_ID, notification);
    }
*/
    private void stopTimer() {
        //Log.d(TAG, "stopTimer called.");
        PendingIntent operation = getPendingIntentForAlarmManager();

        AlarmManager manager = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);
        manager.cancel(operation);
    }

    private void stopNotification() {
        //Log.d(TAG, "stopNotification called");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }

    private PendingIntent getPendingIntentForAlarmManager() {
        Intent startService = new Intent(getApplicationContext(),
                HealthyBatteryService.class);
        startService.setAction(ACTION_TIMER);
        return PendingIntent.getService(this, 0, startService,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
