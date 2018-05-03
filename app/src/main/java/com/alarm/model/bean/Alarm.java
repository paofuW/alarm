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
    private int volume;
    private String ringtone;
    private Boolean vibrate;
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
    public int getVolume(){
        return volume;
    }
    public void setVolume(int volume){
        this.volume = volume;
    }

    //获取或设置铃声
    public String getRingtone(){
        return ringtone;
    }
    public void setRingtone(String ringtone){
        this.ringtone = ringtone;
    }

    //获取或设置震动
    public Boolean getVibrate(){
        return vibrate;
    }
    public void setVibrate(Boolean vibrate){
        this.vibrate = vibrate;
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
        parcel.writeInt(volume);
        parcel.writeString(ringtone);
        parcel.writeByte((byte)(vibrate?1:0));
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
            alarm.setVolume(source.readInt());
            alarm.setRingtone(source.readString());
            alarm.setVibrate((source.readByte()!=0));
            alarm.setRemindAfter(source.readInt());
            alarm.setDescription(source.readString());
            return alarm;
        }
    };

}
