package com.keepitsimplestudios.healthybattery.Definitions;

import android.app.Activity;
import android.content.SharedPreferences;


/**
 * Created by nunop on 29/01/2017.
 */

interface DefinitionsView {

    Activity getActivity();

    void initializeNumberPicker();

    void switchVibrateSetChecked(boolean checked);

    void setTemperature(String temperature);

    String getTemperature();

    void setSound(String sound);

    String getSound();

    void setVibrate(String vibrate);

    String getVibrate();

    void setSharedPreferences(SharedPreferences sp);

    SharedPreferences getSharedPreferences();

    boolean isSVibrateChecked();

    int getNPBattery();

    String getTemperatureDefinition();

    String getSoundDefinition();

    int getNotificationBattery();

    void setTemperatureDefinition(String temperature);

    void setSoundDefinition(String sound);

    void setNotificationBattery(int battery);

}
