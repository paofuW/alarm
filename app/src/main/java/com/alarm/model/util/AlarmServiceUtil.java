package com.alarm.model.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alarm.model.bean.Alarm;
import com.alarm.model.service.MyAlarmService;

import java.util.List;

/**
 * Created by Administrator on 2018/4/30.
 */

public class AlarmServiceUtil {
    private final static String ServiceName="com.alarm.MyAlarmService";

    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(50);

        if(null == serviceInfos || serviceInfos.size() < 1) {
            return false;
        }

        for(int i = 0; i < serviceInfos.size(); i++) {
            if(serviceInfos.get(i).service.getClassName().contains(className)) {
                isRunning = true;
                break;
            }
        }
        Log.i("ServiceUtil", className + " isRunning= " + isRunning);
        return isRunning;
    }

    //适用于sdk版本小于19的android机（只提醒一次）
    public static void setAlarmService(Context context, Alarm alarm){
        Log.i("ServiceUtil", "invokeTimerPOIService wac called.." );
        PendingIntent pi = null;
        Intent serviceIntent = new Intent(context, MyAlarmService.class);
        serviceIntent.setAction(ServiceName);
        serviceIntent.putExtra("alarm", alarm);
        serviceIntent.putExtra("isRepeat", false);

        try {
            pi = PendingIntent.getService(context, alarm.getID(), serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            Log.i("ServiceUtil", "failed to start " + e.toString());
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        //alarmTimeMillis ： 闹钟设定时间的毫秒数
        long alarmTimeMillis = TimeUtil.getAlarmTimeMillis(alarm.getHour(), alarm.getMinute());
        am.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pi);
    }

    //适用于sdk版本小于19的android机（重复提醒）
    public static void setRepeatAlarmService(Context context, Alarm alarm){
        Log.i("ServiceUtil", "invokeTimerPOIService wac called.." );
        PendingIntent pi = null;
        Intent serviceIntent = new Intent(context, MyAlarmService.class);
        serviceIntent.setAction(ServiceName);
        serviceIntent.putExtra("alarm", alarm);
        serviceIntent.putExtra("isRepeat", true);

        try {
            pi = PendingIntent.getService(context, alarm.getID(), serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            Log.i("ServiceUtil", "failed to start " + e.toString());
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        //alarmTimeMillis ： 闹钟设定时间的毫秒数
        long alarmTimeMillis = TimeUtil.getAlarmTimeMillis(alarm.getHour(), alarm.getMinute());
        am.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeMillis, 24*60*60*1000, pi);
    }

    //适用于sdk版本大于19的android机（提醒一次）
    public static void setWindowAlarmService(Context context, Alarm alarm){
        Log.i("ServiceUtil", "invokeTimerPOIService wac called.." );
        boolean isRepeat = !alarm.getFrequency().equals("一次");
        PendingIntent pi = null;
        Intent serviceIntent = new Intent(context, MyAlarmService.class);
        serviceIntent.setAction(ServiceName);
        serviceIntent.putExtra("alarm", alarm);
        serviceIntent.putExtra("isRepeat", isRepeat);

        try {
            pi = PendingIntent.getService(context, alarm.getID(), serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            Log.i("ServiceUtil", "failed to start " + e.toString());
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        //alarmTimeMillis ： 闹钟设定时间的毫秒数
        long alarmTimeMillis = TimeUtil.getAlarmTimeMillis(alarm.getHour(), alarm.getMinute());
        am.setWindow(AlarmManager.RTC_WAKEUP, alarmTimeMillis, 0, pi);
    }

    public static void cancleAlarmServicer(Context context, Alarm alarm){
        Log.i("ServiceUtil", "cancelAlarmManager to start ");
        Intent cancelServiceIntent = new Intent(context,MyAlarmService.class);
        cancelServiceIntent.setAction(ServiceName);
        PendingIntent pendingIntent=PendingIntent.getService(context, alarm.getID(), cancelServiceIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }
}
