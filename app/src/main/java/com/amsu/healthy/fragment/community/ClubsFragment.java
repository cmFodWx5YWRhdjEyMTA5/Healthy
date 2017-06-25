package com.amsu.healthy.fragment.community;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.ClubHomePageActivity;
import com.amsu.healthy.adapter.AllClubsAdapter;
import com.amsu.healthy.bean.Club;
import com.amsu.healthy.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClubsFragment extends BaseFragment {


    private View inflate;
    private ListView lv_clubs_list;
    private List<Club> clubList;

    public ClubsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflate = inflater.inflate(R.layout.fragment_clubs, container, false);

        initView();
        initData();
        return inflate;
    }



    private void initView() {
        lv_clubs_list = (ListView) inflate.findViewById(R.id.lv_clubs_list);
    }

    private void initData() {
        clubList = new ArrayList<>();
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://v1.qzone.cc/avatar/201312/16/23/16/52af19388ec48807.jpg%21200x200.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://img4q.duitang.com/uploads/item/201411/15/20141115110329_aAhFG.thumb.224_0.jpeg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://v1.qzone.cc/avatar/201312/16/23/16/52af19388ec48807.jpg%21200x200.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://img4q.duitang.com/uploads/item/201411/15/20141115110329_aAhFG.thumb.224_0.jpeg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://v1.qzone.cc/avatar/201312/16/23/16/52af19388ec48807.jpg%21200x200.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://img4q.duitang.com/uploads/item/201411/15/20141115110329_aAhFG.thumb.224_0.jpeg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://v1.qzone.cc/avatar/201312/16/23/16/52af19388ec48807.jpg%21200x200.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部","铁人三项","成员数：0","http://img4q.duitang.com/uploads/item/201411/15/20141115110329_aAhFG.thumb.224_0.jpeg"));

        AllClubsAdapter allClubsAdapter = new AllClubsAdapter(getActivity(),clubList);
        lv_clubs_list.setAdapter(allClubsAdapter);

        lv_clubs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ClubHomePageActivity.class);
                intent.putExtra("isJioned",false);
                startActivity(intent);
            }
        });
    }

}
