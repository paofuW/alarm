package com.alarm.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alarm.R;
import com.alarm.model.bean.Alarm;
import com.alarm.presenter.DataHandler;

import java.util.ArrayList;

import static com.alarm.presenter.DataInit.ALTER_ALARM;
import static com.alarm.presenter.DataInit.CREATE_ALARM;
import static com.alarm.presenter.DataInit.DELETE_ALARM;


/**
 * Created by Administrator on 2018/4/23.
 */

public class MainActivity extends AppCompatActivity{
    private Intent intent;
    private ArrayList<Alarm> alarms;
    private ArrayList<String> alarmTimes;
    private DataHandler dataHandler;
    private Alarm alarm;
    private ArrayAdapter<String> adapter;

    private int pos;

    private ListView lv_main_showAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTitle("闹钟");
        setContentView(R.layout.alarm_main);
        dataHandler = new DataHandler(MainActivity.this);
        alarms = dataHandler.getAllAlarm();
        alarmTimes = DataHandler.getAllAlarmTime(alarms);
        intent = new Intent(MainActivity.this, AlarmDetail.class);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alarmTimes);
        lv_main_showAlarm = (ListView)findViewById(R.id.lv_main_showAlarm);
        lv_main_showAlarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                intent.putExtra("alarm", alarms.get(position));
                intent.putExtra("typeOfOperation", ALTER_ALARM);
                startActivityForResult(intent, ALTER_ALARM);
            }
        });
        lv_main_showAlarm.setAdapter(adapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case CREATE_ALARM:
                alarm = data.getParcelableExtra("alarm");
                dataHandler.saveAndSetAlarm(alarm);
                alarms.add(alarm);
                alarmTimes.add(DataHandler.getAlarmTime(alarm));
                adapter.notifyDataSetChanged();
                break;
            case ALTER_ALARM:
                alarm = data.getParcelableExtra("alarm");
                dataHandler.updateAndSetAlarm(alarm);
                alarms.set(pos, alarm);
                alarmTimes.set(pos, DataHandler.getAlarmTime(alarm));
                adapter.notifyDataSetChanged();
                break;
            case DELETE_ALARM:
                dataHandler.removeAndCancelAlarm(alarms.get(pos));
                alarms.remove(pos);
                alarmTimes.remove(pos);
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_alarm:
                intent.putExtra("typeOfOperation", CREATE_ALARM);
                startActivityForResult(intent, CREATE_ALARM);
                Log.i("MainActivity", "add alarm");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
