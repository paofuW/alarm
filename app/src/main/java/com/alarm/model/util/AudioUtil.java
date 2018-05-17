package com.alarm.model.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/5/3.
 */

public class AudioUtil {

    //    获取所有的铃声，存储在HashMap中， 保存其铃声名和URI
    public static HashMap<String, String> getAllSystemMusic(Context context){
        HashMap<String, String> hashMap = new HashMap<>();
        RingtoneManager rm = new RingtoneManager(context);
        Ringtone ringtone;
        int i = 0;
        rm.setType(RingtoneManager.TYPE_ALARM);
        rm.getCursor();
        while(true) {
            try{
                ringtone = rm.getRingtone(i);
                hashMap.put(ringtone.getTitle(context), rm.getRingtoneUri(i).toString());
                i++;
                continue;
            }catch(Exception e){
            }
            break;
        }
        return hashMap;
    }


    //    获取所有的音乐，存储在HashMap中， 保存其音乐名和URI
    public static HashMap<String, String> getAllCustomMusic(Context context){
        HashMap<String, String> hashMap = new HashMap<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.MIME_TYPE + "=? or " + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(1) != null){
                    hashMap.put(cursor.getString(0), cursor.getString(1));
                }
            }while(cursor.moveToNext());
            cursor.close();
        }
        return hashMap;
    }
//    public static HashMap<String, String> getAllCustomMusic(Context context){
//        HashMap<String, String> hashMap = new HashMap<>();
//        RingtoneManager rm = new RingtoneManager(context);
//        Ringtone ringtone;
//        int i = 0;
//        rm.setType(RingtoneManager.TYPE_RINGTONE);
//        rm.getCursor();
//        while(true) {
//            try{
//                ringtone = rm.getRingtone(i);
//                hashMap.put(ringtone.getTitle(context), rm.getRingtoneUri(i).toString());
//                i++;
//                continue;
//            }catch(Exception e){
//            }
//            break;
//        }
//        return hashMap;
//    }

    //获取第一个系统铃声
    public static String[] getDefaultRingtone(Context context){
        String[] defaultRingtone = new String[2];
        Ringtone ringtone;
        RingtoneManager rm = new RingtoneManager(context);
        rm.setType(RingtoneManager.TYPE_RINGTONE);
        rm.getCursor();
        if((ringtone = rm.getRingtone(0)) != null) {
            defaultRingtone[0] = ringtone.getTitle(context);
            defaultRingtone[1] = rm.getRingtoneUri(0).toString();
            return defaultRingtone;
        }else{
            Log.w("DataInit",  "Fail to get Ringtone");
            return null;
        }
    }
}
