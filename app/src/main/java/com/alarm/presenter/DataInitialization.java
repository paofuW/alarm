package com.alarm.presenter;

import com.alarm.model.bean.Alarm;
import com.alarm.model.init.InitData;

/**
 * Created by Administrator on 2018/4/27.
 */

public class DataInitialization {

    public static Integer[] getHourArr(Integer startNum, Integer endNum){
        return InitData.initArray(startNum, endNum);
    }

    public static Integer[] getMinuteArr(Integer startNum, Integer endNum){
        return InitData.initArray(startNum, endNum);
    }

    public static Alarm getDefaultAlarm(){
        return InitData.initAlarm();
    }
}
