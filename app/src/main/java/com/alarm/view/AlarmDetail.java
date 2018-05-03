package com.alarm.view;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.alarm.R;
import com.alarm.model.bean.Alarm;
import com.alarm.presenter.DataHandler;
import com.alarm.presenter.DataInitialization;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;


/**
 * Created by Administrator on 2018/4/25.
 */

public class AlarmDetail extends AppCompatActivity implements View.OnClickListener,OnWheelChangedListener {
    private Integer[] hourArr;
    private Integer[] minuteArr;
    private Integer restOfHour;
    private Integer restOfMinute;
    private Integer typeOfOperation;
    Intent returnIntent;

    private Alarm alarm;
    private DataHandler dataHandler;

    private WheelView wv_hour;
    private WheelView wv_minute;
    private TextView tv_restOfTime;
    private TextView tv_frequency;
    private TextView tv_ringtone;
    private SeekBar skBar_volume;
    private Switch swt_vibrate;
    private TextView tv_remindLater;
    private TextView tv_description;
    private Button btn_delete;

    private final static int CANCEL = 0;
    private final static int CREATE_ALARM = 1;
    private final static int ALTER_ALARM = 2;
    private final static int DELETE_ALARM = 3;
    private String[] frequencyChoices = new String[]{"仅一次","周一至周五","每天","周末"};
    private String[] remindAfterChoices = new String[]{"关闭","5分钟","10分钟","15分钟","30分钟"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_detail);
        alarm = getAlarm(getIntent());

        hourArr = DataInitialization.getHourArr(0,23);
        minuteArr = DataInitialization.getMinuteArr(0,59);

        dataHandler = new DataHandler();
        getAndInitAllView();

