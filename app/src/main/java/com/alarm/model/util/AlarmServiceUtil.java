package com.alarm.model.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alarm.model.bean.Alarm;
import com.alarm.model.service.AlarmReceiverService;
import com.alarm.view.AlarmAlert;

import java.util.List;

/**
 * Created by Administrator on 2018/4/30.
 */

public class AlarmServiceUtil {
    private final static String ServiceName = "com.alarm.AlarmReceiverService";

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
        Log.i("AlarmServiceUtil", className + " isRunning= " + isRunning);
        return isRunning;
    }

    //适用于sdk版本小于19的android机（只提醒一次）
//    public static void setAlarmService(Context context, Alarm alarm){
//        PendingIntent pi = null;
//        Intent serviceIntent = new Intent(context, AlarmReceiverService.class);
//        serviceIntent.setAction(ServiceName);
//        serviceIntent.putExtra("alarm", alarm);
//        serviceIntent.putExtra("isRepeat", false);
//
//        try {
//            pi = PendingIntent.getService(context, alarm.getID(), serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        } catch (Exception e) {
//            Log.i("AlarmServiceUtil", "failed to start " + e.toString());
//        }
//        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
//        //alarmTimeMillis ： 闹钟设定时间的毫秒数
//        long alarmTimeMillis = TimeUtil.getAlarmTimeMillis(alarm.getHour(), alarm.getMinute());
//        am.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pi);
//    }

    //适用于sdk版本大于19的android机（提醒一次）
    public static void setWindowAlarmService(Context context, Alarm alarm, int minute){
        PendingIntent pi = null;
        Intent alarmIntent = new Intent(context, AlarmAlert.class);
        alarmIntent = AlarmDataUtil.putParcelableExtra(alarmIntent, alarm);
        alarmIntent.putExtra("savedInstanceState", false);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        try {
            pi = PendingIntent.getActivity(context, alarm.getID(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            Log.i("AlarmServiceUtil", "failed to start " + e.toString());
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        //alarmTimeMillis ： 闹钟设定时间的毫秒数,listenMinute是延后分钟数

        long alarmTimeMillis = TimeUtil.getAlarmTimeMillis(alarm.getHour(), alarm.getMinute(), minute);
        am.setWindow(AlarmManager.RTC_WAKEUP, alarmTimeMillis, 0, pi);
    }

//    备份本
//    public static void setWindowAlarmService(Context context, Alarm alarm, boolean listen){
//        PendingIntent pi = null;
//        Intent serviceIntent = new Intent(context, AlarmReceiverService.class);
//        serviceIntent.setAction(ServiceName);
//        serviceIntent = AlarmDataUtil.putParcelableExtra(serviceIntent, alarm);
//        serviceIntent.putExtra("listen", listen);
//        serviceIntent.putExtra("savedInstanceState", false);
//
//        try {
//            pi = PendingIntent.getService(context, alarm.getID(), serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        } catch (Exception e) {
//            Log.i("AlarmServiceUtil", "failed to start " + e.toString());
//        }
//        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
//        //alarmTimeMillis ： 闹钟设定时间的毫秒数,listenMinute是延后分钟数
//
//        long alarmTimeMillis = TimeUtil.getAlarmTimeMillis(alarm.getHour(), alarm.getMinute(), listen);
//        am.setWindow(AlarmManager.RTC_WAKEUP, alarmTimeMillis, 0, pi);
//    }

    public static void cancelAlarmService(Context context, Alarm alarm){
        Log.i("AlarmServiceUtil", "cancelAlarm:" + alarm.getHour() + ":" + alarm.getMinute());
        Intent cancelServiceIntent = new Intent(context,AlarmReceiverService.class);
        cancelServiceIntent.setAction(ServiceName);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context, alarm.getID(), cancelServiceIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }
}
