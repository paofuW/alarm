package com.alarm.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/5/3.
 */

public class AlarmOpenHelper extends SQLiteOpenHelper {

    private final static String CREATE_ALARM =  "create table alarm("
            +"id integer primary key,"
            +"hour integer,"
            +"minute integer,"
            +"frequency text,"
            +"volume float,"
            +"ringtone text,"
            +"ringtoneUri text,"
            +"ringtoneType integer,"
            +"remindAfter integer,"
            +"description text,"
            +"enabled integer,"
            +"isVerification integer,"
            +"verification text)";

    public AlarmOpenHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version){
        super(context, dbName, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        switch (oldVersion) {
            case 1:
                db.execSQL("alter table alarm add column isVerification integer");
                db.execSQL("alter table alarm add column verification text");
            default:
        }
    }
}
