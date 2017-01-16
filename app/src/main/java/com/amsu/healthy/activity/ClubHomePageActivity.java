package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.DynamicsAdapter;
import com.amsu.healthy.bean.Dynamics;

import java.util.ArrayList;
import java.util.List;

public class ClubHomePageActivity extends BaseActivity {

    private static final String TAG = "ClubHomePageActivity";
    private ListView lv_homepage_list;
    private List<Dynamics> dynamicsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_home_page);

        initView();
        initData();
    }


    private void initView() {
        // 透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        View inflate = View.inflate(this, R.layout.view_top_club_dynamics, null);
        lv_homepage_list = (ListView) findViewById(R.id.lv_homepage_list);
        lv_homepage_list.addHeaderView(inflate);


        RelativeLayout rl_detial_number = (RelativeLayout) inflate.findViewById(R.id.rl_detial_number);
        RelativeLayout rl_detial_activity = (RelativeLayout) inflate.findViewById(R.id.rl_detial_activity);
        RelativeLayout rl_detial_group = (RelativeLayout) inflate.findViewById(R.id.rl_detial_group);
        RelativeLayout rl_detial_more = (RelativeLayout) inflate.findViewById(R.id.rl_detial_more);
        RelativeLayout rl_homepage_rank = (RelativeLayout) inflate.findViewById(R.id.rl_homepage_rank);
        ImageView iv_clubhome_back = (ImageView) inflate.findViewById(R.id.iv_clubhome_back);
        ImageView iv_clubhome_notice = (ImageView) inflate.findViewById(R.id.iv_clubhome_notice);

        RelativeLayout rl_homepage_add = (RelativeLayout) findViewById(R.id.rl_homepage_add);


        MyOnClickListener myOnClickListener = new MyOnClickListener();
        rl_detial_number.setOnClickListener(myOnClickListener);
        rl_detial_activity.setOnClickListener(myOnClickListener);
        rl_detial_group.setOnClickListener(myOnClickListener);
        rl_detial_more.setOnClickListener(myOnClickListener);
        rl_homepage_rank.setOnClickListener(myOnClickListener);
        iv_clubhome_back.setOnClickListener(myOnClickListener);
        iv_clubhome_notice.setOnClickListener(myOnClickListener);
        rl_homepage_add.setOnClickListener(myOnClickListener);
    }

    private void initData() {
        dynamicsList = new ArrayList<>();
        dynamicsList.add(new Dynamics("http://p1.qzone.la/upload/0/3t08rwvr.jpg","点点滴滴","2016/1/16/  15:50","在坚持5天就放假了，心情那叫一个激动啊，花儿对我笑，小鸟说早早早，我去上学校，啦啦啦啦........",
                new String[]{"http://upload-images.jianshu.io/upload_images/2822380-fdd45a36e2bd4fbc.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240","http://p1.qzone.la/upload/0/3t08rwvr.jpg"},
                "0","0"));
        dynamicsList.add(new Dynamics("http://v1.qzone.cc/avatar/201312/16/23/16/52af19388ec48807.jpg%21200x200.jpg","点点滴滴","2016/1/16/  15:50","智能穿戴设备，带给我很多乐趣，分享给大家",
                new String[]{"http://upload-images.jianshu.io/upload_images/656374-e7bce22771c06a9d.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240","http://upload-images.jianshu.io/upload_images/1444184-f78f95ba17a63fa5.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240","http://upload-images.jianshu.io/upload_images/3790233-949529a9259158ad.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"},
                "0","0"));
        dynamicsList.add(new Dynamics("http://img4q.duitang.com/uploads/item/201411/15/20141115110329_aAhFG.thumb.224_0.jpeg","点点滴滴","2016/1/16/  15:50","回家又要相亲了.........",
                new String[]{"http://img4q.duitang.com/uploads/item/201411/15/20141115110329_aAhFG.thumb.224_0.jpeg"},
                "0","0"));
        dynamicsList.add(new Dynamics("http://img4q.duitang.com/uploads/item/201411/15/20141115110329_aAhFG.thumb.224_0.jpeg","点点滴滴","2016/1/16/  15:50","新年快乐",
                new String[]{},
                "0","0"));

        dynamicsList.addAll(dynamicsList);

        DynamicsAdapter dynamicsAdapter = new DynamicsAdapter(dynamicsList,this);
        lv_homepage_list.setAdapter(dynamicsAdapter);
        lv_homepage_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    return;
                }
                Dynamics dynamics = dynamicsList.get(position-1);
                Log.i(TAG,"onItemClick:"+position);
                Intent intent = new Intent(ClubHomePageActivity.this, ClubDynamicsDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("dynamics",dynamics);
                intent.putExtra("bundle",bundle);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });
    }


    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_detial_number:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubNumberActivity.class));
                    break;
                case R.id.rl_detial_activity:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubCampaignActivity.class));
                    break;
                case R.id.rl_detial_group:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubGroupActivity.class));
                    break;
                case R.id.rl_detial_more:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubMoreActivity.class));
                    break;
                case R.id.rl_homepage_rank:
                    startActivity(new Intent(ClubHomePageActivity.this,ClubNumberRankActivity.class));
                    break;
                case R.id.iv_clubhome_back:
                    finish();
                    break;
                case R.id.iv_clubhome_notice:
                    //finish();
                    break;
                case R.id.rl_homepage_add:
                    startActivity(new Intent(ClubHomePageActivity.this,AddDynamicsActivity.class));
                    break;
            }
        }
    }
}
