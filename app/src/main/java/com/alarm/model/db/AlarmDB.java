package com.alarm.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alarm.model.bean.Alarm;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/5/3.
 */

public class AlarmDB {
    private static final String DB_NAME = "alarm.db";
    private static AlarmDB alarmDB;
    private SQLiteDatabase db;

    private AlarmDB(Context context, int version){
        AlarmOpenHelper dbHelper = new AlarmOpenHelper(context, DB_NAME, null, version);
        db = dbHelper.getWritableDatabase();
    }

//    获取AlarmDB的实例
    public synchronized static AlarmDB getInstance (Context context, int version){
        if(alarmDB == null){
            alarmDB = new AlarmDB(context, version);
        }
        return alarmDB;
    }

    public void addAlarm(Alarm alarm){
        if(alarm != null){
            ContentValues values = new ContentValues();
            values.put("id", alarm.getID());
            values.put("hour", alarm.getHour());
            values.put("minute", alarm.getMinute());
            values.put("frequency", alarm.getFrequency());
            values.put("volume", alarm.getVolume());
            values.put("ringtone", alarm.getRingtone());
            values.put("ringtoneUri", alarm.getRingtoneUri());
            values.put("ringtoneType", alarm.getRingtoneType());
            values.put("remindAfter", alarm.getRemindAfter());
            values.put("description", alarm.getDescription());
            values.put("enabled", alarm.getEnabled()?1:0);
            values.put("isVerification", alarm.getIsVerification()?1:0);
            values.put("verification", alarm.getVerification());
            db.insert("Alarm", null, values);
        }
    }

    public ArrayList<Alarm> loadAllAlarms(){
        ArrayList<Alarm> list = new ArrayList<>();
        Cursor cursor = db
                .query("Alarm", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Alarm alarm = new Alarm();
                alarm.setID(cursor.getInt(cursor.getColumnIndex("id")));
                alarm.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                alarm.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                alarm.setFrequency(cursor.getString(cursor.getColumnIndex("frequency")));
                alarm.setVolume(cursor.getFloat(cursor.getColumnIndex("volume")));
                alarm.setRingtone(cursor.getString(cursor.getColumnIndex("ringtone")));
                alarm.setRingtoneUri(cursor.getString(cursor.getColumnIndex("ringtoneUri")));
                alarm.setRingtoneType(cursor.getInt(cursor.getColumnIndex("ringtoneType")));
                alarm.setRemindAfter(cursor.getInt(cursor.getColumnIndex("remindAfter")));
                alarm.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                alarm.setEnabled( (cursor.getInt(cursor.getColumnIndex("enabled"))==1) );
                alarm.setIsVerification( (cursor.getInt(cursor.getColumnIndex("isVerification"))==1) );
                alarm.setVerification(cursor.getString(cursor.getColumnIndex("verification")));
                list.add(alarm);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public Alarm removeAlarm(Alarm alarm){
        db.delete("Alarm", "id="+alarm.getID(), null);
        return alarm;
    }
//    直接使用id删除原闹钟
//    public void removeAlarm(int alarmId){
//        db.delete("Alarm", "id="+alarmId, null);
//    }

    public Alarm updateAlarm(Alarm alarm){
        ContentValues values = new ContentValues();
        values.put("hour", alarm.getHour());
        values.put("minute", alarm.getMinute());
        values.put("frequency", alarm.getFrequency());
        values.put("volume", alarm.getVolume());
        values.put("ringtone", alarm.getRingtone());
        values.put("ringtoneUri", alarm.getRingtoneUri());
        values.put("ringtoneType", alarm.getRingtoneType());
        values.put("remindAfter", alarm.getRemindAfter());
        values.put("description", alarm.getDescription());
        values.put("enabled", alarm.getEnabled()?1:0);
        values.put("isVerification", alarm.getIsVerification()?1:0);
        values.put("verification", alarm.getVerification());
        db.update("Alarm", values, "id="+alarm.getID(),null);
        return alarm;
    }
}
