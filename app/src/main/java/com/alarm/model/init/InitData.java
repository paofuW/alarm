package com.alarm.model.init;

import com.alarm.model.bean.Alarm;
import com.alarm.model.util.AlarmDataUtil;

/**
 * Created by Administrator on 2018/4/26.
 */

public class InitData {
    public static Integer[] initArray(Integer startNum, Integer endNum){
        Integer[] arr = new Integer[]{};
        for(int i=startNum; i<=endNum; i++){
            arr[i] = i;
        }
        return arr;
    }

    public static Alarm initAlarm(){
        Alarm alarm = new Alarm();
        alarm.setID(AlarmDataUtil.createID());
        alarm.setHour(6);
        alarm.setMinute(0);
        alarm.setFrequency("一次");
        alarm.setVolume(80);
        alarm.setRingtone("默认");
        alarm.setVibrate(false);
        alarm.setRemindAfter(0);
        alarm.setDescription("无");
        return alarm;
    }
}
