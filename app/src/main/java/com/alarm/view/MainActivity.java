package com.alarm.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alarm.R;


/**
 * Created by Administrator on 2018/4/23.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private Button createAlarm;

    private final static int CANCEL = 0;
    private final static int CREATE_ALARM = 1;
    private final static int ALTER_ALARM = 2;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_main);
        createAlarm = (Button)findViewById(R.id.btn_main_createAlarm);
        createAlarm.setOnClickListener(this);

        intent = new Intent(MainActivity.this, AlarmDetail.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_createAlarm:
                intent.putExtra("typeOfOperation", CREATE_ALARM);
                startActivityForResult(intent, CREATE_ALARM);
                break;
            case R.id.alterAlarm:
                intent.putExtra("typeOfOperation", ALTER_ALARM);
                startActivityForResult(intent, ALTER_ALARM);
                break;
            default:
                break;
        }
    }
}
