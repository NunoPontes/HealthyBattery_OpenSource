package com.keepitsimplestudios.healthybattery.MainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.keepitsimplestudios.healthybattery.Utils.CVSFile;

import java.util.Date;
import java.util.List;

/**
 * Created by nunop on 27/01/2017.
 */

interface MainActivityView {

    Activity getActivity();

    void cancelVibrator();

    void setVibratorPattern(long[] sequel, int i);

    void setStatusText(String text);

    void setChargePlugText(String text);

    void setBatteryLevelText(String text);

    void setHealthText(String text);

    void setTechnologyText(String text);

    void setTemperatureText(String text);

    void setVoltageText(String text);

    void setBatteryCapacityText(String text);

    int getStatus();

    void setStatus(int status);

    int getChargePlug();

    void setChargePlug(int chargePlug);

    int getLevel();

    void setLevel(int level);

    int getScale();

    void setScale(int scale);

    int getHealth();

    void setHealth(int health);

    int getTemperature();

    void setTemperature(int temperature);

    int getVoltage();

    void setVoltage(int voltage);

    float getBatteryPercentage();

    void setBatteryPercentage(float batteryPercentage);

    String getTechnology();

    void setTechnology(String technology);

    void setStopSound();

    void releaseSound();

    void setSoundNull();

    Context getContext();

    void addListRecords(String integer, Date date);

    int getMinBatter();

    void setMinBattery(int minBattery);

    void dismissAlertDialog();

    void showAlertDialog();

    int getAuxDialog();

    void setAuxDialog(int auxDialog);

    int getAuxDialog2();

    void setAuxDialog2(int auxDialog2);

    void startSound();

    void loopSound();

    void setVibrate();

    MediaPlayer getSound();

    void setSound(MediaPlayer mp);

    void setAlertDialog(AlertDialog alertDialog);

    AlertDialog getAlertDialog();

    void setTitleAlertDialog(String title);

    void setMessageAlertDialog(String message);

    void addSeriesGraphView(LineGraphSeries<DataPoint> series);

    int getListRecordsSize();

    List<CVSFile> getListRecords();

    int getListRecordsBatteryLevel(int index);

    void setClickListenerAlertDialog();

    void definitionsClickListener();

    void resetDataClickListener();

}
