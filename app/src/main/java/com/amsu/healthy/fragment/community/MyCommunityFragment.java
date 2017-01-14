package com.amsu.healthy.fragment.community;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.ClubGroupActivity;
import com.amsu.healthy.activity.ClubGroupDetialActivity;
import com.amsu.healthy.activity.ClubHomePageActivity;
import com.amsu.healthy.activity.SetupClubActivity;
import com.amsu.healthy.activity.SetupGroupActivity;
import com.amsu.healthy.adapter.AllClubsAdapter;
import com.amsu.healthy.bean.Club;
import com.amsu.healthy.bean.ClubGroup;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCommunityFragment extends Fragment {
    private View inflate;
    private ListView lv_clubs_list;
    private List<Club> clubList;
    private AllClubsAdapter mAllClubsAdapter;

    public MyCommunityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_my_community, container, false);

        initView();
        initData();
        return inflate;
    }

    private void initView() {
        lv_clubs_list = (ListView) inflate.findViewById(R.id.lv_clubs_list);
        View inflate = View.inflate(getActivity(), R.layout.view_bottom_clublist, null);
        lv_clubs_list.addFooterView(inflate);
    }

    private void initData() {
        clubList = new ArrayList<>();
        clubList.add(new Club("小鸡快跑俱乐部", "铁人三项", "成员数：0", "http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部", "铁人三项", "成员数：0", "http://v1.qzone.cc/avatar/201312/16/23/16/52af19388ec48807.jpg%21200x200.jpg"));
        clubList.add(new Club("小鸡快跑俱乐部", "铁人三项", "成员数：0", "http://img4q.duitang.com/uploads/item/201411/15/20141115110329_aAhFG.thumb.224_0.jpeg"));

        mAllClubsAdapter = new AllClubsAdapter(getActivity(), clubList);
        lv_clubs_list.setAdapter(mAllClubsAdapter);

        lv_clubs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == lv_clubs_list.getCount() - 1) {
                    startActivityForResult(new Intent(getActivity(), SetupClubActivity.class), 110);

                } else {
                    Intent intent = new Intent(getActivity(), ClubHomePageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 110 && resultCode == RESULT_OK) {
            //创建活动成果，更新活动列表
            Bundle bundle = data.getBundleExtra("bundle");
            Club club = bundle.getParcelable("club");
            Log.i("club", club.toString());
            mAllClubsAdapter.addClubGroup(club);
            mAllClubsAdapter.notifyDataSetChanged();

        }
    }
}