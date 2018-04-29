package com.alarm.model.util;

import java.util.Calendar;

/**
 * Created by Administrator on 2018/4/26.
 */

public class HandleTime {
    private static Integer[] restOfTime = new Integer[]{};

    public static Integer[] calculateRestOfTime(int alarmHour, int alarmMinute){
        Calendar cal = Calendar.getInstance();
        int curHour = cal.get(Calendar.HOUR);
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
}
