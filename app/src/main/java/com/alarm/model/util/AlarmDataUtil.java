package com.alarm.model.util;

import android.content.Intent;

import com.alarm.model.bean.Alarm;

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

//    判断一个闹钟是否重复(区分一次性闹钟与重复性闹钟)
    public static boolean isRepeat(String frequency){
        return !frequency.equals("一次");
    }


//    判断是否需要稍后提醒
    public static boolean isRemindAfter(int remindAfter){
        return (remindAfter != 0);
    }

//    在无法使用putParcelableExtra(putExtra)的情况下的代替方法
    public static Intent putParcelableExtra(Intent intent, Alarm alarm){
        intent.putExtra("ID", alarm.getID());
        intent.putExtra("hour",alarm.getHour());
        intent.putExtra("minute",alarm.getMinute());
        intent.putExtra("frequency",alarm.getFrequency());
        intent.putExtra("volume",alarm.getVolume());
        intent.putExtra("ringtone",alarm.getRingtone());
        intent.putExtra("ringtoneUri",alarm.getRingtoneUri());
        intent.putExtra("ringtoneType",alarm.getRingtoneType());
        intent.putExtra("remindAfter",alarm.getRemindAfter());
        intent.putExtra("description",alarm.getDescription());
        intent.putExtra("enabled",alarm.getEnabled());
        intent.putExtra("isVerification", alarm.getIsVerification());
        intent.putExtra("verification", alarm.getVerification());
        return intent;
    }

//     在无法使用getParcelableExtra的情况下的代替方法
    public static Alarm getParcelableExtra(Intent intent){
        Alarm alarm = new Alarm();
        alarm.setID(intent.getIntExtra("ID", 0));
        alarm.setHour(intent.getIntExtra("hour", 0));
        alarm.setMinute(intent.getIntExtra("minute", 0));
        alarm.setFrequency(intent.getStringExtra("frequency"));
        alarm.setVolume(intent.getFloatExtra("volume", 0));
        alarm.setRingtone(intent.getStringExtra("ringtone"));
        alarm.setRingtoneUri(intent.getStringExtra("ringtoneUri"));
        alarm.setRingtoneType(intent.getIntExtra("ringtoneType", 0));
        alarm.setRemindAfter(intent.getIntExtra("remindAfter", 0));
        alarm.setDescription(intent.getStringExtra("description"));
        alarm.setEnabled(intent.getBooleanExtra("enabled", true));
        alarm.setIsVerification(intent.getBooleanExtra("isVerification", false));
        alarm.setVerification(intent.getStringExtra("verification"));
        return alarm;
    }
}
