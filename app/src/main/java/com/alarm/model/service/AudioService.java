package com.alarm.model.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import static android.media.AudioManager.FLAG_PLAY_SOUND;

/**
 * Created by Administrator on 2018/5/3.
 */

public class AudioService extends Service implements MediaPlayer.OnPreparedListener{
    MediaPlayer player;
    AudioManager am;

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
        am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 该方法在SDK2.0才开始有的，替代原来的onStart方法
     * 在onStartCommand中设置start_sticky的返回值，可以保证Service因内存不足被kill、内存可用时被重启
     */
    public int onStartCommand(Intent intent, int flags, int startId){
        Float volume = intent.getFloatExtra("volume", 0);
        if(volume == 0){
            return START_STICKY;
        }else{
            playMusic(AudioService.this, Uri.parse(intent.getStringExtra("ringtoneUri")), volume);
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy(){
        if(player.isPlaying()){
            player.stop();
        }
        player.release();
        player = null;
        Log.i("AudioService", "AudioService destroyed...");
        super.onDestroy();
    }

    //为了和Activity交互，我们需要定义一个Binder对象
    public class AudioBinder extends Binder {
        //返回Service对象
        public AudioService getService(){
            return AudioService.this;
        }
    }

    public void playMusic(Context context, Uri uri, float volume){
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        am.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume/3*2, FLAG_PLAY_SOUND);
        if(player == null){
            player = new MediaPlayer();
        }
        try{
            player.reset();
            player.setDataSource(context, uri);
            player.setScreenOnWhilePlaying(true);
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setVolume(volume, volume);
            player.setLooping(true);
            player.prepare();
        }catch (java.io.IOException e){
            Log.e("MusicPlay",  "IOException:  " + e);
        }
    }

    public void volumeAudition(Context context, Uri uri, float volume){
        if(player.isPlaying()){
            player.setVolume(volume, volume);
        }else{
            playMusic(context, uri, volume);
        }
    }

    public void stopMusic(){
        if(player.isPlaying()){
            player.stop();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp){
        mp.start();
    }
}