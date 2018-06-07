package com.alarm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.R;
import com.alarm.model.bean.Alarm;
import com.alarm.model.service.AudioService;
import com.alarm.model.service.SetAlarmService;
import com.alarm.model.util.AlarmDataUtil;
import com.alarm.presenter.DataHandler;
import com.alarm.presenter.DataInit;

import static com.alarm.presenter.DataInit.INIT;
import static com.alarm.presenter.DataInit.UPDATE_ALARMS;

/**
 * Created by Administrator on 2018/6/4.
 */

public class AlarmAlert extends Activity {
    PowerManager powerManager;
    Window window;

    private Intent alarmIntent;
    private Intent musicIntent;
    private Intent receiveIntent;
    private Alarm alarm;
    private Context context;

    private TextView tv_alert_description;
    private EditText et_alert_advanced_verification;
    private Button btn_alert_advanced_positive;
    private Button btn_alert_advanced_negative;
    private TextView tv_alert_normal_time;
    private Button btn_alert_normal_sleepMore;
    private LinearLayout ll_alert_normal_whole;

    private float curX;
    private float preX;
    private float  threshold;
    private Point point;
    private boolean isCompleted = false;

    public final static int TIME_TO_CLOSE = 9;
    public final static int SILENT_30_SECONDS = 10;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message mes){
            super.handleMessage(mes);
            switch(mes.what){
                case SILENT_30_SECONDS:
                    if(!isCompleted){
                        musicIntent.putExtra("ringtoneUri", alarm.getRingtoneUri());
                        musicIntent.putExtra("volume", alarm.getVolume());
                        startService(musicIntent);
                    }
                    break;
                case TIME_TO_CLOSE:
                    if(!isCompleted){
                        missionComplete(alarm, alarmIntent);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        context = AlarmAlert.this;
        window = getWindow();
        receiveIntent = getIntent();
        alarmIntent = new Intent(context, SetAlarmService.class);
        musicIntent = new Intent(context, AudioService.class);
        alarm = AlarmDataUtil.getParcelableExtra(receiveIntent);

        /**
         * 此处说明alarmIntent中的listen参数，该参数是为了设置闹钟持续时间
         */

        if(receiveIntent.getBooleanExtra("savedInstanceState", true)){
            //判断该receiveIntent是否是一个目的为保活service的intent
            Intent mIntent = new Intent(this, SetAlarmService.class);
            mIntent.putExtra("typeOfOperation", INIT);
            startService(alarmIntent);
        }else if(!DataHandler.needToAlarm(alarm.getFrequency())) {
            //判断当天是否需要进行闹钟服务（此处为不需要，说明当天不在设置的需要闹钟提示的频率内）
            alarmIntent.putExtra("typeOfOperation", DataInit.ALTER_ALARM);
            alarmIntent.putExtra("alarm", alarm);
            startService(alarmIntent);
        }else {
            powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

            //判断是否能进行交互（即屏幕亮暗）
            if (!powerManager.isInteractive()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
            }

            if(alarm.getIsVerification()){
                setContentView(R.layout.alarm_alert_advanced);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                tv_alert_description = (TextView) findViewById(R.id.tv_alert_advanced_description);
                et_alert_advanced_verification = (EditText) findViewById(R.id.et_alert_advanced_verification);
                btn_alert_advanced_positive = (Button) findViewById(R.id.btn_alert_advanced_positive);
                btn_alert_advanced_negative = (Button) findViewById(R.id.btn_alert_advanced_negative);

                //volume为AudioService是否播放闹铃的判断依据
                musicIntent.putExtra("ringtoneUri", alarm.getRingtoneUri());
                musicIntent.putExtra("volume", alarm.getVolume());
                startService(musicIntent);
                //定时关闭弹窗，时间为8分钟
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(TIME_TO_CLOSE);
                    }
                }, 10*60*1000);

                et_alert_advanced_verification.setVisibility(View.VISIBLE);
                tv_alert_description.setText(alarm.getDescription());
                btn_alert_advanced_positive.setText("静音30秒");
                btn_alert_advanced_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TypedValue outValue = new TypedValue();
                        getResources().getValue(R.dimen.turnOnAlpha, outValue, true);
                        btn_alert_advanced_positive.setAlpha(outValue.getFloat());
                        btn_alert_advanced_positive.setClickable(false);

                        stopService(musicIntent);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(SILENT_30_SECONDS);
                            }
                        }, 30000);
                    }
                });
                btn_alert_advanced_negative.setText("关闭闹钟");
                btn_alert_advanced_negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (et_alert_advanced_verification.getText().toString().equals(alarm.getVerification())) {
                            missionComplete(alarm, alarmIntent);
                        } else {
                            Toast.makeText(context, "验证错误，请重新输入", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                setContentView(R.layout.alarm_alert_normal);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                WindowManager manager = this.getWindowManager();
                DisplayMetrics outMetrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(outMetrics);
                threshold = (float)outMetrics.widthPixels/2;

                musicIntent.putExtra("ringtoneUri", alarm.getRingtoneUri());
                musicIntent.putExtra("volume", alarm.getVolume());
                startService(musicIntent);

                tv_alert_normal_time = (TextView)findViewById(R.id.tv_alert_normal_time);
                tv_alert_description = (TextView)findViewById(R.id.tv_alert_normal_description);
                btn_alert_normal_sleepMore = (Button)findViewById(R.id.btn_alert_normal_sleepMore);
                ll_alert_normal_whole = (LinearLayout)findViewById(R.id.ll_alert_normal_whole);

                tv_alert_normal_time.setText(DataHandler.getTimeString(alarm.getHour(), alarm.getMinute()));
                tv_alert_description.setText(alarm.getDescription());
                btn_alert_normal_sleepMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmIntent.putExtra("typeOfOperation", DataInit.REMIND_LATER);
                        alarmIntent.putExtra("alarm", alarm);
                        startService(alarmIntent);
                        stopService(musicIntent);
                        AlarmAlert.this.finish();
                    }
                });
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curX = event.getX();
                return true;

            case MotionEvent.ACTION_MOVE:
                preX = curX;
                curX = event.getX();
                moveLayout(curX - preX);
                return true;

            case MotionEvent.ACTION_UP:
                if(!isCompleted){
                    float curLeft = ll_alert_normal_whole.getX();
                    float dis = curLeft / 50;
                    do{
                        curLeft -= dis;
                        ll_alert_normal_whole.setX(curLeft);
                    }while(Math.abs(curLeft) > 3);
                    ll_alert_normal_whole.setX(0);
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void moveLayout(float distance){
        float curLeft = ll_alert_normal_whole.getX() + distance;
        ll_alert_normal_whole.setX(curLeft);
        if(Math.abs(curLeft) >= threshold){
            missionComplete(alarm, alarmIntent);
        }
    }

    private void missionComplete(Alarm alarm, Intent intent){
        isCompleted = true;
        stopService(musicIntent);
        if(AlarmDataUtil.isRepeat(alarm.getFrequency())){
            intent.putExtra("typeOfOperation", DataInit.ALTER_ALARM);
            intent.putExtra("alarm", alarm);
            startService(intent);
        }else{
            alarm.setEnabled(false);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            Intent intent1 = new Intent(UPDATE_ALARMS);
            intent1.putExtra("alarm", alarm);
            localBroadcastManager.sendBroadcast(intent1);
        }
        AlarmAlert.this.finish();
    }

    @Override
    public void onBackPressed(){
    }
}
