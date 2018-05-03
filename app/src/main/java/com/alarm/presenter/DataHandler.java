package com.alarm.presenter;

import com.alarm.model.bean.Alarm;
import com.alarm.model.util.AlarmDataUtil;
import com.alarm.model.util.ArrayUtil;
import com.alarm.model.util.TimeUtil;


/**
 * Created by Administrator on 2018/4/26.
 */

public class DataHandler {

    public Integer[] saveAndGetRestOfTime(Alarm alarm, int alarmHour, int alarmMinute){
        alarm.setHour(alarmHour);
        alarm.setMinute(alarmMinute);
        return TimeUtil.calculateRestOfTime(alarmHour, alarmMinute);
    }

    public int getIndex(String[] stringArr, String item){
        return ArrayUtil.getIndex(stringArr, item);
    }

//    将稍后提醒的时间转化为int类型
    public int getRemindAfter(String remindAfter){
        return AlarmDataUtil.remindAfStrToInt(remindAfter);
    }

    public void saveAndSetAlarm(Alarm alarm){

    }
}
