package com.alarm.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.alarm.presenter.DataInit.ALTER_ALARM;
import static com.alarm.presenter.DataInit.CANCEL;
import static com.alarm.presenter.DataInit.CREATE_ALARM;
import static com.alarm.presenter.DataInit.CUSTOM_MUSIC;
import static com.alarm.presenter.DataInit.MAIN_FRAGMENT;
import static com.alarm.presenter.DataInit.SYSTEM_MUSIC;


/**
 * Created by Administrator on 2018/5/7.
 */

public class AlarmDetail extends AppCompatActivity {
    public String mainTitle;
    private Context context;
    Intent returnIntent;

    public static HashMap<String, String> systemMusic;
    public static HashMap<String, String> customMusic;
    public Integer typeOfOperation;
    public Alarm alarm;
    public String newRingtone;
    public String newRingtoneUri;
    public int newRingtoneType;
    public float volume;
    public MusicServiceHandler musicServiceHandler;

    private View actionBarView;
    private ActionBar.LayoutParams lp;
    private ImageButton img_detail_actionBarCancel;
    private ImageButton img_detail_actionBarSave;
    private TextView tv_detail_actionBarTitle;
    private ActionBar actionBar;

    public FragmentManager fragmentManager;
    public List<Fragment> fragments;
    public ViewPager vp_detail;
    public FragmentAdapter fragmentAdapter;
    public AlarmDetailFragment alarmDetailFragment;
    public RingtoneMainFragment ringtoneMainFragment;
    public RingtoneMusicFragment ringtoneMusicFragment;
    //position用于给返回键判断当前所处界面，用于模仿BackStack的行为
    public int position;

    public static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    public final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message mes){
            super.handleMessage(mes);
            Log.i("AlarmDetail", "接收到线程传回的音乐列表 : " + (mes.what == SYSTEM_MUSIC? "SYSTEM_MUSIC" : "CUSTOM_MUSIC"));
            switch(mes.what){
                case SYSTEM_MUSIC:
                    systemMusic = (HashMap<String, String>) mes.obj;
                    if(ringtoneMainFragment.created){
                        ringtoneMainFragment.initListView(systemMusic);
                    }
                    break;
                case CUSTOM_MUSIC:
                    customMusic = (HashMap<String, String>) mes.obj;
                    if(ringtoneMusicFragment.created){
                        ringtoneMusicFragment.initListView(customMusic);
                    }
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

        context = this;
        initTitle(getIntent());
        alarm = getAlarm(getIntent());
        newRingtone = alarm.getRingtone();
        newRingtoneType = alarm.getRingtoneType();
        newRingtoneUri = alarm.getRingtoneUri();
        volume = alarm.getVolume();

        initFragmentList();
        fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, fragments);
        vp_detail = (ViewPager)findViewById(R.id.vp_detail);
        initViewPager();

        musicServiceHandler = new MusicServiceHandler(context);
        musicServiceHandler.startPlayService();
        requestPower();

        returnIntent = new Intent();
    }

    //获取初始化的闹钟信息
    private Alarm getAlarm(Intent lastIntent) {
        typeOfOperation = lastIntent.getIntExtra("typeOfOperation", -1);
        switch (typeOfOperation){
            case ALTER_ALARM:
                return lastIntent.getParcelableExtra("alarm");
            case CREATE_ALARM:
                return DataInit.getDefaultAlarm(context);
            default:
                return DataInit.getDefaultAlarm(context);
        }
    }

    private void initTitle(Intent lastIntent) {
        typeOfOperation = lastIntent.getIntExtra("typeOfOperation", -1);
        switch (typeOfOperation){
            case ALTER_ALARM:
                mainTitle = "修改闹钟";
                break;
            case CREATE_ALARM:
            default:
                mainTitle = "新建闹钟";
                break;
        }
        setCustomActionBar(mainTitle);
    }

    private void initFragmentList(){
        fragments = new ArrayList<Fragment>();
        alarmDetailFragment = new AlarmDetailFragment();
        ringtoneMainFragment = new RingtoneMainFragment();
        ringtoneMusicFragment = new RingtoneMusicFragment();
        fragments.add(alarmDetailFragment);
        fragments.add(ringtoneMainFragment);
        fragments.add(ringtoneMusicFragment);
    }

    //初始化ViewPager组件
    private void initViewPager(){
        vp_detail.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // This space for rent
            }

            @Override
            public void onPageSelected(int position) {
                // This space for rent
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // This space for rent
            }
        });
        vp_detail.setAdapter(fragmentAdapter);
        vp_detail.setCurrentItem(0);
        position = MAIN_FRAGMENT;
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
                retreat();
            }
        });
        img_detail_actionBarSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicServiceHandler.stopRingtone();
                switch(position){
                    case CUSTOM_MUSIC:
                    case SYSTEM_MUSIC:
                        position = MAIN_FRAGMENT;
                        changeActionBarTitle(mainTitle);
                        vp_detail.setCurrentItem(0);
                        alarm.setRingtone(newRingtone);
                        alarm.setRingtoneUri(newRingtoneUri);
                        alarm.setRingtoneType(newRingtoneType);
                        ((TextView)(alarmDetailFragment.getView().findViewById(R.id.tv_detail_ringtone))).setText(newRingtone);
                        break;
                    case MAIN_FRAGMENT:
                        returnIntent.putExtra("alarm", alarm);
                        ((AlarmDetail)context).setResult(typeOfOperation, returnIntent);
                        ((AlarmDetail)context).finish();
                        break;
                }
            }
        });
        actionBar = getSupportActionBar();
        try{
            actionBar.setCustomView(actionBarView, lp);
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
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
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed(){
        retreat();
    }

    @Override
    public void onDestroy(){
        Log.i("AlarmDetail", "AlarmDetail destroyed...");
        musicServiceHandler.stopPlayService();
        super.onDestroy();
    }

    private void retreat(){
        musicServiceHandler.stopRingtone();
        switch(position){
            case CUSTOM_MUSIC:
                position = SYSTEM_MUSIC;
                vp_detail.setCurrentItem(1);
                break;
            case SYSTEM_MUSIC:
                position = MAIN_FRAGMENT;
                vp_detail.setCurrentItem(0);
                newRingtone = alarm.getRingtone();
                newRingtoneType = alarm.getRingtoneType();
                newRingtoneUri = alarm.getRingtoneUri();
                break;
            case MAIN_FRAGMENT:
                ((AlarmDetail)context).setResult(CANCEL, returnIntent);
                ((AlarmDetail)context).finish();
                break;
        }
    }

    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,  Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
                Toast.makeText(this, "该app需要读取权限，读取系统音乐", Toast.LENGTH_LONG);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
            }
        }
        DataInit.getAllSystemMusic(this, singleThreadExecutor, handler);
        DataInit.getAllCustomMusic(this, singleThreadExecutor, handler);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    DataInit.getAllSystemMusic(this, singleThreadExecutor, handler);
                    DataInit.getAllCustomMusic(this, singleThreadExecutor, handler);
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
