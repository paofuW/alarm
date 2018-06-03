package com.alarm.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alarm.model.service.SetAlarmService;

import static com.alarm.presenter.DataInit.INIT;

/**
 * Created by Administrator on 2018/5/31.
 */

public class StartUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Intent mIntent = new Intent(context, SetAlarmService.class);
        mIntent.putExtra("typeOfOperation", INIT);
        context.startService(intent);
    }
}
