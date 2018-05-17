package com.alarm.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alarm.model.bean.Alarm;
import com.alarm.model.util.AlarmDataUtil;
import com.alarm.model.util.ArrayUtil;
import com.alarm.model.util.AudioUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/4/27.
 */

public class DataInit {
    /**
     * 所有的常量都定义在此
     */
    public final static int CANCEL = 0;
    public final static int CREATE_ALARM = 1;
    public final static int ALTER_ALARM = 2;
    public final static int DELETE_ALARM = 3;

    public final static int MAIN_FRAGMENT = 4;
    public final static int SYSTEM_MUSIC = 5;
    public final static int CUSTOM_MUSIC  = 6;

    private static String[] frequencyChoices = new String[]{"仅一次","周一至周五","每天","周末"};
    private static String[] remindAfterChoices = new String[]{"关闭","5分钟","10分钟","15分钟","30分钟"};

    public static ArrayList<String> getHourArr(Integer startNum, Integer endNum){
        return ArrayUtil.initArray(startNum, endNum);
    }

    public static ArrayList<String> getMinuteArr(Integer startNum, Integer endNum){
        return ArrayUtil.initArray(startNum, endNum);
    }

    public static Alarm getDefaultAlarm(Context context){
        String[] defaultRingtone = AudioUtil.getDefaultRingtone(context);
        Alarm alarm = new Alarm();
        alarm.setID(AlarmDataUtil.createID());
        alarm.setHour(6);
        alarm.setMinute(0);
        alarm.setFrequency("一次");
        alarm.setVolume(0.8f);
        alarm.setRingtone((defaultRingtone[0] != null)? defaultRingtone[0] : "无");
        alarm.setRingtoneUri((defaultRingtone[1] != null)? defaultRingtone[1] : null);
        alarm.setRingtoneType(SYSTEM_MUSIC);
        alarm.setRemindAfter(0);
        alarm.setDescription("无");
        return alarm;
    }

    public static String[] getFrequencyChoices(){
        return frequencyChoices;
    }

    public static String[] getRemindAfterChoices(){
        return  remindAfterChoices;
    }

    //该函数启动两个线程获取相应的音乐
    public static void getAllMusic(final Context context,final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> systemMusic = AudioUtil.getAllSystemMusic(context);
                Message message = Message.obtain();
                message.what = SYSTEM_MUSIC;
                message.obj = systemMusic;
                handler.sendMessage(message);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> customMusic = AudioUtil.getAllCustomMusic(context);
                Message message = Message.obtain();
                message.what = CUSTOM_MUSIC;
                message.obj = customMusic;
                handler.sendMessage(message);
            }
        });
    }
//
//    public static HashMap<String, String> getAllSystemMusic(Context context){
//
//        return AudioUtil.getAllSystemMusic(context);
//    }
//
//    public static HashMap<String, String> getAllCustomMusic(Context context){
//        return AudioUtil.getAllCustomMusic(context);
//    }
}
