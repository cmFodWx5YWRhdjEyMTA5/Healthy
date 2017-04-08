package com.amsu.healthy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.amsu.healthy.page.BasePage;

import java.util.List;

/**
 * Created by HP on 2016/11/25.
 */
public class AnalysisRateAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;
    //private String titleStrings[];

    public AnalysisRateAdapter(FragmentManager fm) {
        super(fm);
    }

    public AnalysisRateAdapter(FragmentManager fm, List<Fragment> fragmentList) {
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





    /*public AnalysisRateAdapter(List<BasePage> fragmentList, String titleStrings[]) {
        this.basePageLista = fragmentList;
        this.titleStrings = titleStrings;
    }

    @Override
    public int getCount() {
        return basePageLista.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BasePage basePage = basePageLista.get(position);
        View fragmentView = basePage.getView();
        container.addView(fragmentView);
        return fragmentView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleStrings[position];
    }*/
}
