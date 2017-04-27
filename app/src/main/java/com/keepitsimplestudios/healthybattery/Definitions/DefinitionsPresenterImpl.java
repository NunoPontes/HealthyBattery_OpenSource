package com.keepitsimplestudios.healthybattery.Definitions;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.keepitsimplestudios.healthybattery.Definitions.Definitions.Battery;
import static com.keepitsimplestudios.healthybattery.Definitions.Definitions.MyPREFERENCES;
import static com.keepitsimplestudios.healthybattery.Definitions.Definitions.Sound;
import static com.keepitsimplestudios.healthybattery.Definitions.Definitions.Temperature;
import static com.keepitsimplestudios.healthybattery.Definitions.Definitions.Vibration;
/*import static com.nunop.healthybattery.R.id.npBattery;
import static com.nunop.healthybattery.R.id.rbC;
import static com.nunop.healthybattery.R.id.rbF;
import static com.nunop.healthybattery.R.id.rbLoop;
import static com.nunop.healthybattery.R.id.rbOnePlay;
import static com.nunop.healthybattery.R.id.rgSound;
import static com.nunop.healthybattery.R.id.sVibrate;*/

/**
 * Created by nunop on 29/01/2017.
 */

public class DefinitionsPresenterImpl implements DefinitionsPresenter {

    private DefinitionsView mView;

    public DefinitionsPresenterImpl(DefinitionsView view) {
        mView = view;
    }

    @Override
    public void initializeElements() {
        mView.initializeNumberPicker();
        //mView.radioGroupCCheck();
        //mView.radioButtonCSetChecked(true);
        mView.setTemperatureDefinition("ºC");

        mView.setTemperature(getSharedPrefTemperature(mView.getActivity().getApplicationContext()));
        mView.setSound(getSharedPrefSound(mView.getActivity().getApplicationContext()));
        mView.setVibrate(getSharedPrefVibration(mView.getActivity().getApplicationContext()));

        if (mView.getTemperature().equals("ºC")) {
            mView.setTemperatureDefinition("ºC");
            //mView.radioButtonCSetChecked(true);
            //mView.radioButtonFSetChecked(false);
        }
        if (mView.getTemperature().equals("F")) {
            mView.setTemperatureDefinition("F");
            //mView.radioButtonCSetChecked(false);
            //mView.radioButtonFSetChecked(true);
        }

        //mView.radioGroupSoundCheck();
        //mView.radioButtonLoopSetChecked(true);
        mView.setSoundDefinition("Loop");

        if (mView.getSound().equals("OnePlay")) {
            mView.setSoundDefinition("One Play");
            //mView.radioButtonOnePlaySetChecked(true);
            //mView.radioButtonLoopSetChecked(false);
        }
        if (mView.getSound().equals("Loop")) {
            mView.setSoundDefinition("Loop");
            //mView.radioButtonOnePlaySetChecked(false);
            //mView.radioButtonLoopSetChecked(true);
        }

        if (mView.getVibrate().equals("true")) {
            mView.switchVibrateSetChecked(true);
        } else {
            mView.switchVibrateSetChecked(false);
        }
    }

    public static String getSharedPrefTemperature(Context context) {
        String temp;

        SharedPreferences shared = context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        temp = (shared.getString(Temperature, "ºC"));

        return temp;
    }

    public static String getSharedPrefSound(Context context) {
        String sound;

        SharedPreferences shared = context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        sound = (shared.getString(Sound, "Loop"));

        return sound;
    }

    public static String getSharedPrefVibration(Context context) {
        String temp;

        SharedPreferences shared = context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        temp = (shared.getString(Vibration, "true"));

        return temp;
    }

    public static String getSharedPrefBattery(Context context) {
        String temp;

        SharedPreferences shared = context.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        temp = (shared.getString(Battery, "30"));

        return temp;
    }

    private void setSharedPrefTemperature(String format) {
        mView.setSharedPreferences(mView.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE));
        SharedPreferences.Editor editor = mView.getSharedPreferences().edit();
        editor.putString(Temperature, format);
        editor.commit();
    }

    private void setSharedPrefSound(String format) {
        mView.setSharedPreferences(mView.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE));
        SharedPreferences.Editor editor = mView.getSharedPreferences().edit();
        editor.putString(Sound, format);
        editor.commit();
    }

    private void setSharedPrefVibration(String format) {
        mView.setSharedPreferences(mView.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE));
        SharedPreferences.Editor editor = mView.getSharedPreferences().edit();
        editor.putString(Vibration, format);
        editor.commit();
    }

    private void setSharedPrefBattery(String format) {
        mView.setSharedPreferences(mView.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE));
        SharedPreferences.Editor editor = mView.getSharedPreferences().edit();
        editor.putString(Battery, format);
        editor.commit();
    }

    @Override
    public void saveElements() {
        saveTemperature();
        saveSound();
        saveVibration();
        saveBattery();
    }


    private void saveTemperature() {
        if (mView.getTemperatureDefinition().equals("ºC"))
            setSharedPrefTemperature("ºC");
        if (mView.getTemperatureDefinition().equals("F"))
            setSharedPrefTemperature("F");
    }

    private void saveSound() {
        if (mView.getSoundDefinition().equals("One Play"))
            setSharedPrefSound("OnePlay");
        if (mView.getSoundDefinition().equals("Loop"))
            setSharedPrefSound("Loop");
    }

    private void saveVibration() {
        //Ir buscar valor e guardar
        if (mView.isSVibrateChecked()) {
            setSharedPrefVibration("true");
        } else {
            setSharedPrefVibration("false");
        }
    }

    private void saveBattery() {
        //Ir buscar valor ao picker e guardar
        setSharedPrefBattery(String.valueOf(mView.getNPBattery()));
    }
}
