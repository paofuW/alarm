package com.alarm.view;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alarm.R;
import com.alarm.model.bean.Alarm;
import com.alarm.presenter.DataHandler;
import com.alarm.presenter.DataInit;

import java.util.ArrayList;


/**
 * Created by Administrator on 2018/4/25.
 */

public class AlarmDetailFragment extends Fragment{
    private ArrayList<String> hourArr;
    private ArrayList<String> minuteArr;
    private String[] frequencyChoices;
    private String[] remindAfterChoices;
    private Integer restOfHour;
    private Integer restOfMinute;
    private Integer typeOfOperation;
    Intent returnIntent;
    private View rootView;
    public View.OnClickListener listener;
    private Alarm alarm;

    private WheelView wv_hour;
    private WheelView wv_minute;
    private TextView tv_restOfTime;
    private TextView tv_frequency;
    private TextView tv_ringtone;
    private SeekBar skBar_volume;
    private TextView tv_remindLater;
    private TextView tv_description;
    private Button btn_delete;
    private AlarmDetail alarmDetail;

    private final static int CREATE_ALARM = 1;
    private final static int ALTER_ALARM = 2;
    private final static int DELETE_ALARM = 3;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        hourArr = DataInit.getHourArr(0,23);
        minuteArr = DataInit.getMinuteArr(0,59);
        frequencyChoices = DataInit.getFrequencyChoices();
        remindAfterChoices = DataInit.getRemindAfterChoices();
        listener = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int index;
                Log.i("MainFragment", "Click view ID: " + view.getId());
                switch(view.getId()){
                    case R.id.rl_detail_setFrequency:
                        index = DataHandler.getIndex(frequencyChoices, tv_frequency.getText().toString());
                        new AlertDialog.Builder(getActivity()).setTitle("重复")
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
                        alarmDetail.changeActionBarTitle("铃声");
                        if(alarmDetail.ringtoneMainFragment == null){
                            alarmDetail.ringtoneMainFragment = new RingtoneMainFragment();
                            alarmDetail.fragmentTransaction.add(R.id.fl_detail, alarmDetail.ringtoneMainFragment).addToBackStack("first").commit();
                        }else{
                            alarmDetail.fragmentTransaction.hide(alarmDetail.alarmDetailFragment).show(alarmDetail.ringtoneMainFragment);
                            alarmDetail.fragmentTransaction.addToBackStack("first").commit();
                        }

                        break;
                    case R.id.rl_detail_setRemind:
                        index = DataHandler.getIndex(remindAfterChoices, tv_remindLater.getText().toString());
                        new AlertDialog.Builder(getActivity()).setTitle("稍后提醒")
                                .setSingleChoiceItems(remindAfterChoices, index, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        alarm.setRemindAfter(DataHandler.getRemindAfter(remindAfterChoices[which]));
                                        tv_remindLater.setText(remindAfterChoices[which]);
                                        dialog.dismiss();
                                    }
                                }).create().show();
                        break;
                    case R.id.rl_detail_setDescription:
                        final EditText et_description = new EditText(getActivity());
                        et_description.setText(alarm.getDescription());
                        new AlertDialog.Builder(getActivity()).setTitle("描述")
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
                    case R.id.btn_detail_delete:
                        returnIntent.putExtra("alarm", alarm);
                        getActivity().setResult(DELETE_ALARM, returnIntent);
                        break;
                }
            }
        };
        returnIntent = new Intent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(rootView == null){
            rootView = inflater.inflate(R.layout.alarm_detail_main, container, false);
        }
        getAllView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        alarmDetail = (AlarmDetail)getActivity();
        alarm = alarmDetail.alarm;
        typeOfOperation = alarmDetail.typeOfOperation;
        initAllView();
    }

    private void getAllView(View view){
        wv_hour = (WheelView)view.findViewById(R.id.wv_detail_hour);
        wv_minute = (WheelView)view.findViewById(R.id.wv_detail_minute);
        tv_restOfTime = (TextView)view.findViewById(R.id.tv_detail_restOfTime);
        tv_frequency = (TextView)view.findViewById(R.id.tv_detail_frequency);
        tv_ringtone = (TextView)view.findViewById(R.id.tv_detail_ringtone);
        skBar_volume = (SeekBar)view.findViewById(R.id.skBar_detail_volume);
        tv_remindLater = (TextView)view.findViewById(R.id.tv_detail_remindLater);
        tv_description = (TextView)view.findViewById(R.id.tv_detail_description);
        btn_delete = (Button)view.findViewById(R.id.btn_detail_delete);

        view.findViewById(R.id.rl_detail_setFrequency).setOnClickListener(listener);
        view.findViewById(R.id.rl_detail_setRingtone).setOnClickListener(listener);
        view.findViewById(R.id.rl_detail_setRemind).setOnClickListener(listener);
        view.findViewById(R.id.rl_detail_setDescription).setOnClickListener(listener);
    }

    private void initAllView(){

        int alarmHour = alarm.getHour();
        int alarmMinute = alarm.getMinute();
        initWheelView(alarmHour, alarmMinute);
        updateRestOfTime(alarmHour, alarmMinute);
        hasBtnDelete(typeOfOperation);
        tv_frequency.setText(alarm.getFrequency());
        tv_ringtone.setText(alarm.getRingtone());
        skBar_volume.setProgress((int)(alarm.getVolume()*100));
        tv_remindLater.setText(String.format(getString(R.string.minute), alarm.getRemindAfter()));
        tv_description.setText(alarm.getDescription());

        skBar_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alarm.setVolume(((float)progress)/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btn_delete.setOnClickListener(listener);
    }

    private void initWheelView(int alarmHour, int alarmMinute){
        wv_hour.setItems( hourArr);
        wv_minute.setItems(minuteArr);

        wv_hour.setSeletion(alarmHour);
        wv_minute.setSeletion(alarmMinute);

        wv_hour.setOffset(3);
        wv_minute.setOffset(3);

        wv_hour.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int index, String item){
                alarm.setHour(index);
                updateRestOfTime(index, alarm.getMinute());
            }
        });
        wv_minute.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int index, String item){
                alarm.setMinute(index);
                updateRestOfTime(alarm.getHour(), index);
            }
        });
    }

    private void updateRestOfTime(int alarmHour, int alarmMinute){
        Integer[] restOfTime = DataHandler.getRestOfTime(alarmHour, alarmMinute);
        restOfHour = restOfTime[0];
        restOfMinute = restOfTime[1];

        tv_restOfTime.setText((restOfHour==0? restOfHour+"小时":"") + (restOfMinute==0? restOfMinute+"分钟":"") + "后响铃");
    }

    private void hasBtnDelete(int typeOfOperation) {
        switch (typeOfOperation){
            case ALTER_ALARM:
                break;
            case CREATE_ALARM:
                btn_delete.setVisibility(View.GONE);
                break;
            default:
                btn_delete.setVisibility(View.GONE);
                break;
        }
    }
}
