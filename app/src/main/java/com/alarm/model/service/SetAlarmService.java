package com.alarm.model.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alarm.model.bean.Alarm;
import com.alarm.model.db.AlarmDB;
import com.alarm.model.receiver.TimeChangedReceiver;
import com.alarm.model.util.AlarmServiceUtil;
import com.alarm.presenter.DataHandler;

import java.util.ArrayList;

import static com.alarm.presenter.DataInit.ALTER_ALARM;
import static com.alarm.presenter.DataInit.DELETE_ALARM;
import static com.alarm.presenter.DataInit.INIT;
import static com.alarm.presenter.DataInit.REMIND_LATER;

/**
 * Created by Administrator on 2018/5/28.
 */

public class SetAlarmService extends Service {
    private AlarmDB db;
    private ArrayList<Alarm> alarms;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SetAlarmService", "设置闹钟服务开启");
        db = AlarmDB.getInstance(SetAlarmService.this, 2);
        alarms = db.loadAllAlarms();
        db = null;

        for(int i = 0; i < alarms.size(); i++){
            if(alarms.get(i).getEnabled()) {
                Log.i("SetAlarmService", "设置闹钟服务:开启闹钟" + alarms.get(i).getID());
                AlarmServiceUtil.setWindowAlarmService(SetAlarmService.this, alarms.get(i), 0);
            }
        }
        TimeChangedReceiver timeChangedReceiver = new TimeChangedReceiver();
        registerReceiver(timeChangedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Alarm alarm;
        alarm = intent.getParcelableExtra("alarm");
        switch(intent.getIntExtra("typeOfOperation", ALTER_ALARM)){
            case INIT:
                break;
            case DELETE_ALARM:
                AlarmServiceUtil.cancelAlarmService(SetAlarmService.this, alarm);
                break;
            case REMIND_LATER:
                AlarmServiceUtil.setWindowAlarmService(SetAlarmService.this, alarm, 10);
                break;
            default:
                Log.i("SetAlarmService", alarm.getID() + ":" + DataHandler.getTimeString(alarm.getHour(), alarm.getMinute()));
                AlarmServiceUtil.setWindowAlarmService(SetAlarmService.this, alarm, 0);
                break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        Intent intent = new Intent(this, AlarmReceiverService.class);
        intent.putExtra("savedInstanceState", true);
        startService(intent);
        Log.i("SetAlarmService", "SetAlarmService : onDestroy");
        super.onDestroy();
    }
}
