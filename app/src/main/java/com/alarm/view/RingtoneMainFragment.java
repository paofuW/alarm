package com.alarm.view;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import static com.alarm.presenter.DataInit.SYSTEM_MUSIC;

/**
 * Created by Administrator on 2018/5/5.
 */

public class RingtoneMainFragment extends Fragment {
    private HashMap<String, String> systemMusic;
    private ArrayList<String> systemMusicNames;
    private View rootView;

    private AlarmDetail alarmDetail;
    private int typeOfMusic;

    private ListView listView;
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message mes){
            super.handleMessage(mes);
            if(mes.what == SYSTEM_MUSIC){
                systemMusic = (HashMap<String, String>) mes.obj;
                systemMusicNames = DataHandler.getAllMusicName(systemMusic);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, systemMusicNames);
                listView = (ListView)rootView.findViewById(R.id.ls_alarm_ringtoneSystem);
                if(typeOfMusic == SYSTEM_MUSIC){
                    if(alarmDetail.newRingtone == null){
                        listView.setSelection(systemMusicNames.indexOf(alarmDetail.alarm.getRingtone()));
                    }else{
                        listView.setSelection(systemMusicNames.indexOf(alarmDetail.newRingtone));
                    }
                }
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        alarmDetail.newRingtone = systemMusicNames.get(position);
                        alarmDetail.newRingtoneUri = systemMusic.get(systemMusicNames.get(position));
                        alarmDetail.newRingtoneType = SYSTEM_MUSIC;
                        alarmDetail.musicServiceHandler.playRingtone(alarmDetail.newRingtoneUri, alarmDetail.alarm.getVolume());
                    }
                });
                listView.setAdapter(adapter);
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        ringingMap = DataInit.getAllSystemMusic(getActivity());
//        ringingNames = DataHandler.getAllRingingName(ringingMap);
        alarmDetail = (AlarmDetail) getActivity();
        typeOfMusic = alarmDetail.alarm.getRingtoneType();
        if(alarmDetail.systemMusic != null){
            systemMusic = alarmDetail.systemMusic;
            systemMusicNames = DataHandler.getAllMusicName(systemMusic);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(rootView == null){
            rootView = inflater.inflate(R.layout.alarm_detail_ringtone_system, container, false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RelativeLayout rl_ringtone_music = (RelativeLayout) alarmDetail.findViewById(R.id.rl_ringtone_music);
        rl_ringtone_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alarmDetail.ringtoneMusicFragment == null){
                    alarmDetail.ringtoneMusicFragment = new RingtoneMusicFragment();
                    alarmDetail.fragmentTransaction.add(R.id.fl_detail, alarmDetail.ringtoneMusicFragment).addToBackStack(null).commit();
                }else{
                    alarmDetail.fragmentTransaction.hide(alarmDetail.ringtoneMainFragment).show(alarmDetail.ringtoneMusicFragment);
                    alarmDetail.fragmentTransaction.addToBackStack(null).commit();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            alarmDetail.musicServiceHandler.stopRingtone();
        } else {
            if(typeOfMusic == SYSTEM_MUSIC){
                if(alarmDetail.newRingtone == null){
                    listView.setSelection(systemMusicNames.indexOf(alarmDetail.alarm.getRingtone()));
                }else{
                    listView.setSelection(systemMusicNames.indexOf(alarmDetail.newRingtone));
                }
            }
        }
    }
}
