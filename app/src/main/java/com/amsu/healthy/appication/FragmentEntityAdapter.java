package com.amsu.healthy.appication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.amsu.healthy.bean.FragmentEntityBase;

import java.util.List;


/**
 * Created by 最美的时光陪我度过那些年 on 2015/10/23_09:20. Fragment适配器
 */
public class FragmentEntityAdapter extends FragmentStatePagerAdapter {
    private List<FragmentEntityBase> fragmentList;

    public FragmentEntityAdapter(FragmentManager fm, List<FragmentEntityBase> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        FragmentEntityBase fragmentEntity = fragmentList.get(position);
        return fragmentEntity.getFragment();
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        FragmentEntityBase fragmentEntity = fragmentList.get(position);
        return fragmentEntity.getTitle();
    }
}
