package com.keepitsimplestudios.healthybattery.Definitions;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.keepitsimplestudios.healthybattery.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/*import static com.nunop.healthybattery.R.id.npBattery;
import static com.nunop.healthybattery.R.id.rbC;
import static com.nunop.healthybattery.R.id.rbF;
import static com.nunop.healthybattery.R.id.rbLoop;
import static com.nunop.healthybattery.R.id.rbOnePlay;
import static com.nunop.healthybattery.R.id.sVibrate;*/

public class Definitions extends AppCompatActivity implements DefinitionsView {

    @BindView(R.id.sVibrate)
    Switch sVibrate;
    /*@BindView(R.id.rbC) RadioButton rbC;
    @BindView(R.id.rbF) RadioButton rbF;
    @BindView(R.id.rbOnePlay) RadioButton rbOnePlay;
    @BindView(R.id.rbLoop) RadioButton rbLoop;
    @BindView(R.id.rgTemperature) RadioGroup rg;
    @BindView(R.id.rgSound) RadioGroup rgSound;
    @BindView(R.id.npBattery) NumberPicker npBattery;*/
    @BindView(R.id.tv_settings_temperature)
    TextView tvTemperature;
    @BindView(R.id.tv_settings_sound)
    TextView tvSound;
    @BindView(R.id.tv_settings_notification)
    TextView tvBatterynotification;


    DefinitionsPresenter mPresenter;


    public static final String MyPREFERENCES = "Preferences";
    public static final String Temperature = "temperatureKey";
    public static final String Sound = "soundKey";
    public static final String Battery = "batteryKey";
    public static final String Vibration = "vibrationKey";

    SharedPreferences sharedpreferences;

