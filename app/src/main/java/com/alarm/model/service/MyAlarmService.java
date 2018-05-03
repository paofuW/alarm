package com.alarm.model.service;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.alarm.model.bean.Alarm;
import com.alarm.model.util.AlarmDataUtil;
import com.alarm.model.util.AlarmServiceUtil;
import com.alarm.model.util.ArrayUtil;

import java.util.Calendar;

/**
 * Created by Administrator on 2018/4/29.
 */

public class MyAlarmService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        final Alarm alarm = intent.getParcelableExtra("alarm");
        Boolean isRepeat = intent.getBooleanExtra("isRepeat", false);

        Boolean needToAlarm = true;
        Calendar calendar = Calendar.getInstance();

        if(isRepeat){
            int[] frequency = AlarmDataUtil.freqStrToInt(alarm.getFrequency());
            needToAlarm = (frequency!=null && ArrayUtil.containInt(frequency, calendar.get(Calendar.DAY_OF_WEEK)));
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                AlarmServiceUtil.setWindowAlarmService(MyAlarmService.this, alarm);
            }
        }
        if(needToAlarm){
            if(alarm.getRemindAfter() == 0){
                new AlertDialog.Builder(MyAlarmService.this)
                        .setTitle("闹钟来了")
                        .setMessage(alarm.getDescription())
                        .setNegativeButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
            }else{
                new AlertDialog.Builder(MyAlarmService.this)
                        .setTitle("稍后提醒")
                        .setMessage(alarm.getDescription())
                        .setPositiveButton("那行", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alarm.setID(AlarmDataUtil.createID());
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                                calendar.set(Calendar.MINUTE, alarm.getMinute());
                                calendar.add(Calendar.MINUTE, alarm.getRemindAfter());
                                alarm.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                                alarm.setMinute(calendar.get(Calendar.MINUTE));
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                                    AlarmServiceUtil.setWindowAlarmService(MyAlarmService.this, alarm);
                                }else{
                                    AlarmServiceUtil.setAlarmService(MyAlarmService.this, alarm);
                                }
                            }
                        }).setNegativeButton("不要", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
