package com.alarm.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alarm.R;
import com.alarm.model.bean.Alarm;
import com.alarm.presenter.DataHandler;

import java.util.ArrayList;
import java.util.Locale;

import static com.alarm.presenter.DataInit.ALTER_ALARM;
import static com.alarm.presenter.DataInit.CREATE_ALARM;
import static com.alarm.presenter.DataInit.DELETE_ALARM;
import static com.alarm.presenter.DataInit.TIMECHANGED;


/**
 * Created by Administrator on 2018/4/23.
 */

public class MainActivity extends AppCompatActivity{
    private Intent intent;
    private ArrayList<Alarm> alarms;
    private DataHandler dataHandler;
    private LayoutInflater inflater;
    private Alarm alarm;

    private TimeChangedRecv timeChangedRecv;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;

    private LinearLayout ll_main;
    private LinearLayout ll_main_showAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTitle("闹钟");
        setContentView(R.layout.alarm_main);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        timeChangedRecv = new TimeChangedRecv();
        intentFilter = new IntentFilter();
        intentFilter.addAction(TIMECHANGED);
        //注册本地接收器
        localBroadcastManager.registerReceiver(timeChangedRecv, intentFilter);

        dataHandler = new DataHandler(MainActivity.this);
        alarms = dataHandler.getAllAlarm();
        intent = new Intent(MainActivity.this, AlarmDetail.class);

        ll_main = (LinearLayout) findViewById(R.id.ll_main);
        ll_main_showAlarm = (LinearLayout)ll_main.findViewById(R.id.ll_main_showAlarm);
        inflater = (LayoutInflater)this.getSystemService (LAYOUT_INFLATER_SERVICE);
        int len = alarms.size();
        for(int i = 0; i<len; i++){
            ll_main_showAlarm.addView(initAlarmView(inflater, alarms.get(i)));
        }
        setContentView(ll_main);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(timeChangedRecv);
        dataHandler = null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MainActivity", resultCode + "");
        switch (resultCode){
            case CREATE_ALARM:
                alarm = data.getParcelableExtra("alarm");
                dataHandler.addAndSetAlarm(alarm);
                alarms.add(alarm);
                ll_main_showAlarm.addView(initAlarmView(inflater, alarm));
                setContentView(ll_main);
                break;
            case ALTER_ALARM:
                alarm = data.getParcelableExtra("alarm");
                dataHandler.updateAndSetAlarm(alarm);
                alarms.set(DataHandler.getPositionFromId(alarms, alarm.getID()), alarm);
                changeAlarmViewInfo(ll_main_showAlarm.findViewById(alarm.getID()), alarm);
                break;
            case DELETE_ALARM:
                alarm = data.getParcelableExtra("alarm");
                dataHandler.removeAndCancelAlarm(alarm);
                alarms.remove(alarm);
                ll_main_showAlarm.removeView(ll_main_showAlarm.findViewById(alarm.getID()));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_alarm:
                intent.putExtra("typeOfOperation", CREATE_ALARM);
                startActivityForResult(intent, CREATE_ALARM);
                Log.i("MainActivity", "add alarm");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View initAlarmView(LayoutInflater inflater, final Alarm alarm){
        final View view = inflater.inflate(R.layout.alarm_for_show, null);
        view.setId(alarm.getID());
        changeAlarmViewInfo(view, alarm);

        ((Switch)view.findViewById(R.id.swt_alarm_enabled)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                View view = (View)buttonView.getParent().getParent();
                TextView tv_alarm_restOfTime = (TextView)view.findViewById(R.id.tv_alarm_restOfTime);
                TypedValue outValue = new TypedValue();
                if(isChecked){
                    alarm.setEnabled(true);
                    getResources().getValue(R.dimen.turnOnAlpha, outValue, true);
                    view.setAlpha(outValue.getFloat());
                    tv_alarm_restOfTime.setText(DataHandler.getRestOfTimeString(alarm.getHour(), alarm.getMinute(), alarm.getFrequency()));
                    dataHandler.updateAndSetAlarm(alarm);
                }else{
                    alarm.setEnabled(false);
                    getResources().getValue(R.dimen.turnOffAlpha, outValue, true);
                    view.setAlpha(outValue.getFloat());
                    tv_alarm_restOfTime.setText("关闭");
                    dataHandler.updateAndCancelAlarm(alarm);
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("alarm", DataHandler.getAlarmFromId(alarms, v.getId()));
                intent.putExtra("typeOfOperation", ALTER_ALARM);
                startActivityForResult(intent, ALTER_ALARM);
            }
        });
        return view;
    }

    private void changeAlarmViewInfo(View view, Alarm alarm){
        TextView tv_alarm_time = (TextView)view.findViewById(R.id.tv_alarm_time);
        TextView tv_alarm_description = (TextView)view.findViewById(R.id.tv_alarm_description);
        TextView tv_alarm_frequency = (TextView)view.findViewById(R.id.tv_alarm_frequency);
        TextView tv_alarm_restOfTime = (TextView)view.findViewById(R.id.tv_alarm_restOfTime);
        Switch swt_alarm_enabled = (Switch)view.findViewById(R.id.swt_alarm_enabled);

        tv_alarm_time.setText(String.format(Locale.CHINA, "%1$02d:%2$02d", alarm.getHour(), alarm.getMinute()));
        tv_alarm_description.setText(alarm.getDescription());
        tv_alarm_frequency.setText(alarm.getFrequency());

        TypedValue outValue = new TypedValue();
        if(alarm.getEnabled()){
            getResources().getValue(R.dimen.turnOnAlpha, outValue, true);
            view.setAlpha(outValue.getFloat());
            tv_alarm_restOfTime.setText(DataHandler.getRestOfTimeString(alarm.getHour(), alarm.getMinute(), alarm.getFrequency()));
        }else{
            getResources().getValue(R.dimen.turnOffAlpha, outValue, true);
            view.setAlpha(outValue.getFloat());
            tv_alarm_restOfTime.setText("关闭");
        }
        swt_alarm_enabled.setChecked(alarm.getEnabled());
    }

    private void updateRestOfTime(){
        int len = alarms.size();
        Alarm alarm;
        TextView tv;
        for(int i = 0; i<len; i++){
            alarm = alarms.get(i);
            if(alarm.getEnabled()){
                tv = (TextView)(ll_main_showAlarm.findViewById(alarm.getID())).findViewById(R.id.tv_alarm_restOfTime);
                tv.setText(DataHandler.getRestOfTimeString(alarm.getHour(), alarm.getMinute(), alarm.getFrequency()));
            }
        }
    }

    public class TimeChangedRecv extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            updateRestOfTime();
        }
    }
}
