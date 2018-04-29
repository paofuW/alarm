package com.alarm.presenter;

import android.content.Context;

import com.alarm.model.bean.Alarm;
import com.alarm.model.util.HandleString;
import com.alarm.model.util.HandleTime;


/**
 * Created by Administrator on 2018/4/26.
 */

public class DataHandler {
    private Context mContext;

    public DataHandler(Context context){
        this.mContext = context;
    }

    public Integer[] saveAndGetRestOfTime(Alarm alarm, int alarmHour, int alarmMinute){
        alarm.setHour(alarmHour);
        alarm.setMinute(alarmMinute);
        return HandleTime.calculateRestOfTime(alarmHour, alarmMinute);
    }

    public int getIndex(String[] stringArr, String item){
        return HandleString.getIndex(stringArr, item);
    }

    public void saveFrequency(Alarm alarm, String frequency){
        alarm.setFrequency(frequency);
    }

    public void saveRemindAfter(Alarm alarm, String remindAfter){
        alarm.setRemindAfter(remindAfter);
    }
}
