package com.alarm.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/24.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mList = new ArrayList<Fragment>();
    public FragmentAdapter(FragmentManager fm , List<Fragment> list) {
        super(fm);
        this.mList = list;
    }
    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }
    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }
}
