package com.alarm.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;

import com.alarm.model.service.AudioService;

/**
 * Created by Administrator on 2018/5/8.
 */

public class MusicServiceHandler {
    private Context mContext;
    private AudioService audioService;
    private Intent intent;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audioService = ((AudioService.AudioBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioService = null;
        }
    };

    public MusicServiceHandler(Context context){
        this.mContext = context;
        intent = new Intent(context, AudioService.class);
    }

    public void startPlayService(){
        mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void stopPlayService(){
        mContext.unbindService(conn);
        conn = null;
    }

    public void playRingtone(String ringtoneUri, float volume){
        Uri uri = Uri.parse(ringtoneUri);
        audioService.playMusic(mContext, uri, volume);
    }

    public void volumeAudition(String ringtoneUri, float volume){
        Uri uri = Uri.parse(ringtoneUri);
        audioService.volumeAudition(mContext, uri, volume);
    }

    public void stopRingtone(){
        audioService.stopMusic();
    }
}
