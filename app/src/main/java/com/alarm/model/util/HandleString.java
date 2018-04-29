package com.alarm.model.util;

/**
 * Created by Administrator on 2018/4/27.
 */

public class HandleString {
    private static int index;

    public static int getIndex(String[] stringArr, String item){
        index = -1;
        for(int i=0; i<stringArr.length; i++){
            if (stringArr[i].equals(item)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
