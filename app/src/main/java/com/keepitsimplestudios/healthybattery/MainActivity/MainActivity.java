package com.keepitsimplestudios.healthybattery.MainActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.keepitsimplestudios.healthybattery.Definitions.Definitions;
import com.keepitsimplestudios.healthybattery.R;
import com.keepitsimplestudios.healthybattery.Tips.TipsActivity;
import com.keepitsimplestudios.healthybattery.Utils.CVSFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static android.R.attr.permission;




//TODO: layout tablets
//TODO: botão fixo
//TODO: tempo estimado de bateria
//TODO: capacidade da bateria em mha
//TODO: desenhar grafico assim q se tenham dados
//TODO: erro crashlytics
//TODO:proguard
//Done: fazer com que ao ler o ficheiro leia apenas os ultimos, ao não ser que dê para fazer scroll no grafico

//Note: está-se apenas a usar armazenamento interno, apesar de para externo tb estar implementado
//(à excepção de ir buscar dados ao externo)
//Consultar:https://developer.android.com/training/basics/data-storage/files.html (faz mais sentido gravar apenas no interno)
//tendo isto em conta, é desnecessário usar a biblioteca Dexter, pq esta era usada para as permissões em runtime para escrever no externo
//portanto vou comentar tudo relacionado com dexter e externo

