package com.alarm.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static com.alarm.presenter.DataInit.TIMECHANGED;

/**
 * Created by Administrator on 2018/6/2.
 */

public class TimeChangedReceiver extends BroadcastReceiver {
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onReceive(Context context, Intent intent){
        Log.i("TimeChangedReceiver", "Time changed...");
        if(localBroadcastManager == null){
            localBroadcastManager = LocalBroadcastManager.getInstance(context);
        }
        Intent intent1 = new Intent(TIMECHANGED);
        localBroadcastManager.sendBroadcast(intent1);
//
//        Intent intent2 = new Intent(context, AlarmReceiverService.class);
//        intent2.putExtra("savedInstanceState", true);
//        context.startService(intent2);
    }
}
