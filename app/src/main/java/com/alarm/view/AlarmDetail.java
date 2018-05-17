package com.alarm.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.R;
import com.alarm.model.bean.Alarm;
import com.alarm.presenter.DataInit;
import com.alarm.presenter.MusicServiceHandler;

import java.util.HashMap;

import static com.alarm.presenter.DataInit.ALTER_ALARM;
import static com.alarm.presenter.DataInit.CANCEL;
import static com.alarm.presenter.DataInit.CREATE_ALARM;
import static com.alarm.presenter.DataInit.CUSTOM_MUSIC;
import static com.alarm.presenter.DataInit.SYSTEM_MUSIC;


/**
 * Created by Administrator on 2018/5/7.
 */

public class AlarmDetail extends AppCompatActivity {
    private String mainTitle;
    Intent returnIntent;

    public HashMap<String, String> systemMusic;
    public HashMap<String, String> customMusic;
    public Integer typeOfOperation;
    public Alarm alarm;
    public String newRingtone;
    public String newRingtoneUri;
    public int newRingtoneType;
    public MusicServiceHandler musicServiceHandler;

    private View actionBarView;
    private ActionBar.LayoutParams lp;
    private ImageButton img_detail_actionBarCancel;
    private ImageButton img_detail_actionBarSave;
    private TextView tv_detail_actionBarTitle;
    private ActionBar actionBar;

    public AlarmDetailFragment alarmDetailFragment;
    public RingtoneMainFragment ringtoneMainFragment;
    public RingtoneMusicFragment ringtoneMusicFragment;
    public FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message mes){
            super.handleMessage(mes);
            switch(mes.what){
                case SYSTEM_MUSIC:
                    systemMusic = (HashMap<String, String>) mes.obj;
                    break;
                case CUSTOM_MUSIC:
                    customMusic = (HashMap<String, String>) mes.obj;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_detail);

        initTitle(getIntent());
        alarm = getAlarm(getIntent());
        initFragment();
        musicServiceHandler = new MusicServiceHandler(AlarmDetail.this);

        returnIntent = new Intent();
        DataInit.getAllMusic(this, handler);
    }

    //获取初始化的闹钟信息
    private Alarm getAlarm(Intent lastIntent) {
        typeOfOperation = lastIntent.getIntExtra("typeOfOperation", -1);
        switch (typeOfOperation){
            case ALTER_ALARM:
                return lastIntent.getParcelableExtra("alarm");
            case CREATE_ALARM:
                return DataInit.getDefaultAlarm(AlarmDetail.this);
            default:
                return DataInit.getDefaultAlarm(AlarmDetail.this);
        }
    }

    private void initTitle(Intent lastIntent) {
        typeOfOperation = lastIntent.getIntExtra("typeOfOperation", -1);
        switch (typeOfOperation){
            case ALTER_ALARM:
                mainTitle = "修改闹钟";
                break;
            case CREATE_ALARM:
                mainTitle = "新建闹钟";
                break;
            default:
                mainTitle = "新建闹钟";
                break;
        }
        setCustomActionBar(mainTitle);
    }

    private void initFragment(){
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.removeOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                //通过判断返回栈的总数来判断具体跳转到的页面
                switch (fragmentManager.getBackStackEntryCount()+4+1) {
                    case SYSTEM_MUSIC:
                        changeActionBarTitle(mainTitle);
                        break;
                    case CUSTOM_MUSIC:
                        changeActionBarTitle("铃声");
                        break;
                    default:
                        clearBackStack(fragmentManager);
                        changeActionBarTitle(mainTitle);
                        break;
                }
            }
        });
        alarmDetailFragment = (AlarmDetailFragment)fragmentManager.findFragmentById(R.id.fg_detail_main);
    }

    //初始化导航栏，并给按钮添加点击事件
    private void setCustomActionBar(String title) {
        if(lp == null){
            lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        }
        if(actionBarView == null){
            actionBarView = LayoutInflater.from(this).inflate(R.layout.alarm_detail_actionbar, null);
        }
        tv_detail_actionBarTitle = (TextView)actionBarView.findViewById(R.id.tv_detail_actionBarTitle);
        img_detail_actionBarCancel = (ImageButton)actionBarView.findViewById(R.id.img_detail_actionBarCancel);
        img_detail_actionBarSave = (ImageButton)actionBarView.findViewById(R.id.img_detail_actionBarSave);
        tv_detail_actionBarTitle.setText(title);
        img_detail_actionBarCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentManager.getBackStackEntryCount() == 0){
                    AlarmDetail.this.setResult(CANCEL, returnIntent);
                    AlarmDetail.this.finish();
                }else{
                    musicServiceHandler.stopRingtone();
                    fragmentManager.popBackStack();
                }
            }
        });
        img_detail_actionBarSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentManager.getBackStackEntryCount() == 0){
                    returnIntent.putExtra("alarm", alarm);
                    AlarmDetail.this.setResult(typeOfOperation, returnIntent);
                    AlarmDetail.this.finish();
                }else{
                    musicServiceHandler.stopRingtone();
                    if(newRingtoneUri != null && !(newRingtoneUri.equals(alarm.getRingtoneUri()))){
                        alarm.setRingtone(newRingtone);
                        alarm.setRingtoneUri(newRingtoneUri);
                        alarm.setRingtoneType(newRingtoneType);
                    }
                    ((TextView)(alarmDetailFragment.getView().findViewById(R.id.tv_detail_ringtone))).setText(alarm.getRingtone());
                    clearBackStack(fragmentManager);
                }
            }
        });
        actionBar = getSupportActionBar();
        try{
            actionBar.setCustomView(actionBarView, lp);
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(AlarmDetail.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }finally {
            Toast.makeText(AlarmDetail.this, "finally", Toast.LENGTH_LONG).show();
        }
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM|ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    //改变导航栏的标题
    public void changeActionBarTitle(String title){
        tv_detail_actionBarTitle.setText(title);
        if(actionBarView == null){
            actionBarView = LayoutInflater.from(this).inflate(R.layout.alarm_detail_actionbar, null);
        }
        if(lp == null){
            lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        }
        try{
            actionBar.setCustomView(actionBarView, lp);
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(AlarmDetail.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //清空返回栈
    private void clearBackStack(FragmentManager fm){
        fm.popBackStack("first", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
