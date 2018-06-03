package com.alarm.model.util;

import com.alarm.model.bean.Alarm;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/1.
 */

public class ArrayUtil {

    public static int getIndex(String[] stringArr, String item){
        int index = -1;
        for(int i=0; i<stringArr.length; i++){
            if (stringArr[i].equals(item)) {
                index = i;
                break;
            }
        }
        return index;
    }

//    根据id获取闹钟
    public static Alarm getAlarmFromId(ArrayList<Alarm> alarms, int alarmId){
        Alarm alarm = null;
        for(int i=0; i<alarms.size(); i++){
            if(alarms.get(i).getID() == alarmId){
                alarm = alarms.get(i);
                break;
            }
        }
        return alarm;
    }
    public static int getPositionFromId(ArrayList<Alarm> alarms, int alarmId){
        int pos = -1;
        for(int i=0; i<alarms.size(); i++){
            if(alarms.get(i).getID() == alarmId){
                pos = i;
                break;
            }
        }
        return pos;
    }

//    判断数组intArray是否包含num
    public static boolean containInt(int[] intArray, int num){
        boolean isContain = false;
        for(int i=0; i<intArray.length; i++){
            if(intArray[i] == num){
                isContain = true;
                break;
            }
        }
        return isContain;
    }

//    初始化一个连续数组
    public static ArrayList<String> initArray(Integer startNum, Integer endNum){
        ArrayList<String> arr = new ArrayList<>();
        for(int i=startNum; i<=endNum; i++){
            arr.add(Integer.toString(i));
        }
        return arr;
    }
}
