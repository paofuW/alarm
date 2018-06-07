package com.alarm.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.alarm.model.bean.Alarm;
import com.alarm.model.db.AlarmDB;
import com.alarm.model.receiver.TimeChangedReceiver;
import com.alarm.model.service.SetAlarmService;
import com.alarm.model.util.AlarmDataUtil;
import com.alarm.model.util.ArrayUtil;
import com.alarm.model.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import static com.alarm.presenter.DataInit.ALTER_ALARM;
import static com.alarm.presenter.DataInit.DELETE_ALARM;
import static com.alarm.presenter.DataInit.INIT;
import static com.alarm.presenter.DataInit.TIME_CHANGED;


/**
 * Created by Administrator on 2018/4/26.
 */

public class DataHandler {
    private Context mContext;
    private AlarmDB db;
    private ArrayList<Alarm> alarms;
    private Intent intent;
    private TimeChangedReceiver timeChangedReceiver;

    public DataHandler(Context context){
        this.mContext = context;
        db = AlarmDB.getInstance(context, 2);
        alarms = db.loadAllAlarms();
        intent = new Intent(mContext, SetAlarmService.class);
        intent.putExtra("typeOfOperation", INIT);
        mContext.startService(intent);
        timeChangedReceiver = new TimeChangedReceiver();
        mContext.registerReceiver(timeChangedReceiver, new IntentFilter(TIME_CHANGED));
    }

    public ArrayList<Alarm> getAllAlarm(){
        Log.d("DataHandler", "try to get All Alarms");
        return alarms;
    }

    public void addAndSetAlarm(Alarm alarm){
        db.addAlarm(alarm);
        setAlarm(mContext, intent, alarm, ALTER_ALARM);
    }

    public void updateAndSetAlarm(Alarm alarm){
        db.updateAlarm(alarm);
        setAlarm(mContext, intent, alarm, ALTER_ALARM);
    }

    public void removeAndCancelAlarm(Alarm alarm){
        db.removeAlarm(alarm);
        setAlarm(mContext, intent, alarm, DELETE_ALARM);
    }

    public void updateAndCancelAlarm(Alarm alarm) {
        db.updateAlarm(alarm);
        setAlarm(mContext, intent, alarm, DELETE_ALARM);
    }

    public void destroyAll(){
        mContext.unregisterReceiver(timeChangedReceiver);
        db = null;
    }

    private void setAlarm(Context context, Intent intent, Alarm alarm, int type){
        //当sdk版本大于19时，使用新版的设置定时语句
        intent.putExtra("alarm", alarm);
        intent.putExtra("typeOfOperation", type);
        context.startService(intent);
    }


    /**************************************************************
     * 以下是静态方法
     */

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
//    public static ArrayList<String> getAllAlarmTime(ArrayList<Alarm> alarms){
//        ArrayList<String> alarmTimes = new ArrayList<>();
//        for(Alarm alarm : alarms){
//            alarmTimes.add(getAlarmTime(alarm));
//        }
//        return alarmTimes;
//    }
//
//    public static String getAlarmTime(Alarm alarm){
//        return (alarm.getHour() + ":" + alarm.getMinute());
//    }

    public static long[] getRestOfTime(int alarmHour, int alarmMinute, String frequency){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        long curTime = cal.getTimeInMillis();

        int[] freq = AlarmDataUtil.freqStrToInt(frequency);
        cal.set(Calendar.HOUR_OF_DAY, alarmHour);
        cal.set(Calendar.MINUTE, alarmMinute);
        cal.set(Calendar.SECOND, 0);
        if(cal.getTimeInMillis() <= curTime){
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        if(!frequency.equals("一次")){
            while(!ArrayUtil.containInt(freq, cal.get(Calendar.DAY_OF_WEEK))){
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

        }
        return TimeUtil.nowToThen(cal.getTimeInMillis() - curTime);
    }

    public static String getRestOfTimeString(int alarmHour, int alarmMinute, String frequency){
        long[] restOfTime = getRestOfTime(alarmHour, alarmMinute, frequency);
        return (restOfTime[0] !=0? restOfTime[0] + "天":"") + (restOfTime[1] !=0? restOfTime[1] + "小时":"") + (restOfTime[2] !=0? restOfTime[2] + "分钟":"") + "后响铃";
    }

    public static Alarm getAlarmFromId(ArrayList<Alarm> alarms, int alarmId){
        return  ArrayUtil.getAlarmFromId(alarms, alarmId);
    }

    public static int getPositionFromId(ArrayList<Alarm> alarms, int alarmId){
        return ArrayUtil.getPositionFromId(alarms, alarmId);
    }

    public static Alarm getParcelableExtra(Intent intent){
        return AlarmDataUtil.getParcelableExtra(intent);
    }

    //    判断是否需要响闹铃
    public static boolean needToAlarm(String frequency){
        Calendar calendar = Calendar.getInstance();
        int[] freqArr = AlarmDataUtil.freqStrToInt(frequency);
        return frequency.equals("一次") || ArrayUtil.containInt(freqArr, calendar.get(Calendar.DAY_OF_WEEK));
    }

    //将时分处理成正常显示的string（即xx:xx)
    public static String getTimeString(int hour, int minute){
        return String.format(Locale.CHINA, "%1$02d:%2$02d", hour, minute);
    }
}
