package com.keepitsimplestudios.healthybattery.MainActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.keepitsimplestudios.healthybattery.Definitions.DefinitionsPresenterImpl;
import com.keepitsimplestudios.healthybattery.HealthyBatteryApplication;
import com.keepitsimplestudios.healthybattery.HealthyBatteryService;
import com.keepitsimplestudios.healthybattery.R;
import com.keepitsimplestudios.healthybattery.Util;
import com.keepitsimplestudios.healthybattery.Utils.CVSFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.keepitsimplestudios.healthybattery.Util.MAXBATTERY;
import static com.keepitsimplestudios.healthybattery.Util.MAXTEMPERATURE;
import static com.keepitsimplestudios.healthybattery.Util.MAX_GRAPH_POINTS;
import static com.keepitsimplestudios.healthybattery.Util.TIMEOUT_SERVICE;

/**
 * Created by nunop on 27/01/2017.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter {

    private BatteryInfoReceiverBR mBatteryInfoReceiverBR;

    private MainActivityView mView;

    public MainActivityPresenterImpl(MainActivityView view) {
        mView = view;
    }

    @Override
    public void startServices() {
        final boolean monitoring = ((HealthyBatteryApplication) mView.getActivity().getApplicationContext()).getMonitorStatus();
        final long period = ((HealthyBatteryApplication) mView.getActivity().getApplication()).getMonitorPeriod();


        if (!isMyServiceRunning(HealthyBatteryService.class)) {
            Intent intent = new Intent(mView.getActivity(), HealthyBatteryService.class);
            intent.setAction(HealthyBatteryService.ACTION_START);
            mView.getActivity().startService(intent);


            intent = new Intent(mView.getActivity(), HealthyBatteryService.class);
            intent.setAction(HealthyBatteryService.ACTION_SET_PERIOD);
            intent.putExtra(HealthyBatteryService.EXTRA_PERIOD, TIMEOUT_SERVICE); //15 minutes
            mView.getActivity().startService(intent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mView.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerBroadcasts() {

        mBatteryInfoReceiverBR = new BatteryInfoReceiverBR();

        mView.getActivity().registerReceiver(mBatteryInfoReceiverBR, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void unregisterBroadcasts() {
        try {
            mView.getActivity().unregisterReceiver(mBatteryInfoReceiverBR);
        } catch (Exception e) {
        }
    }

    public class BatteryInfoReceiverBR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            update();
            stopSound();
            try {
                try {
                    mView.cancelVibrator();
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
            checkBatteryLevel();
            checkTemperature();
        }
    }

    private void updateStatus() {
        String msg;
        Context context = mView.getContext();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        mView.setStatus(batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1));
        switch (mView.getStatus()) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                msg = mView.getActivity().getString(R.string.charging);
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                msg = mView.getActivity().getString(R.string.discharging);
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                msg = mView.getActivity().getString(R.string.full);
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                msg = mView.getActivity().getString(R.string.not_charging);
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                msg = mView.getActivity().getString(R.string.unknown);
                break;
            default:
                msg = mView.getActivity().getString(R.string._unknown);
        }
        mView.setStatusText(msg);
    }

    private void updateChargePlug() {
        String msg;
        Context context = mView.getContext();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        mView.setChargePlug(batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1));
        switch (mView.getChargePlug()) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                msg = mView.getActivity().getString(R.string.plugged_ac);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                msg = mView.getActivity().getString(R.string.plugged_usb);
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                msg = mView.getActivity().getString(R.string.plugged_wireless);
                break;
            default:
                msg = "---";
                break;
        }
        mView.setChargePlugText(msg);
    }

    private void updateBatteryLevel() {

        Context context = mView.getContext();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        mView.setLevel(batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        mView.setScale(batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1));

        mView.setBatteryPercentage(mView.getLevel() / (float) mView.getScale());
        //String.format(".3f",batteryPct);

        //tvBatteryLevel.setText(String.format("%.2f",batteryPct));

        mView.setBatteryLevelText((Integer.toString(Math.round(mView.getBatteryPercentage() * 100))) + "%");
    }

    private void updateHealth() {
        String msg;
        Context context = mView.getContext();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        mView.setHealth(batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
        switch (mView.getHealth()) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                msg = mView.getActivity().getString(R.string.health_cold);
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                msg = mView.getActivity().getString(R.string.health_dead);
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                msg = mView.getActivity().getString(R.string.health_good);
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                msg = mView.getActivity().getString(R.string.health_over_voltage);
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                msg = mView.getActivity().getString(R.string.health_overheat);
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                msg = mView.getActivity().getString(R.string.health_unknown);
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                msg = mView.getActivity().getString(R.string.health_unspecified_failure);
                break;
            default:
                msg = mView.getActivity().getString(R.string.error);
                break;
        }
        mView.setHealthText(msg);
    }

    private void updateTechnology() {
        Context context = mView.getContext();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        mView.setTechnology(batteryStatus.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY));
        mView.setTechnologyText(mView.getTechnology());
    }

    private void updateTemperature() {
        Context context = mView.getContext();
        String aux;

        aux = DefinitionsPresenterImpl.getSharedPrefTemperature(mView.getContext().getApplicationContext());


        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        mView.setTemperature(batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));

        if (aux.equals("F")) {
            mView.setTemperatureText(Float.toString((float) ((mView.getTemperature() / 10) * 1.8 + 32)) + "F");
        } else {
            mView.setTemperatureText(Float.toString((float) (mView.getTemperature() / 10)) + "ºC");
        }
    }

    private void updateVoltage() {
        Context context = mView.getContext();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        mView.setVoltage(batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));

        mView.setVoltageText(Integer.toString(mView.getVoltage()) + "mV");
    }

    private void update() {
        updateStatus();
        updateChargePlug();
        updateBatteryLevel();
        updateHealth();
        updateTechnology();
        updateTemperature();
        updateVoltage();
    }

    private void stopSound() {
        try {
            mView.setStopSound();
            mView.releaseSound();
            mView.setSoundNull();
        } catch (Exception e) {
            //Empty on purpose
        }
    }

    private void checkBatteryLevel() {
        Context context = mView.getContext();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        mView.setLevel(batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        mView.setScale(batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
        mView.setStatus(batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1));

        mView.setBatteryPercentage(mView.getLevel() / (float) mView.getScale());


        getMinBattery();

        //AVISO PARA CARREGAR
        if (mView.getBatteryPercentage() * 100 <= mView.getMinBatter() && mView.getStatus() == BatteryManager.BATTERY_STATUS_DISCHARGING) {  //70 para testar, mas é 30
            //Carregar bateria
            alertDialog(mView.getActivity().getString(R.string.warning), mView.getActivity().getString(R.string.charge_battery));
            mView.setAuxDialog(1);
        } else if (mView.getBatteryPercentage() * 100 <= mView.getMinBatter() && mView.getStatus() == BatteryManager.BATTERY_STATUS_CHARGING) {
            //Dismiss alert Dialog
            try {
                mView.dismissAlertDialog();
                stopSound();
                try {
                    mView.cancelVibrator();
                } catch (Exception e) {
                }
            } catch (Exception e) {
                //Empty on purpose
            } finally {
                mView.setAuxDialog(0);
            }
        }

        //  AVISO PARA DESCARREGAR
        else if (mView.getBatteryPercentage() * 100 >= MAXBATTERY && mView.getStatus() == BatteryManager.BATTERY_STATUS_CHARGING) {
            //Desligar o carregador
            alertDialog(mView.getActivity().getString(R.string.warning), mView.getActivity().getString(R.string.disconnect_charger));
        } else if (mView.getBatteryPercentage() * 100 >= MAXBATTERY && mView.getStatus() == BatteryManager.BATTERY_STATUS_DISCHARGING) {
            //Dismiss alert Dialog
            try {
                mView.dismissAlertDialog();
                stopSound();
                try {
                    mView.cancelVibrator();
                } catch (Exception e) {
                }
            } catch (Exception e) {
                //Empty on purpose
            } finally {
                mView.setAuxDialog(0);
            }

        }

        mView.setBatteryLevelText((Integer.toString(Math.round(mView.getBatteryPercentage() * 100))) + "%");

    }

    private void checkTemperature() {
        Context context = mView.getContext();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        mView.setTemperature(batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));


        if (mView.getTemperature() / 10 >= MAXTEMPERATURE)   //default=40
        {
            //Temperatura muito alta
            alertDialog(mView.getActivity().getString(R.string.warning), mView.getActivity().getString(R.string.temperature_high));
            mView.setAuxDialog2(1);
        }

    }

    public void getMinBattery() {

        mView.setMinBattery(Integer.parseInt(DefinitionsPresenterImpl.getSharedPrefBattery(mView.getActivity().getApplicationContext())));
    }

    @Override
    public void threadGetDataFiles() {
        new Thread(new Runnable() {
            public void run() {

                int auxMax = MAX_GRAPH_POINTS;
                //SE NÃO CONSEGUIR OBTER DO ARMAZENAMENTO INTERNO VÊ NO EXTERNO
                if (getDataFromCSV(mView.getActivity().getFilesDir().getAbsolutePath() + "/" + Util.FILE_PATH_INTERNAL)) {
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                    /*for (int i = 0; i < mView.getListRecordsSize(); i++) {
                        series.appendData(new DataPoint(i, mView.getListRecordsBatteryLevel(i)), true, MAX_GRAPH_POINTS);
                    }*/

                    if(mView.getListRecordsSize() < auxMax)
                    {
                        auxMax = mView.getListRecordsSize();
                    }

                    List<CVSFile> auxListLastElements = new ArrayList<>();
                    auxListLastElements = mView.getListRecords().subList(mView.getListRecordsSize() - auxMax, mView.getListRecordsSize());


                    for(int i = 0; i< auxMax; i++){
                        //Vai buscar os MAX_GRAPH_POINTS últimos da lista
                        series.appendData(new DataPoint(i, auxListLastElements.get(i).getBatteryLevel()/*mView.getListRecordsBatteryLevel(mView.getListRecordsSize() - (MAX_GRAPH_POINTS + i))*/), true, MAX_GRAPH_POINTS);
                    }


                    mView.addSeriesGraphView(series);

                } else {
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
                    for (int i = 0; i < mView.getListRecordsSize(); i++) {
                        series.appendData(new DataPoint(i, mView.getListRecordsBatteryLevel(i)), true, 10);
                    }

                    mView.addSeriesGraphView(series);
                }

            }
        }).start();
    }

    public boolean getDataFromCSV(String path) {

        String token = ",";
        String[] separated;

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;

            while ((line = br.readLine()) != null) {
                separated = line.split(token);
                String date = separated[0];
                String hours = separated[1];
                Date recordDate = new SimpleDateFormat("yyyy/MM/dd/hh:mm:ss").parse(date + "/" + hours);
                mView.addListRecords(separated[2], recordDate);
            }
            br.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void alertDialog(String title, String message) {
        String sound;


        if (mView.getSound() == null)
            mView.setSound(MediaPlayer.create(mView.getContext(), R.raw.ding));


        sound = DefinitionsPresenterImpl.getSharedPrefSound(mView.getActivity().getApplicationContext());

        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);


        // Setting Positive "Yes" Button
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                // Write your code here to invoke OK event

            }
        });

        // Showing Alert Message
        alertDialog.show();*/


        if ((mView.getAuxDialog() == 1 && message.equals(mView.getActivity().getString(R.string.charge_battery))) || (mView.getAuxDialog2() == 1 && message.equals(mView.getActivity().getString(R.string.temperature_high))))
            return;
        else {

            mView.setAlertDialog(new AlertDialog.Builder(mView.getActivity()).create());
            mView.setTitleAlertDialog(title);
            // Setting Dialog Message
            mView.setMessageAlertDialog(message);


            mView.setClickListenerAlertDialog();
            // Setting Positive "Yes" Button
            /*alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke OK event
                    stopSound();
                    try {
                        mView.cancelVibrator();
                    } catch (Exception e) {
                    }
                }
            });*/

            if (getVibrate()) {
                //vibrate is on
                // Get instance of Vibrator from current Context
                mView.setVibrate();

                // Start without a delay
                // Vibrate for 100 milliseconds
                // Sleep for 1000 milliseconds
                long[] pattern = {0, 400, 1000};

                // The '0' here means to repeat indefinitely
                // '0' is actually the index at which the pattern keeps repeating from (the start)
                // To repeat the pattern from any other point, you could increase the index, e.g. '1'
                mView.setVibratorPattern(pattern, 0);
            }
            if (sound.equals("OnePlay")) {
                mView.startSound();
            } else if (sound.equals("Loop")) {
                mView.startSound();
                mView.loopSound();
            }

            // Showing Alert Message
            mView.showAlertDialog();
        }

    }

    public boolean getVibrate() {
        if (DefinitionsPresenterImpl.getSharedPrefVibration(mView.getActivity().getApplicationContext()).equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void alerDialogClickListener() {
        stopSound();
        try {
            mView.cancelVibrator();
        } catch (Exception e) {
        }
    }

    @Override
    public void stopAllSound() {
        stopSound();
    }

    @Override
    public void updateAll() {
        update();
    }

    @Override
    public void definitionsClickListener() {
        mView.definitionsClickListener();
    }

    @Override
    public void resetDataClickListener() {
        mView.resetDataClickListener();
    }

    @Override
    public void resetData() {
        String pathInternal = mView.getActivity().getApplication().getApplicationContext().getFilesDir().getAbsolutePath() + "/" + Util.FILE_PATH_INTERNAL;
        String pathExternal = Util.FILE_PATH;

        File file = null;
        PrintWriter pw = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        FileOutputStream fOut = null;
        try {
            //External
            file = new File(pathExternal);
            fw = new FileWriter(file.getAbsoluteFile(), false);
            bw = new BufferedWriter(fw);
            bw.write("");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            //Internal
            file = new File(pathInternal);
            file.delete();


    }
}
