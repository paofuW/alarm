package com.alarm.model.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/4/27.
 */

public class AlarmDataUtil {

//    该函数用于将String类型的frequency频率值转化为calendar的星期几表示形式的数组并返回
    public static int[] freqStrToInt(String frequency){
        switch (frequency){
            case "周一至周五":
                return new int[]{Calendar.MONDAY,Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY};
            case "每天":
                return new int[]{Calendar.MONDAY,Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
            case "周末":
                return new int[]{Calendar.SATURDAY, Calendar.SUNDAY};
            default:
                return null;
        }
    }

//    String类型的提醒时间转化为int值并返回
    public static int remindAfStrToInt(String str){
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);
        return (m.find()? Integer.parseInt(m.group()) : 0);
    }

//    获取一个由时间构成的id，为了不重复性
    public static int createID(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dFormat = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        return Integer.parseInt(dFormat.format(calendar.getTime()));
    }


}
