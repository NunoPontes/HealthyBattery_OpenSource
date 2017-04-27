package com.keepitsimplestudios.healthybattery.Tips;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.keepitsimplestudios.healthybattery.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TipsActivity extends AppCompatActivity {

    @BindView(R.id.tvTips)
    TextView tvTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        ButterKnife.bind(this);

        tvTips.setText(getResources().getString(R.string.tipnumber1) + " " + getResources().getString(R.string.tip1)+ "\n\n" +
                getResources().getString(R.string.tipnumber2) + " " +  getResources().getString(R.string.tip2) + "\n\n" +
                getResources().getString(R.string.tipnumber3) + " " +  getResources().getString(R.string.tip3) + "\n\n" +
                getResources().getString(R.string.tipnumber4) + " " +  getResources().getString(R.string.tip4) + "\n\n" +
                getResources().getString(R.string.tipnumber5) + " " +  getResources().getString(R.string.tip5) + "\n\n" +
                getResources().getString(R.string.tipnumber6) + " " +  getResources().getString(R.string.tip6));
    }
}
