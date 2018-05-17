package com.alarm.model.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Administrator on 2018/5/3.
 */

public class AudioService extends Service implements MediaPlayer.OnPreparedListener{
    MediaPlayer player;

    private final IBinder binder = new AudioBinder();
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return binder;
    }

    //实例化MediaPlayer对象
    public void onCreate(){
        super.onCreate();
        player = new MediaPlayer();
        player.setOnPreparedListener(this);
    }

    /**
     * 该方法在SDK2.0才开始有的，替代原来的onStart方法
     */
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        //super.onDestroy();
        if(player.isPlaying()){
            player.stop();
        }
        player.release();
    }

    //为了和Activity交互，我们需要定义一个Binder对象
    public class AudioBinder extends Binder {
        //返回Service对象
        public AudioService getService(){
            return AudioService.this;
        }
    }

    public void playMusic(Uri uri, float volume){
        if(player == null){
            player = new MediaPlayer();
        }
        try{
            player.reset();
            player.setDataSource(AudioService.this, uri);
            player.setVolume(volume, volume);
            player.prepare();
        }catch (java.io.IOException e){
            Log.e("MusicPlay",  "IOException:  " + e);
        }
    }

    public void stopMusic(){
        if(player.isPlaying()){
            player.stop();
        }
        player.release();
    }

    @Override
    public void onPrepared(MediaPlayer mp){
        mp.start();
    }
}