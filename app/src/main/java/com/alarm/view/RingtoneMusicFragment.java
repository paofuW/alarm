package com.alarm.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alarm.R;
import com.alarm.presenter.DataHandler;

import java.util.ArrayList;
import java.util.HashMap;

import static com.alarm.presenter.DataInit.CUSTOM_MUSIC;


/**
 * Created by Administrator on 2018/5/5.
 */

public class RingtoneMusicFragment extends Fragment{
    private HashMap<String, String> customMusic;
    private ArrayList<String> customMusicNames;
    private View rootView;

    private AlarmDetail alarmDetail;
    private int typeOfMusic;

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        alarmDetail = (AlarmDetail) getActivity();
        typeOfMusic = alarmDetail.alarm.getRingtoneType();
        if(alarmDetail.customMusic != null){
            customMusic = alarmDetail.customMusic;
            customMusicNames = DataHandler.getAllMusicName(customMusic);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(rootView == null){
            rootView = inflater.inflate(R.layout.alarm_detail_ringtone_system, container, false);
        }
        return inflater.inflate(R.layout.alarm_detail_ringtone_custom, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        while(customMusic == null || rootView == null){
            if(customMusic == null && alarmDetail.systemMusic != null){
                customMusic = alarmDetail.systemMusic;
                customMusicNames = DataHandler.getAllMusicName(customMusic);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, customMusicNames);
        listView = (ListView)rootView.findViewById(R.id.ls_alarm_ringtoneMusic);
        if(typeOfMusic == CUSTOM_MUSIC){
            listView.setSelection(customMusicNames.indexOf(alarmDetail.alarm.getRingtone()));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alarmDetail.newRingtone = customMusicNames.get(position);
                alarmDetail.newRingtoneUri = customMusic.get(customMusicNames.get(position));
                alarmDetail.newRingtoneType = CUSTOM_MUSIC;
                alarmDetail.musicServiceHandler.playRingtone(alarmDetail.newRingtoneUri, alarmDetail.alarm.getVolume());
            }
        });
        listView.setAdapter(adapter);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            alarmDetail.musicServiceHandler.stopRingtone();
        } else {
            if(typeOfMusic == CUSTOM_MUSIC){
                if(alarmDetail.newRingtone == null){
                    listView.setSelection(customMusicNames.indexOf(alarmDetail.alarm.getRingtone()));
                }else{
                    listView.setSelection(customMusicNames.indexOf(alarmDetail.newRingtone));
                }
            }
        }
    }
}
