package com.amsu.wear.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amsu.bleinteraction.proxy.BleUtil;
import com.amsu.wear.R;
import com.amsu.wear.adapter.FragmentListAdapter;
import com.amsu.wear.application.MyApplication;
import com.amsu.wear.fragment.DeviceListFragment;
import com.amsu.wear.fragment.HistroyRecordFragment;
import com.amsu.wear.fragment.HomeFragment;
import com.amsu.wear.fragment.IndexWarringFragment;
import com.amsu.wear.fragment.UserIconFragment;
import com.amsu.wear.util.LogUtil;
import com.amsu.wear.util.ToastUtil;
import com.amsu.wear.util.UploadHealthyDataUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayout ll_main_index;
    private ImageView[] mImageViews = null;
    private ViewPager vp_main_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        boolean running = MyApplication.getInstance().isRunning();
        LogUtil.i(TAG,"running:"+running);
        if (running){
            startActivity(new Intent(this,RunningActivity.class));
        }

        vp_main_fragment = findViewById(R.id.vp_main_fragment);
        ll_main_index = findViewById(R.id.ll_main_index);

        //将Fragment对象添加到list中
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new UserIconFragment());
        fragmentList.add(new DeviceListFragment());
        fragmentList.add(new HomeFragment());
        fragmentList.add(new IndexWarringFragment());
        fragmentList.add(new HistroyRecordFragment());

        FragmentListAdapter adapter = new FragmentListAdapter(getSupportFragmentManager(), fragmentList);

        vp_main_fragment.setAdapter(adapter);
        vp_main_fragment.setCurrentItem(2);
        //vp_main_fragment.setOffscreenPageLimit(5);

        vp_main_fragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /*RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v_analysis_select.getLayoutParams();
                int floatWidth=  (int) (mOneTableWidth *(positionOffset+position));  //view向左的偏移量
                layoutParams.setMargins(floatWidth,0,0,0); //4个参数按顺序分别是左上右下
                v_analysis_select.setLayoutParams(layoutParams);*/
            }

            @Override
            public void onPageSelected(int position) {
                if (mImageViews != null) {
                    int imgCount = mImageViews.length;
                    if (imgCount != 0) {
                        position = position % imgCount;
                    }
                    mImageViews[position].setBackgroundResource(R.drawable.banner_dian_1);
                    for (int i = 0; i < mImageViews.length; i++) {
                        if (position != i) {
                            mImageViews[i].setBackgroundResource(R.drawable.banner_dian_2);
                        }
                    }
                }
                if (position == 3) {
                    //homeWeekReportFragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        initIndexView(fragmentList.size());

        /*User user = new User();
        user.setPhone("18689463192");
        UserUtil.saveUserToLocal(user);*/

        BleUtil.checkBluetoothAndOpen(this);

        //UserUtil.saveUserToLocal(null);

   /*     List<User> users = new ArrayList<>();
        users.add(new User());
        SPUtil.putObjectToSP("users",users);
        //User user = (User) SPUtil.getObjectFromSP("user", User.class);
        //List<User> usersfromSp = (List<User>) SPUtil.getObjectFromSP("users", new ArrayList<>().getClass());
        //LogUtil.i(TAG,"usersfromSp:"+usersfromSp);
        //LogUtil.i(TAG,"usersfromSp:"+usersfromSp.get(0).getAge());

        SPUtil.putListToSP(users,"users");
        ArrayList<User> usersfromSp = JsonUtil.fromJsonList("users", User.class);
        LogUtil.i(TAG,"usersfromSp:"+usersfromSp);
        LogUtil.i(TAG,"usersfromSp:"+usersfromSp.get(0).getAge());*/

        //UserUtil.saveUserToLocal(null);

        /*DBUtil dbUtil = new DBUtil();

        UploadRecord uploadRecord = new UploadRecord();
        try {
            dbUtil.getDbManager().save(uploadRecord);
        } catch (DbException e) {
            e.printStackTrace();
            LogUtil.e(TAG,"e:"+e);
        }
*/


    }

    private void initData() {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        int mCurrYear = calendar.get(Calendar.YEAR);
        int mCurrWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        UploadHealthyDataUtil.downlaodWeekReport(mCurrYear,mCurrWeekOfYear,false,null);
    }

    private void initIndexView(int imageCount) {
        ll_main_index.removeAllViews();
        mImageViews = new ImageView[imageCount];
        Resources r = getResources();
        float x10 = r.getDimension(R.dimen.x10);
        int xI10 = Math.round(x10);
        float x5 = r.getDimension(R.dimen.x2);
        int margin = Math.round(x5);
        int middle = Math.round(imageCount / 2f);
        boolean is = imageCount % 2 == 0;
        middle = is ? middle : middle - 1;
        for (int i = 0; i < imageCount; i++) {
            ImageView mImageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = xI10;
            if (i < middle) {
                params.bottomMargin = (middle - i) * margin + xI10 - (i * 3);
            } else if (i == middle) {
                params.bottomMargin = xI10 - 3;
            } else {
                params.bottomMargin = (i - middle) * margin + xI10 - ((middle - (i - middle)) * 3);
            }
            mImageView.setLayoutParams(params);
            mImageViews[i] = mImageView;
            if (i == middle) {
                mImageViews[i].setBackgroundResource(R.drawable.banner_dian_1);
            } else {
                mImageViews[i].setBackgroundResource(R.drawable.banner_dian_2);
            }
            ll_main_index.addView(mImageViews[i]);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Process.killProcess(Process.myPid());
        LogUtil.i(TAG,"onKeyDown:"+event);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            System.exit(0);
            LogUtil.i(TAG,"KEYCODE_BACK:");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtil.i(TAG,"onBackPressed:");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG,"onResume:");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i(TAG,"onStart:");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG,"onStop:");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.i(TAG,"onRestart:");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG,"onPause:");
    }

    @Override
    public boolean onNavigateUp() {
        LogUtil.i(TAG,"onNavigateUp:");
        return super.onNavigateUp();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        LogUtil.i(TAG,"onKeyUp:event "+event);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG,"onDestroy:");
        ToastUtil.showToask("应用已退出");
        System.exit(0);
    }

    @Override
    public boolean moveTaskToBack(boolean nonRoot) {
        LogUtil.i(TAG,"moveTaskToBack:"+nonRoot);
        return super.moveTaskToBack(nonRoot);
    }


    @Override
    public void finishAndRemoveTask() {
        LogUtil.i(TAG,"finishAndRemoveTask:");
        super.finishAndRemoveTask();
    }
}
