package com.amsu.healthy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.Device;
import com.amsu.healthy.utils.Constant;
import com.amsu.healthy.utils.MyUtil;
import com.mob.tools.utils.ResHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.SearchEngine;
import cn.smssdk.gui.layout.SizeHelper;
import cn.smssdk.utils.SMSLog;

public class LoginCountryListActivity extends BaseActivity implements TextWatcher {
    private static final String TAG = "LoginCountryListActivity";
    // 国家号码规则
    private HashMap<String, String> countryRules;
    private EventHandler handler;
    private TextView et_country_search;
    private ImageView im_country_search;
    private ListView lv_country_list;
    private TextView tv_country_searchdec;
    private SearchEngine sEngine;
    private CountryListAdapter countryListAdapter;
    private HashMap<Character, ArrayList<String[]>> mGgroupedCountryList;
    private ArrayList<String> countries;
    private ArrayList<String[]> mCountryList;
    private RelativeLayout rl_country_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_country_list);

        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setCenterText(getResources().getString(R.string.login));
        setLeftImage(R.drawable.guanbi_icon);
        getIv_base_leftimage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        rl_country_search = (RelativeLayout) findViewById(R.id.rl_country_search);
        et_country_search = (TextView) findViewById(R.id.et_country_search);
        tv_country_searchdec = (TextView) findViewById(R.id.tv_country_searchdec);
        im_country_search = (ImageView) findViewById(R.id.im_country_search);
        lv_country_list = (ListView) findViewById(R.id.lv_country_list);

        et_country_search.addTextChangedListener(this);

        rl_country_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_country_searchdec.setVisibility(View.GONE);
                im_country_search.setVisibility(View.GONE);
                et_country_search.setCursorVisible(true);
            }
        });

        et_country_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_country_searchdec.setVisibility(View.GONE);
                im_country_search.setVisibility(View.GONE);
                et_country_search.setCursorVisible(true);
            }
        });


        lv_country_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] country = mCountryList.get(position);
                if (country!=null){
                    if (countryRules != null && countryRules.containsKey(country[1])) {
                        Intent intent = new Intent();
                        /*String countryString = Arrays.toString(country);
                        intent.putExtra("countryString",countryString);*/
                        String countryId = country[2];
                        intent.putExtra("countryId",countryId);
                        setResult(RESULT_OK,intent);
                    } else {
                        MyUtil.showToask(LoginCountryListActivity.this,R.string.smssdk_country_not_support_currently);
                    }
                    finish();
                }
            }
        });

    }

    private void initData() {
        mGgroupedCountryList = SMSSDK.getGroupedCountryList();

        mCountryList = new ArrayList<>();

        mCountryList.add(new String[]{getResources().getString(R.string.china),"86","42"});
        mCountryList.add(new String[]{getResources().getString(R.string.united_states),"1","2"});
        mCountryList.add(new String[]{getResources().getString(R.string.japan),"81","39"});
        mCountryList.add(new String[]{getResources().getString(R.string.hong_kong),"852","168"});

        mCountryList.add(new String[]{getResources().getString(R.string.all_countries_regions),"-1","0"});

        for (ArrayList<String[]> characterList: mGgroupedCountryList.values()){
            for (String[] s:characterList){
                System.out.println(s);
                mCountryList.add(s);
            }
        }

        Log.i(TAG,"countryList:"+ mCountryList);

        countryListAdapter = new CountryListAdapter(this, mCountryList);
        lv_country_list.setAdapter(countryListAdapter);

        sEngine = new SearchEngine();

        ArrayList<String> countries = new ArrayList<String>();
        for (Map.Entry<Character, ArrayList<String[]>> ent : mGgroupedCountryList.entrySet()) {
            ArrayList<String[]> cl = ent.getValue();
            for (String[] paire : cl) {
                countries.add(paire[0]);
            }
        }
        sEngine.setIndex(countries);


        //Log.i(TAG,"groupedCountryList:"+groupedCountryList.toString());


        handler = new EventHandler() {
            @SuppressWarnings("unchecked")
            public void afterEvent(int event, final int result, final Object data) {
                Log.i(TAG,"event:"+event+"  result:"+result+"  data:"+data);
                if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                onCountryListGot((ArrayList<HashMap<String,Object>>) data);
                            } else {
                                finish();
                            }
                        }
                    });
                }
            }
        };
        // 注册回调接口
        SMSSDK.registerEventHandler(handler);
        // 获取国家列表
        SMSSDK.getSupportedCountries();
    }

    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        // 解析国家列表
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }

            if (countryRules == null) {
                countryRules = new HashMap<String, String>();
            }
            countryRules.put(code, rule);
            Log.i(TAG,"code:"+code+",rule:"+rule);
        }
        // 回归页面初始化操作
        //initPage();

    }

    public void clickSearch(View view) {


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        search(s.toString().toLowerCase());

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 搜索
     * @param token
     */
    public void search(String token) {
        ArrayList<String> res = sEngine.match(token);
        boolean isEmptyToken = false;
        if (res == null || res.size() <= 0) {
            res = new ArrayList<String>();
            isEmptyToken = true;
        }

        HashMap<String, String> resMap = new HashMap<String, String>();
        for (String r : res) {
            resMap.put(r, r);
        }

        mCountryList.clear();

        ArrayList<String> titles = new ArrayList<String>();
        ArrayList<ArrayList<String[]>> countries = new ArrayList<ArrayList<String[]>>();
        for (Map.Entry<Character, ArrayList<String[]>> ent : mGgroupedCountryList.entrySet()) {
            ArrayList<String[]> cl = ent.getValue();
            ArrayList<String[]> list = new ArrayList<String[]>();
            for (String[] paire : cl) {
                if (isEmptyToken || resMap.containsKey(paire[0])) {
                    list.add(paire);
                }
            }
            if (list.size() > 0) {
                titles.add(String.valueOf(ent.getKey()));
                countries.add(list);
            }

            mCountryList.addAll(list);
        }

        Log.i(TAG,"mCountryList:"+mCountryList);
        Log.i(TAG,"countries:"+countries);
        countryListAdapter.notifyDataSetChanged();
    }


    class CountryListAdapter extends BaseAdapter {
        ArrayList<String[]> countries;
        Context context;

        public CountryListAdapter(Context context, ArrayList<String[]> countries) {
            this.countries = countries;
            this.context = context;
        }

        @Override
        public int getCount() {
            return countries.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String[] strings = countries.get(position);
            if (position==4 && "-1".equals(strings[1])){  //==4时为分类标签
                TextView textView = new TextView(parent.getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.x40));
                textView.setTextColor(Color.parseColor("#949494"));
                textView.setBackgroundColor(getResources().getColor(R.color.app_background_color));

                int dp6 = (int) getResources().getDimension(R.dimen.x36);
                textView.setPadding(dp6, 0, dp6, 0);textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.x88)));

                textView.setText(strings[0]);
                return textView;
            }
            else {
                View inflate = View.inflate(context, R.layout.view_item_country_list, null);
                TextView tv_item_countryname = (TextView) inflate.findViewById(R.id.tv_item_countryname);
                TextView tv_item_countrycode = (TextView) inflate.findViewById(R.id.tv_item_countrycode);

                if (strings.length>=2){
                    tv_item_countryname.setText(strings[0]);
                    tv_item_countrycode.setText(strings[1]);
                }
                return inflate;
            }

        }
    }

    /*class CountryCodeInfo{
        public String code;
        public String code;
    }*/

}
