package com.amsu.healthy.adapter;

import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.amsu.healthy.fragment.BaseFragment;
import com.amsu.healthy.page.BasePage;

import java.util.List;

/**
 * Created by HP on 2016/11/25.
 */
public class AnalysisRateAdapter extends PagerAdapter {
    private List<BasePage> basePageLista;
    private String titleStrings[];

    public AnalysisRateAdapter(List<BasePage> fragmentList, String titleStrings[]) {
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
    }
}
