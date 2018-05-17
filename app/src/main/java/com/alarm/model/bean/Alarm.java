package com.alarm.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/4/23.
 */

public class Alarm implements Parcelable{
    private int ID;
    private int hour;
    private int minute;
    private String frequency;
    private Float volume;
    private String ringtone;
    private String ringtoneUri;
    private int ringtoneType;
    private int remindAfter;
    private String description;

    //获取和设置该闹钟的id（用于设置pending的requestCode以识别不同的闹钟
    public int getID(){
        return ID;
    }
    public void setID(int id){
        this.ID = id;
    }

    //获取或设置小时
    public int getHour() {
        return hour;
    }
    public void setHour(int hour){
        this.hour = hour;
    }

    //获取或设置分钟
    public int getMinute(){
        return minute;
    }
    public void setMinute(int minute){
        this.minute = minute;
    }

    //获取或设置频率（重复）
    public String getFrequency(){
        return frequency;
    }
    public void setFrequency(String frequency){
        this.frequency = frequency;
    }

    //获取或设置音量
    public Float getVolume(){
        return volume;
    }
    public void setVolume(Float volume){
        this.volume = volume;
    }

    //获取或设置铃声
    public String getRingtone(){
        return ringtone;
    }
    public void setRingtone(String ringtone){
        this.ringtone = ringtone;
    }

    //获取或设置铃声的uri
    public String getRingtoneUri(){
        return  ringtoneUri;
    }
    public void setRingtoneUri(String ringtoneUri){
        this.ringtoneUri = ringtoneUri;
    }

    //获取或设置铃声的类型（系统铃声或本地铃声）
    public int getRingtoneType(){
        return  ringtoneType;
    }
    public void setRingtoneType(int ringtoneType){
        this.ringtoneType = ringtoneType;
    }

    //获取或设置提醒时间
    public int getRemindAfter(){
        return remindAfter;
    }
    public void setRemindAfter(int remindAfter){
        this.remindAfter = remindAfter;
    }

    //获取或设置描述
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    //give some attention to the oder between  writeToParcel and createFromParcel
    @Override
    public void writeToParcel(Parcel parcel, int flags){
        parcel.writeInt(ID);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
        parcel.writeString(frequency);
        parcel.writeFloat(volume);
        parcel.writeString(ringtone);
        parcel.writeString(ringtoneUri);
        parcel.writeInt(ringtoneType);
        parcel.writeInt(remindAfter);
        parcel.writeString(description);
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Creator<Alarm>() {

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }

        @Override
        public Alarm createFromParcel(Parcel source) {
            Alarm alarm = new Alarm();
            alarm.setID(source.readInt());
            alarm.setHour(source.readInt());
            alarm.setMinute(source.readInt());
            alarm.setFrequency(source.readString());
            alarm.setVolume(source.readFloat());
            alarm.setRingtone(source.readString());
            alarm.setRingtoneUri(source.readString());
            alarm.setRingtoneType(source.readInt());
            alarm.setRemindAfter(source.readInt());
            alarm.setDescription(source.readString());
            return alarm;
        }
    };

}
