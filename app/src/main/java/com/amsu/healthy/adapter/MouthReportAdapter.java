package com.amsu.healthy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by HP on 2016/12/22.
 */
public class MouthReportAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    //private String titleStrings[];

    public MouthReportAdapter(FragmentManager fm) {
        super(fm);
    }

    public MouthReportAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

}