    private String auxTemp, auxSound, auxVibrate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definitions);
        ButterKnife.bind(this);
        mPresenter = new DefinitionsPresenterImpl(this);

        /*sVibrate = (Switch) findViewById(R.id.sVibrate);
        rbC = (RadioButton) findViewById(R.id.rbC);
        rbF = (RadioButton) findViewById(R.id.rbF);
        rbOnePlay = (RadioButton) findViewById(R.id.rbOnePlay);
        rbLoop = (RadioButton) findViewById(R.id.rbLoop);
        rg = (RadioGroup) findViewById(R.id.rgTemperature);
        rgSound = (RadioGroup) findViewById(R.id.rgSound);
        npBattery = (NumberPicker) findViewById(R.id.npBattery);
*/
        mPresenter.initializeElements();

        /*FUTURAMENTE POR ISTO A FUNCIONAR PARA NÃO SER PRECISO O BOTÃO SAVE E SEMPRE QUE HOUVER UMA ALTERAÇÃO ELE GRAVA. MAS PODERA SER MT DISPENDIOSO. AVERIGUAR SE COMPENSA

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgTemperature);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                Toast.makeText(Definitions.this,""+checkedId,Toast.LENGTH_LONG).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_definitions; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_definitions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            mPresenter.saveElements();
            Toast.makeText(Definitions.this, R.string.definitions_saved, Toast.LENGTH_LONG).show();
            //Voltar para trás após ter salvo
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public String getSound() {
        return auxSound;
    }

    @Override
    public void setTemperature(String temperature) {
        auxTemp = temperature;
    }

    @Override
    public String getTemperature() {
        return auxTemp;
    }

    @Override
    public void setSound(String sound) {
        auxSound = sound;
    }

    @Override
    public void setVibrate(String vibrate) {
        auxVibrate = vibrate;
    }

    @Override
    public String getVibrate() {
        return auxVibrate;
    }
/*
    @Override
    public void radioButtonCSetChecked(boolean check) {
        rbC.setChecked(check);
    }

    @Override
    public void radioGroupCCheck() {
        rg.check(rbF.getId());
    }

    @Override
    public void radioGroupSoundCheck() {
        rgSound.check(rbOnePlay.getId());
    }

    @Override
    public void radioButtonFSetChecked(boolean check) {
        rbF.setChecked(check);
    }

    @Override
    public void radioButtonOnePlaySetChecked(boolean check) {
        rbOnePlay.setChecked(check);
    }

    @Override
    public void radioButtonLoopSetChecked(boolean check) {
        rbLoop.setChecked(check);
    }
*/
    @Override
    public void switchVibrateSetChecked(boolean checked) {
        sVibrate.setChecked(checked);
    }

    @Override
    public void initializeNumberPicker() {
        try {
            tvBatterynotification.setText(DefinitionsPresenterImpl.getSharedPrefBattery(getApplicationContext()));
            //npBattery.setValue(Integer.parseInt(DefinitionsPresenterImpl.getSharedPrefBattery(getApplicationContext())));
        } catch (Exception e) {
            tvBatterynotification.setText("20");
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return sharedpreferences;
    }

    @Override
    public void setSharedPreferences(SharedPreferences sp) {
        sharedpreferences = sp;
    }

    @Override
    public int getNPBattery() {
        return Integer.parseInt(tvBatterynotification.getText().toString());
    }

    /*
    @Override
    public boolean isRBCChecked() {
        return rbC.isChecked();
    }

    @Override
    public boolean isRBFChecked() {
        return rbF.isChecked();
    }

    @Override
    public boolean isRBOnePlayChecked() {
        return rbOnePlay.isChecked();
    }

    @Override
    public boolean isRBLoopChecked() {
        return rbLoop.isChecked();
    }
*/
    @Override
    public boolean isSVibrateChecked() {
        return sVibrate.isChecked();
    }


    @OnClick(R.id.tv_settings_notification)
    public void onNotificationBatteryClick() {
        show();

        /*CharSequence[] items =null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification when battery is");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Define text and save definition

                show();
                mPresenter.saveElements();
            }
        });
        builder.show();*/
    }

    public void show()
    {
        final Dialog d = new Dialog(Definitions.this);
        d.setTitle("Escolha a percentagem");
        d.setContentView(R.layout.layout_alert_dialog_number_picker);
        Button apply = (Button) d.findViewById(R.id.apply_button);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.number_picker);
        np.setMaxValue(79);
        np.setMinValue(20);
        np.setValue(Integer.parseInt(tvBatterynotification.getText().toString()));
        np.setWrapSelectorWheel(false);
        //np.setOnValueChangedListener(this);
        apply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                tvBatterynotification.setText(String.valueOf(np.getValue()));
                mPresenter.saveElements();
                d.dismiss();
            }
        });
        d.show();
    }

    @OnClick(R.id.tv_settings_temperature)
    public void onTemperatureClick() {
        final CharSequence[] items = {"ºC", "F"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Temperature Format");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Define text and save definition
                tvTemperature.setText(items[which]);
                mPresenter.saveElements();
            }
        });
        builder.show();
    }

    @OnClick(R.id.tv_settings_sound)
    public void onSoundClick() {
        final CharSequence[] items = {"One Play", "Loop"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sound Play");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Define text and save definition
                tvSound.setText(items[which]);
                mPresenter.saveElements();
            }
        });
        builder.show();
    }

    @OnCheckedChanged(R.id.sVibrate)
    public void onVibrateChange() {
        mPresenter.saveElements();
    }

    @Override
    public int getNotificationBattery() {
        return 0;
    }

    @Override
    public String getTemperatureDefinition() {
        return tvTemperature.getText().toString();
    }

    @Override
    public String getSoundDefinition() {
        return tvSound.getText().toString();
    }

    @Override
    public void setNotificationBattery(int battery) {
        tvBatterynotification.setText(String.valueOf(battery));
    }

    @Override
    public void setTemperatureDefinition(String temperature) {
        tvTemperature.setText(temperature);
    }

    @Override
    public void setSoundDefinition(String sound) {
        tvSound.setText(sound);
    }
}