public class MainActivity extends AppCompatActivity implements MainActivityView {

    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvPlug)
    TextView tvPlug;
    @BindView(R.id.tvBatteryLevel)
    TextView tvBatteryLevel;
    @BindView(R.id.tvHealth)
    TextView tvHealth;
    @BindView(R.id.tvTechnology)
    TextView tvTechnology;
    @BindView(R.id.tvTemperature)
    TextView tvTemperature;
    @BindView(R.id.tvVoltage)
    TextView tvVoltage;
    @BindView(R.id.tvBatteryCapacityMAH)
    TextView tvBatteryCapacityMAH;
    @BindView(R.id.graphBattery)
    GraphView gvBatteryUsage;
    @BindView(R.id.facDefinitions)
    FloatingActionButton facDefinitions;
    @BindView(R.id.btnResetData)
    Button btnResetData;

    private MainActivityPresenter mPresenter;


    private static final String TAG = "MainActivity";
    private int minBattery = 20;
    private MediaPlayer mp;
    private int status, chargePlug, level, scale, health, temperature, voltage;
    private float batteryPct;
    public String technology;
    private int auxDialog, auxDialog2; //variaveis auxiliares para controlar alert dialogs
    private AlertDialog alertDialog;
    private Vibrator v;
    private List<CVSFile> listRecords = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //Dexter.withActivity(this)
        //        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        //       .withListener(new PermissionListener() {
        //            @Override public void onPermissionGranted(PermissionGrantedResponse response) {/*Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();*/}
        //            @Override public void onPermissionDenied(PermissionDeniedResponse response) {/*Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_LONG).show();*/}
        //            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
        //        }).check();
        mPresenter = new MainActivityPresenterImpl(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mPresenter.startServices();
        mPresenter.registerBroadcasts();

        //this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        auxDialog = 0;
        auxDialog2 = 0;

        gvBatteryUsage.getViewport().setYAxisBoundsManual(true);
        gvBatteryUsage.getViewport().setMinY(0);
        gvBatteryUsage.getViewport().setMaxY(100);

        gvBatteryUsage.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        mPresenter.threadGetDataFiles();
        mPresenter.definitionsClickListener();
        mPresenter.resetDataClickListener();

        //update();
        //checkBatteryLevel();
        //checkTemperature();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tips, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tips) {
            Intent nextScreen = new Intent(MainActivity.this, TipsActivity.class);
            startActivity(nextScreen);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void definitionsClickListener() {
        facDefinitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(MainActivity.this, Definitions.class);
                startActivity(nextScreen);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //mPresenter.updateAll();
        //mPresenter.threadGetDataFiles();
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        //update();
        //checkBatteryLevel();
        //checkTemperature();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //update();
        //checkBatteryLevel();
        //checkTemperature();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.stopAllSound();
        //stopSound();

        try {
            v.cancel();
        } catch (Exception e) {
        }
        mPresenter.unregisterBroadcasts();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.stopAllSound();
        //stopSound();
        try {
            v.cancel();
        } catch (Exception e) {
        }
    }


    @Override
    public void addListRecords(String integer, Date date) {
        listRecords.add(new CVSFile(Integer.parseInt(integer), date));
    }


    @Override
    public void cancelVibrator() {
        v.cancel();
    }

    @Override
    public void setVibrate() {
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setVibratorPattern(long[] sequel, int i) {
        v.vibrate(sequel, i);
    }

    @Override
    public void setBatteryLevelText(String text) {
        tvBatteryLevel.setText(text);
    }

    @Override
    public void setStatusText(String text) {
        tvStatus.setText(text);
    }

    @Override
    public void setChargePlugText(String text) {
        tvPlug.setText(text);
    }

    @Override
    public void setHealthText(String text) {
        tvHealth.setText(text);
    }

    @Override
    public void setTechnologyText(String text) {
        tvTechnology.setText(text);
    }

    @Override
    public void setTemperatureText(String text) {
        tvTemperature.setText(text);
    }

    @Override
    public void setVoltageText(String text) {
        tvVoltage.setText(text);
    }

    @Override
    public void setBatteryCapacityText(String text) {
        tvBatteryCapacityMAH.setText(text);
    }

    @Override
    public void releaseSound() {
        mp.release();
    }

    @Override
    public void setSoundNull() {
        mp = null;
    }

    @Override
    public void setStopSound() {
        mp.stop();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public float getBatteryPercentage() {
        return batteryPct;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getChargePlug() {
        return chargePlug;
    }

    @Override
    public void setChargePlug(int chargePlug) {
        this.chargePlug = chargePlug;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getScale() {
        return scale;
    }

    @Override
    public void setScale(int scale) {
        this.scale = scale;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public int getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public int getVoltage() {
        return voltage;
    }

    @Override
    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    @Override
    public void setBatteryPercentage(float batteryPercentage) {
        batteryPct = batteryPercentage;
    }

    @Override
    public String getTechnology() {
        return technology;
    }

    @Override
    public void setTechnology(String technology) {
        this.technology = technology;
    }

    @Override
    public int getMinBatter() {
        return minBattery;
    }

    @Override
    public void setMinBattery(int minBattery) {
        this.minBattery = minBattery;
    }

    @Override
    public void dismissAlertDialog() {
        alertDialog.dismiss();
    }

    @Override
    public int getAuxDialog() {
        return auxDialog;
    }

    @Override
    public void setAuxDialog(int auxDialog) {
        this.auxDialog = auxDialog;
    }

    @Override
    public int getAuxDialog2() {
        return auxDialog2;
    }

    @Override
    public void setAuxDialog2(int auxDialog2) {
        this.auxDialog2 = auxDialog2;
    }

    @Override
    public void showAlertDialog() {
        this.alertDialog.show();
    }

    @Override
    public void loopSound() {
        mp.setLooping(true);
    }

    @Override
    public void startSound() {
        mp.start();
    }

    @Override
    public MediaPlayer getSound() {
        return mp;
    }

    @Override
    public void setSound(MediaPlayer mp) {
        this.mp = mp;
    }

    @Override
    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    @Override
    public void setAlertDialog(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    @Override
    public void setTitleAlertDialog(String title) {
        alertDialog.setTitle(title);
    }

    @Override
    public void setMessageAlertDialog(String message) {
        alertDialog.setMessage(message);
    }

    @Override
    public void addSeriesGraphView(LineGraphSeries<DataPoint> series) {
        series.setColor(getResources().getColor(R.color.primary));
        gvBatteryUsage.addSeries(series);
    }

    @Override
    public int getListRecordsSize() {
        return listRecords.size();
    }

    @Override
    public List<CVSFile> getListRecords() {
        return listRecords;
    }

    @Override
    public int getListRecordsBatteryLevel(int index) {
        return listRecords.get(index).getBatteryLevel();
    }

    @Override
    public void setClickListenerAlertDialog() {
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke OK event
                mPresenter.alerDialogClickListener();
            }
        });
    }

    @Override
    public void resetDataClickListener() {
        btnResetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.are_you_sure);
                builder.setMessage(R.string.every_data_will_be_deleted);
                builder.setCancelable(true);

                builder.setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mPresenter.resetData();
                                gvBatteryUsage.removeAllSeries(); //to redraw graph
                                dialog.cancel();
                            }
                        });

                builder.setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }
}