        returnIntent = new Intent();
    }

    @Override
    public void onClick(View view){
        int index;
        switch(view.getId()){
            case R.id.rl_detail_setFrequency:
                index = dataHandler.getIndex(frequencyChoices, tv_frequency.getText().toString());
                new AlertDialog.Builder(AlarmDetail.this).setTitle("重复")
                        .setSingleChoiceItems(frequencyChoices, index, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                alarm.setFrequency(frequencyChoices[which]);
                                tv_frequency.setText(frequencyChoices[which]);
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            case R.id.rl_detail_setRingtone:
                break;
            case R.id.rl_detail_setRemind:
                index = dataHandler.getIndex(remindAfterChoices, tv_remindLater.getText().toString());
                new AlertDialog.Builder(AlarmDetail.this).setTitle("稍后提醒")
                        .setSingleChoiceItems(remindAfterChoices, index, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                alarm.setRemindAfter(dataHandler.getRemindAfter(remindAfterChoices[which]));
                                tv_remindLater.setText(remindAfterChoices[which]);
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            case R.id.rl_detail_setDescription:
                final EditText et_description = new EditText(this);
                et_description.setText(alarm.getDescription());
                new AlertDialog.Builder(AlarmDetail.this).setTitle("描述")
                        .setView(et_description)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                String inputDescription = et_description.getText().toString();
                                if( inputDescription.equals("")){
                                    alarm.setDescription("无");
                                }else{
                                    alarm.setDescription(inputDescription);
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create().show();
                break;
            case R.id.btn_actionBar_save:
                returnIntent.putExtra("alarm", alarm);
                AlarmDetail.this.setResult(typeOfOperation, returnIntent);
                break;
            case R.id.btn_actionBar_cancel:
                AlarmDetail.this.setResult(CANCEL, returnIntent);
                break;
            case R.id.btn_detail_delete:
                returnIntent.putExtra("alarm", alarm);
                AlarmDetail.this.setResult(DELETE_ALARM, returnIntent);
                break;
        }
    }

    @Override
    public void onChanged(WheelView wheelView, int oldValue, int newValue) {
        updateRestOfTime(hourArr[wv_hour.getCurrentItem()], minuteArr[wv_minute.getCurrentItem()]);
    }

    private void getAndInitAllView(){
        wv_hour = (WheelView)findViewById(R.id.wv_detail_hour);
        wv_minute = (WheelView)findViewById(R.id.wv_detail_minute);
        tv_restOfTime = (TextView)findViewById(R.id.tv_detail_restOfTime);
        tv_frequency = (TextView)findViewById(R.id.tv_detail_frequency);
        tv_ringtone = (TextView)findViewById(R.id.tv_detail_ringtone);
        skBar_volume = (SeekBar)findViewById(R.id.skBar_detail_volume);
        swt_vibrate = (Switch)findViewById(R.id.swt_detail_vibrate);
        tv_remindLater = (TextView)findViewById(R.id.tv_detail_remindLater);
        tv_description = (TextView)findViewById(R.id.tv_detail_description);
        btn_delete = (Button)findViewById(R.id.btn_detail_delete);

        int alarmHour = alarm.getHour();
        int alarmMinute = alarm.getMinute();
        initWheelView(alarmHour, alarmMinute);
        updateRestOfTime(alarmHour, alarmMinute);
        tv_frequency.setText(alarm.getFrequency());
        tv_ringtone.setText(alarm.getRingtone());
        skBar_volume.setProgress(alarm.getVolume());
        swt_vibrate.setChecked(alarm.getVibrate());
        tv_remindLater.setText(alarm.getRemindAfter());
        tv_description.setText(alarm.getDescription());

        swt_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    alarm.setVibrate(true);
                }else{
                    alarm.setVibrate(false);
                }
            }
        });
        skBar_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alarm.setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btn_delete.setOnClickListener(this);
    }

    private void initWheelView(int alarmHour, int alarmMinute){
        wv_hour.setViewAdapter(new ArrayWheelAdapter<>(AlarmDetail.this, hourArr));
        wv_minute.setViewAdapter(new ArrayWheelAdapter<>(AlarmDetail.this, minuteArr));

        wv_hour.setCurrentItem(alarmHour);
        wv_minute.setCurrentItem(alarmMinute);

        wv_hour.setVisibleItems(7);
        wv_minute.setVisibleItems(7);

        wv_hour.setCyclic(true);
        wv_minute.setCyclic(true);

        wv_hour.addChangingListener(this);
        wv_minute.addChangingListener(this);
    }

    private void updateRestOfTime(int alarmHour, int alarmMinute){
        Integer[] restOfTime = dataHandler.saveAndGetRestOfTime(alarm, alarmHour, alarmMinute);
        restOfHour = restOfTime[0];
        restOfMinute = restOfTime[1];

        tv_restOfTime.setText((restOfHour==0? restOfHour+"小时":"") + (restOfMinute==0? restOfMinute+"分钟":"") + "后响铃");
    }

    private Alarm getAlarm(Intent lastIntent) {
        typeOfOperation = lastIntent.getIntExtra("typeOfOperation", -1);
        switch (typeOfOperation){
            case ALTER_ALARM:
                setCustomActionBar("修改闹钟");
                return lastIntent.getParcelableExtra("pickedAlarm");
            case CREATE_ALARM:
                setCustomActionBar("新建闹钟");
                btn_delete.setVisibility(View.GONE);
                return DataInitialization.getDefaultAlarm();
            default:
                setCustomActionBar("新建闹钟");
                btn_delete.setVisibility(View.GONE);
                return DataInitialization.getDefaultAlarm();
        }
    }

    private void setCustomActionBar(String title) {
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View actionBarView = LayoutInflater.from(this).inflate(R.layout.alarm_detail_actionbar, null);
        ((TextView)actionBarView.findViewById(R.id.tv_actionBar_title)).setText(title);
        (actionBarView.findViewById(R.id.btn_actionBar_cancel)).setOnClickListener(this);
        (actionBarView.findViewById(R.id.btn_actionBar_save)).setOnClickListener(this);

        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(actionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }
}
