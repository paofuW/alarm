package com.alarm.model.util;

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

    //初始化一个连续数组
    public static ArrayList<String> initArray(Integer startNum, Integer endNum){
        ArrayList<String> arr = new ArrayList<>();
        for(int i=startNum; i<=endNum; i++){
            arr.add(Integer.toString(i));
        }
        return arr;
    }
}
