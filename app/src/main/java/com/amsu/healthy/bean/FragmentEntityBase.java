package com.amsu.healthy.bean;

import android.support.v4.app.Fragment;

import java.io.Serializable;

/**
 * Created by 最美的时光陪我度过那些年 on 2015/12/17_11:36.
 */
public class FragmentEntityBase implements Serializable {
    private Fragment fragment;
    private String title;

    public FragmentEntityBase(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
