package com.amsu.healthy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.adapter.ClubGroupAdapter;
import com.amsu.healthy.adapter.ClubNumberAdapter;
import com.amsu.healthy.bean.ClubGroup;
import com.amsu.healthy.bean.ClubNumber;

import java.util.ArrayList;
import java.util.List;

public class ClubNumberActivity extends BaseActivity {
    private ListView lv_number_list;
    private List<ClubNumber> clubNumbers;
    private ClubNumberAdapter clubGroupAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_number);
        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText("俱乐部成员");
        setLeftImage(R.drawable.back_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv_number_list = (ListView) findViewById(R.id.lv_number_list);

    }

    private void initData() {
        clubNumbers = new ArrayList<>();
        clubNumbers.add(new ClubNumber("青烟绕指柔","赵子龙","广东省  深圳市","男","18689463192","hh","游泳教练","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubNumbers.add(new ClubNumber("青烟绕指柔","赵子龙","广东省  深圳市","男","18689463192","hh","跑步教练","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubNumbers.add(new ClubNumber("青烟绕指柔","赵子龙","广东省  深圳市","男","18689463192","hh","","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubNumbers.add(new ClubNumber("青烟绕指柔","赵子龙","广东省  深圳市","男","18689463192","hh","","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubNumbers.add(new ClubNumber("青烟绕指柔","赵子龙","广东省  深圳市","男","18689463192","hh","","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));
        clubNumbers.add(new ClubNumber("青烟绕指柔","赵子龙","广东省  深圳市","男","18689463192","hh","","http://p1.qzone.la/upload/0/3t08rwvr.jpg"));


        clubGroupAdapter = new ClubNumberAdapter(this,clubNumbers);
        lv_number_list.setAdapter(clubGroupAdapter);

        lv_number_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ClubNumberActivity.this, ClubNumberDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("clubNumber",clubNumbers.get(position));
                intent.putExtra("bundle",bundle);
                startActivityForResult(intent,106);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==106 && resultCode==RESULT_OK){
            /*//创建活动成果，更新活动列表
            Bundle bundle = data.getBundleExtra("bundle");
            ClubNumber clubNumber = bundle.getParcelable("clubGroup");
            Log.i("clubNumber",clubNumber.toString());
            clubGroupAdapter.addClubGroup(clubNumber);
            clubGroupAdapter.notifyDataSetChanged();*/

        }
    }
}
