package com.alarm.view;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
    private ArrayAdapter<String> adapter;

    private AlarmDetail alarmDetail;
    public boolean created = false;

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i("CustomMusic", "onCreate...");
        alarmDetail = (AlarmDetail) getActivity();
        created = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.i("CustomMusic", "onCreateView...");
        if(rootView == null){
            rootView = inflater.inflate(R.layout.alarm_detail_ringtone_custom, container, false);
        }
        if(AlarmDetail.customMusic != null){
            initListView(AlarmDetail.customMusic);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void initListView(HashMap<String, String> cMusic){
        if(customMusic == null){
            customMusic = cMusic;
            customMusicNames = DataHandler.getAllMusicName(customMusic);
        }
        adapter = new ArrayAdapter<>(alarmDetail, android.R.layout.simple_list_item_1, customMusicNames);
        listView = (ListView)rootView.findViewById(R.id.ls_alarm_ringtoneMusic);
        if(alarmDetail.newRingtoneType == CUSTOM_MUSIC){
            listView.setSelection(customMusicNames.indexOf(alarmDetail.newRingtone));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alarmDetail.newRingtone = customMusicNames.get(position);
                alarmDetail.newRingtoneUri = customMusic.get(customMusicNames.get(position));
                alarmDetail.newRingtoneType = CUSTOM_MUSIC;
                alarmDetail.musicServiceHandler.playRingtone(alarmDetail.newRingtoneUri, alarmDetail.volume);
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
