package com.alarm.model.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.R;
import com.alarm.model.bean.Alarm;
import com.alarm.model.db.AlarmDB;
import com.alarm.model.util.AlarmDataUtil;
import com.alarm.presenter.DataInit;

import static com.alarm.presenter.DataInit.INIT;

/**
 * Created by Administrator on 2018/5/28.
 */

public class AlarmReceiverService extends Service {
    private AlarmDB db;
    private Intent mIntent;
    private Alarm alarm;
    private WindowManager wm;
    private WindowManager.LayoutParams para;
    private View mView;
    private Intent musicIntent;

    private TextView tv_alert_description;
    private EditText et_alert_verification;
    private Button btn_alert_positive;
    private Button btn_alert_negative;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        para = new WindowManager.LayoutParams();
        //设置弹窗的宽高
        para.height = WindowManager.LayoutParams.WRAP_CONTENT;
        para.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //期望的位图格式。默认为不透明
        para.format = 1;
        //当FLAG_DIM_BEHIND设置后生效。该变量指示后面的窗口变暗的程度。
        //1.0表示完全不透明，0.0表示没有变暗。
        para.dimAmount = 1.0f;
        para.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        //设置为系统提示
        para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.system_alert_dialog, null);
        tv_alert_description = (TextView)mView.findViewById(R.id.tv_alert_description);
        et_alert_verification = (EditText)mView.findViewById(R.id.et_alert_verification);
        btn_alert_positive = (Button)mView.findViewById(R.id.btn_alert_positive);
        btn_alert_negative = (Button)mView.findViewById(R.id.btn_alert_negative);

        musicIntent = new Intent(AlarmReceiverService.this, AudioService.class);
        mIntent = new Intent(AlarmReceiverService.this, SetAlarmService.class);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        alarm = AlarmDataUtil.getParcelableExtra(intent);

        if(intent.getBooleanExtra("savedInstanceState", true)){
            Intent mIntent = new Intent(this, SetAlarmService.class);
            mIntent.putExtra("typeOfOperation", INIT);
            startService(intent);
        }else {
            if (intent.getBooleanExtra("listen", false)) {
                missionComplete(alarm, mIntent, intent.getBooleanExtra("listen", false));
            } else {
                if (AlarmDataUtil.needToAlarm(alarm.getFrequency())) {
                    //volume为AudioService是否播放闹铃的判断依据
                    musicIntent.putExtra("ringtoneUri", alarm.getRingtoneUri());
                    musicIntent.putExtra("volume", alarm.getVolume());
                    startService(musicIntent);

                    //定时关闭弹窗，时间为8分钟
                    mIntent.putExtra("typeOfOperation", DataInit.ALTER_ALARM);
                    mIntent.putExtra("listen", true);
                    mIntent.putExtra("alarm", alarm);
                    startService(mIntent);

                    String description = alarm.getDescription().equals("无") ? "闹钟来了" : alarm.getDescription();
                    if (alarm.getIsVerification()) {
                        et_alert_verification.setVisibility(View.VISIBLE);
                        tv_alert_description.setText(description);
                        btn_alert_positive.setText("对吗");
                        btn_alert_positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (et_alert_verification.getText().toString().equals(alarm.getVerification())) {
                                    missionComplete(alarm, mIntent, false);
                                } else {
                                    Toast.makeText(AlarmReceiverService.this, "验证错误，请重新输入", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        btn_alert_negative.setText("听歌");
                        btn_alert_negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btn_alert_negative.setVisibility(View.GONE);
                            }
                        });
                        wm.addView(mView, para);
                    } else {
                        tv_alert_description.setText(description);
                        et_alert_verification.setVisibility(View.GONE);
                        btn_alert_negative.setVisibility(View.GONE);
                        btn_alert_positive.setText("好的");
                        btn_alert_positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                missionComplete(alarm, mIntent, false);
                            }
                        });
                        wm.addView(mView, para);
                    }
                } else {
                    mIntent.putExtra("typeOfOperation", DataInit.ALTER_ALARM);
                    mIntent.putExtra("listen", false);
                    mIntent.putExtra("alarm", alarm);
                    startService(mIntent);
                }
            }
        }


//        if(AlarmDataUtil.isRemindAfter(alarm.getRemindAfter())){
//            tv_alert_description.setText("稍后提醒");
//            tv_alert_message.setText(alarm.getDescription());
//            btn_alert_positive.setText("那行");
//            btn_alert_positive.setOnClickListener(listener);
//            btn_alert_negative.setText("不要");
//            btn_alert_negative.setOnClickListener(listener);
//            wm.addView(mView, para);
//        }else{
//            tv_alert_description.setText("闹钟来了");
//            tv_alert_message.setText(alarm.getDescription());
//            btn_alert_positive.setVisibility(View.GONE);
//            btn_alert_negative.setText("好的");
//            btn_alert_negative.setOnClickListener(listener);
//            wm.addView(mView, para);
//        }

        return super.onStartCommand(intent,flags,startId);
    }

    private void missionComplete(Alarm alarm, Intent intent, boolean isCancel){
        stopService(musicIntent);
        wm.removeView(mView);
        if(AlarmDataUtil.isRepeat(alarm.getFrequency())){
            intent.putExtra("typeOfOperation", DataInit.ALTER_ALARM);
            intent.putExtra("listen", false);
            intent.putExtra("alarm", alarm);
            startService(intent);
        }else{
            if(!isCancel){
                intent.putExtra("typeOfOperation", DataInit.DELETE_ALARM);
                intent.putExtra("listen", false);
                intent.putExtra("alarm", alarm);
                startService(intent);
            }
            db = AlarmDB.getInstance(AlarmReceiverService.this, 2);
            alarm.setEnabled(false);
            db.updateAlarm(alarm);
            db = null;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    /**
     * 更新原来id的闹钟
     * @param context   用于开启服务的上下文环境
     * @param intent    用于通信，传输设置闹钟所需要的相关信息
     * @param remindAfterTimes  该参数提示“稍后提醒”的次数，即当前闹钟为第几次稍后提醒的闹钟，用于设置闹钟响起时间
     * @param alarm   更新的闹钟的id（即alarm的ID）
     */
//    private void setAndStartService(Context context, Intent intent, int remindAfterTimes, Alarm alarm){
//        intent.putExtra("alarm", alarm);
//        intent.putExtra("remindAfterTimes", remindAfterTimes);
//        context.startService(intent);
//    }
}
