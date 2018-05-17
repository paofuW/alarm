package com.alarm.presenter;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alarm.model.bean.Alarm;
import com.alarm.model.db.AlarmDB;
import com.alarm.model.util.AlarmDataUtil;
import com.alarm.model.util.AlarmServiceUtil;
import com.alarm.model.util.ArrayUtil;
import com.alarm.model.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by Administrator on 2018/4/26.
 */

public class DataHandler {
    private Context mContext;
    private AlarmDB db;

    public DataHandler(Context context){
        this.mContext = context;
        db = AlarmDB.getInstance(context, 1);
        Log.d("DataHandler", "get a DataHandler instance");
    }

    public ArrayList<Alarm> getAllAlarm(){
        Log.d("DataHandler", "try to get All Alarms");
        return db.loadAllAlarms();
    }

    public void saveAndSetAlarm(Alarm alarm){
        db.saveAlarm(alarm);
        setAlarm(alarm);
    }

    public void updateAndSetAlarm(Alarm alarm){
        db.updateAlarm(alarm);
        setAlarm(alarm);
    }

    public void removeAndCancelAlarm(Alarm alarm){
        db.removeAlarm(alarm);
        AlarmServiceUtil.cancleAlarmServicer(mContext, alarm);
    }

    private void setAlarm(Alarm alarm){
        //当sdk版本大于19时，使用新版的设置定时语句
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            AlarmServiceUtil.setWindowAlarmService(mContext, alarm);
        }else{
            if(alarm.getFrequency().equals("一次")){
                AlarmServiceUtil.setAlarmService(mContext, alarm);
            }else{
                AlarmServiceUtil.setRepeatAlarmService(mContext, alarm);
            }
        }
    }


    /**
     *
     * 以下是静态方法
     *
     */
    public static Integer[] getRestOfTime(int alarmHour, int alarmMinute){
        return TimeUtil.calculateRestOfTime(alarmHour, alarmMinute);
    }

    public static int getIndex(String[] stringArr, String item){
        return ArrayUtil.getIndex(stringArr, item);
    }

//    将稍后提醒的时间转化为int类型
    public static int getRemindAfter(String remindAfter){
        return AlarmDataUtil.remindAfStrToInt(remindAfter);
    }

    public static ArrayList<String> getAllMusicName(HashMap hashMap){
        ArrayList<String> keys = new ArrayList<>();
        Iterator it = hashMap.keySet().iterator();
        while(it.hasNext()) {
            keys.add((String)it.next());
        }
        return  keys;
    }

    //临时使用
    public static ArrayList<String> getAllAlarmTime(ArrayList<Alarm> alarms){
        ArrayList<String> alarmTimes = new ArrayList<>();
        for(Alarm alarm : alarms){
            alarmTimes.add(getAlarmTime(alarm));
        }
        return alarmTimes;
    }

    public static String getAlarmTime(Alarm alarm){
        return (alarm.getHour() + ":" + alarm.getMinute());
    }
}
