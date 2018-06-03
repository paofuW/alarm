package com.alarm.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alarm.R;
import com.alarm.presenter.DataHandler;

import java.util.ArrayList;
import java.util.HashMap;

import static com.alarm.presenter.DataInit.CUSTOM_MUSIC;
import static com.alarm.presenter.DataInit.SYSTEM_MUSIC;

/**
 * Created by Administrator on 2018/5/5.
 */

public class RingtoneMainFragment extends Fragment {
    private HashMap<String, String> systemMusic;
    private ArrayList<String> systemMusicNames;
    private View rootView;

    private AlarmDetail alarmDetail;
    private ArrayAdapter<String> adapter;
    public boolean created = false;

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmDetail = (AlarmDetail) getActivity();
        created = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.alarm_detail_ringtone_system, container, false);
        }
        if (AlarmDetail.systemMusic != null) {
            initListView(AlarmDetail.systemMusic);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("RingtoneMainFragment", "onActivityCreated ");
        RelativeLayout rl_ringtone_music = (RelativeLayout) alarmDetail.findViewById(R.id.rl_ringtone_music);
        rl_ringtone_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmDetail.musicServiceHandler.stopRingtone();
                alarmDetail.changeActionBarTitle(getString(R.string.ringtone_music));
                alarmDetail.position = CUSTOM_MUSIC;
                alarmDetail.vp_detail.setCurrentItem(2);
            }
        });

    }

    public void initListView(HashMap<String, String> sMusic) {
        if(systemMusic == null){
            systemMusic = sMusic;
            systemMusicNames = DataHandler.getAllMusicName(systemMusic);
        }
        adapter = new ArrayAdapter<>(alarmDetail, android.R.layout.simple_list_item_1, systemMusicNames);
        listView = (ListView) rootView.findViewById(R.id.ls_alarm_ringtoneSystem);
        if (alarmDetail.newRingtoneType == SYSTEM_MUSIC) {
            listView.setSelection(systemMusicNames.indexOf(alarmDetail.newRingtone));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                alarmDetail.newRingtone = systemMusicNames.get(position);
                alarmDetail.newRingtoneUri = systemMusic.get(systemMusicNames.get(position));
                alarmDetail.newRingtoneType = SYSTEM_MUSIC;
                alarmDetail.musicServiceHandler.playRingtone(alarmDetail.newRingtoneUri, alarmDetail.volume);
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("SystemMusic", "onResume");
//        if(firstShow) {
//            firstShow = false;
//        }else{
//            typeOfMusic = alarmDetail.newRingtoneType;
//            if(typeOfMusic == SYSTEM_MUSIC){
//                listView.setSelection(systemMusicNames.indexOf(alarmDetail.alarm.getRingtone()));
//            }
//            listView.setAdapter(adapter);
//        }
    }
}
