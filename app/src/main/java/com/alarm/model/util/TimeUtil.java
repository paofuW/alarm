package com.alarm.model.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2018/4/26.
 */

public class TimeUtil {
    private static Integer[] restOfTime = new Integer[]{};

//    计算距离闹钟响起的时间，该函数只是计算下一个设置的时间点距离现在的时间，不考虑频率（即这个点的闹钟是否需要）
    public static Integer[] calculateRestOfTime(int alarmHour, int alarmMinute){
        Calendar cal = Calendar.getInstance();
        int curHour = cal.get(Calendar.HOUR_OF_DAY);
        int curMinute = cal.get(Calendar.MINUTE);
        boolean borrow = false;
        if(alarmMinute < curMinute){
            restOfTime[1] = alarmMinute + 60 - curMinute;
            borrow = true;
        }else{
            restOfTime[1] = alarmMinute - curMinute;
        }
        if(alarmHour < curHour) {
            restOfTime[0] = alarmHour + 24 - curHour;
        }else{
            restOfTime[0] = alarmHour - curHour;
        }
        if(borrow){
            restOfTime[0]--;
        }
        return restOfTime;
    }

//    获取设置的闹钟时间的毫秒数，返回long类型
    public static long getAlarmTimeMillis(int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        if(hour<calendar.get(Calendar.HOUR_OF_DAY) || (hour == calendar.get(Calendar.HOUR_OF_DAY) && minute <= calendar.get(Calendar.HOUR_OF_DAY))){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }
}
