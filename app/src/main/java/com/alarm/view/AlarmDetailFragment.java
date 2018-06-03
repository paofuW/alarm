package com.alarm.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alarm.R;
import com.alarm.model.bean.Alarm;
import com.alarm.presenter.DataHandler;
import com.alarm.presenter.DataInit;

import static com.alarm.presenter.DataInit.ALTER_ALARM;
import static com.alarm.presenter.DataInit.CREATE_ALARM;
import static com.alarm.presenter.DataInit.DELETE_ALARM;
import static com.alarm.presenter.DataInit.SYSTEM_MUSIC;

/**
 * Created by Administrator on 2018/4/25.
 */

public class AlarmDetailFragment extends Fragment{
    private String[] frequencyChoices;
    private String[] remindAfterChoices;
    private Integer typeOfOperation;
    private View rootView;
    public View.OnClickListener listener;
    private Alarm alarm;

    private TimePicker tp_detail;
    private TextView tv_frequency;
    private TextView tv_ringtone;
    private SeekBar skBar_volume;
//    private TextView tv_remindLater;
    private TextView tv_description;
    private Switch swt_advancedRemind;
    private TextView tv_verification;
    private RelativeLayout rl_setVerification;
    private Button btn_delete;
    private AlarmDetail alarmDetail;
    private EditText editText;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        frequencyChoices = DataInit.getFrequencyChoices();
        remindAfterChoices = DataInit.getRemindAfterChoices();

        listener = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int index;
                switch(view.getId()){
                    case R.id.rl_detail_setFrequency:
                        editText = new EditText(getContext());
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
                        alarmDetail.musicServiceHandler.stopRingtone();
                        alarmDetail.changeActionBarTitle(getString(R.string.ringtone_system));
                        alarmDetail.position = SYSTEM_MUSIC;
                        alarmDetail.vp_detail.setCurrentItem(1);
                        break;
//                    case R.id.rl_detail_setRemind:
//                        index = DataHandler.getIndex(remindAfterChoices, tv_remindLater.getText().toString());
//                        new AlertDialog.Builder(getActivity()).setTitle("稍后提醒")
//                                .setSingleChoiceItems(remindAfterChoices, index, new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which){
//                                        alarm.setRemindAfter(DataHandler.getRemindAfter(remindAfterChoices[which]));
//                                        tv_remindLater.setText(remindAfterChoices[which]);
//                                        dialog.dismiss();
//                                    }
//                                }).create().show();
//                        break;
                    case R.id.rl_detail_setDescription:
                        editText = new EditText(getContext());
                        editText.setText(alarm.getDescription());
                        new AlertDialog.Builder(getActivity()).setTitle("描述")
                                .setView(editText)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        String inputDescription = editText.getText().toString();
                                        if( inputDescription.equals("")){
                                            alarm.setDescription("闹钟");
                                            tv_description.setText("闹钟");
                                        }else{
                                            alarm.setDescription(inputDescription);
                                            tv_description.setText(inputDescription);
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
                    case R.id.rl_detail_setVerification:
                        editText = new EditText(getContext());
                        editText.setText(alarm.getVerification());
                        new AlertDialog.Builder(getActivity()).setTitle("文字输入")
                                .setView(editText)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        String verification = editText.getText().toString();
                                        if(verification.equals("")){
                                            alarm.setVerification("好的");
                                            tv_verification.setText("好的");
                                        }else{
                                            alarm.setVerification(verification);
                                            tv_verification.setText(verification);
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
                        alarmDetail.musicServiceHandler.stopPlayService();
                        alarmDetail.returnIntent.putExtra("alarm", alarm);
                        alarmDetail.setResult(DELETE_ALARM, alarmDetail.returnIntent);
                        alarmDetail.finish();
                        break;
                }
            }
        };
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
        Log.i("AlarmDetailFragment", "onActivityCreated");
        alarmDetail = (AlarmDetail)getActivity();
        alarm = alarmDetail.alarm;
        typeOfOperation = alarmDetail.typeOfOperation;
        initAllView(rootView);
    }

    private void getAllView(View view){
        tp_detail = (TimePicker) view.findViewById(R.id.tp_detail);
        tv_frequency = (TextView)view.findViewById(R.id.tv_detail_frequency);
        tv_ringtone = (TextView)view.findViewById(R.id.tv_detail_ringtone);
        skBar_volume = (SeekBar)view.findViewById(R.id.skBar_detail_volume);
//        tv_remindLater = (TextView)view.findViewById(R.id.tv_detail_remindLater);
        tv_description = (TextView)view.findViewById(R.id.tv_detail_description);
        swt_advancedRemind = (Switch)view.findViewById(R.id.swt_detail_advancedRemind);
        tv_verification = (TextView)view.findViewById(R.id.tv_detail_verification);
        rl_setVerification = (RelativeLayout)view.findViewById(R.id.rl_detail_setVerification);
        btn_delete = (Button)view.findViewById(R.id.btn_detail_delete);

    }

    private void initAllView(View view){
        final int alarmHour = alarm.getHour();
        int alarmMinute = alarm.getMinute();
        initDatePicker(alarmHour, alarmMinute);
        hasBtnDelete(typeOfOperation);
        tv_frequency.setText(alarm.getFrequency());
        tv_ringtone.setText(alarm.getRingtone());
        skBar_volume.setProgress((int)(alarm.getVolume()*100));
        tv_description.setText(alarm.getDescription());
//        if(alarm.getRemindAfter() != 0){
//            tv_remindLater.setText(String.format(getString(R.string.minute), alarm.getRemindAfter()));
//        }else{
//            tv_remindLater.setText("关闭");
//        }
        if(alarm.getIsVerification()){
            swt_advancedRemind.setChecked(true);
            rl_setVerification.setVisibility(View.VISIBLE);
            tv_verification.setText(alarm.getVerification());
        }else{
            swt_advancedRemind.setChecked(false);
            rl_setVerification.setVisibility(View.GONE);
        }

        swt_advancedRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setIsVerification(isChecked);
                if(isChecked){
                    rl_setVerification.setVisibility(View.VISIBLE);
                    tv_verification.setText(alarm.getVerification());
                }else{
                    rl_setVerification.setVisibility(View.GONE);
                }
            }
        });

        skBar_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar.getProgress() < 1){
                    seekBar.setProgress(1);
                }
                alarmDetail.volume = ((float)seekBar.getProgress())/100;
                alarm.setVolume(alarmDetail.volume);
                alarmDetail.musicServiceHandler.volumeAudition(alarmDetail.newRingtoneUri,alarmDetail.volume);
            }
        });
        btn_delete.setOnClickListener(listener);

        view.findViewById(R.id.rl_detail_setFrequency).setOnClickListener(listener);
        view.findViewById(R.id.rl_detail_setRingtone).setOnClickListener(listener);
//        view.findViewById(R.id.rl_detail_setRemind).setOnClickListener(listener);
        view.findViewById(R.id.rl_detail_setDescription).setOnClickListener(listener);
        rl_setVerification.setOnClickListener(listener);
    }

    private void initDatePicker(int alarmHour, int alarmMinute){
        tp_detail.setHour(alarmHour);
        tp_detail.setMinute(alarmMinute);
        tp_detail.setIs24HourView(true);
//        tp_detail.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        tp_detail.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                alarm.setHour(hourOfDay);
                alarm.setMinute(minute);
            }
        });
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
