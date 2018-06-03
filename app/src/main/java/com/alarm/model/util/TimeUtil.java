package com.alarm.model.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/26.
 */

public class TimeUtil {

//    计算距离闹钟响起的时间，该函数只是计算下一个设置的时间点距离现在的时间，不考虑频率（即这个点的闹钟是否需要）
    public static long[] nowToThen(Calendar then){
        long[] restOfTime = new long[3];
        Calendar cal = Calendar.getInstance();
        long curTime = cal.getTimeInMillis();
        long diff = then.getTimeInMillis() - curTime;
        restOfTime[0] = diff / (1000 * 60 * 60 * 24);
        restOfTime[1] = (diff-restOfTime[0]*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
        restOfTime[2] = (diff-restOfTime[0]*(1000 * 60 * 60 * 24)-restOfTime[1]*(1000* 60 * 60))/(1000* 60);
        return restOfTime;
    }

    public static long[] nowToThen(long diffTimeInMills){
        long[] restOfTime = new long[3];
        restOfTime[0] = diffTimeInMills / (1000 * 60 * 60 * 24);
        restOfTime[1] = (diffTimeInMills-restOfTime[0]*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
        restOfTime[2] = (diffTimeInMills-restOfTime[0]*(1000 * 60 * 60 * 24)-restOfTime[1]*(1000* 60 * 60))/(1000* 60);
        return restOfTime;

    }

//    获取设置的闹钟时间的毫秒数，返回long类型
    public static long getAlarmTimeMillis(int hour, int minute, boolean add){
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, add?8:0);
        //用于判断是否需要跨天
        if(currentTime >= calendar.getTimeInMillis()){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
        Log.i("SetAlarmService", "设置闹钟服务：闹钟时间"+simpleDateFormat.format(calendar.getTimeInMillis()));
        return calendar.getTimeInMillis();
    }
}
