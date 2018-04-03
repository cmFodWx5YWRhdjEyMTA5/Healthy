package com.amsu.wear.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amsu.wear.R;
import com.amsu.wear.bean.HistoryRecord;
import com.amsu.wear.bean.JsonBase;
import com.amsu.wear.bean.ParcelableDoubleList;
import com.amsu.wear.bean.UploadRecord;
import com.amsu.wear.util.DataUtil;
import com.amsu.wear.util.DialogUtil;
import com.amsu.wear.util.FormatUtil;
import com.amsu.wear.util.HttpUtil;
import com.amsu.wear.util.JsonUtil;
import com.amsu.wear.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Date;
import java.util.List;

import butterknife.BindBitmap;
import butterknife.BindView;

public class HistoryDetailActivity extends BaseActivity {
    private static final String TAG = HistoryDetailActivity.class.getSimpleName();
    @BindView(R.id.detailsDate)
    TextView detailsDate;
    @BindView(R.id.detailsTime)
    TextView detailsTime;
    @BindView(R.id.detailsDistance)
    TextView detailsDistance;
    @BindView(R.id.detailsDataTime)
    TextView detailsDataTime;
    @BindView(R.id.detailsHR)
    TextView detailsHR;
    @BindView(R.id.detailsSpeed)
    TextView detailsSpeed;
    @BindView(R.id.detailsCAL)
    TextView detailsCAL;
    @BindView(R.id.detailsCadence)
    TextView detailsCadence;
    @BindView(R.id.historyIcon)
    ImageView historyIcon;

    @BindBitmap(R.drawable.huwai_3)
    Bitmap image1;
    @BindBitmap(R.drawable.shinei_3)
    Bitmap image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_history_detail;
    }


    protected void initView() {
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        final HistoryRecord historyRecord = bundle.getParcelable("historyRecord");
        if (historyRecord==null){
            final UploadRecord uploadRecord = bundle.getParcelable("uploadRecord");
            showData(uploadRecord);
        }
        else {
            getHistoryReportDetail(historyRecord);
        }

    }

    private void getHistoryReportDetail(final HistoryRecord historyRecord) {
        DialogUtil.showDialog(getResources().getString(R.string.loading),this);

        RequestParams params = new RequestParams();
        params.addBodyParameter("id",historyRecord.getId());
        HttpUtil.addCookieForHttp(params);
        params.setUri("http://www.amsu-new.com:8081/intellingence-web/getDetail.do");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (isActiivtyDestroy)return;
                Log.i(TAG,"上传onSuccess==result:"+result);

                //JsonBase<UploadRecord> jsonBase = MyUtil.commonJsonParse(result, new TypeToken<JsonBase<UploadRecord>>() {}.getType());
                //Log.i(TAG,"jsonBase："+jsonBase);

                Gson gson = new Gson();
                JsonBase jsonBase = gson.fromJson(result, JsonBase.class);
                Log.i(TAG,"jsonBase:"+jsonBase);
                if (jsonBase.getRet()==0){
                    parseHealthData(result);
                }
                else {
                    finish();
                    ToastUtil.showToask("数据加载失败");
                }

                DialogUtil.hideDialog(HistoryDetailActivity.this);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (isActiivtyDestroy)return;
                DialogUtil.hideDialog(HistoryDetailActivity.this);
                ToastUtil.showToask("数据加载失败");
                Log.i(TAG,"上传onFailure==s:"+ex);
                finish();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    private void parseHealthData(String result) {

        String iosDefaultString = "\"0\"";
        try {
            JSONObject object = new JSONObject(result);
            JSONObject jsonObject =object.getJSONObject("errDesc");
            String id    = jsonObject.getString("id");
            String fi    = jsonObject.getString("fi");
            String es    = jsonObject.getString("es");
            String pi    = jsonObject.getString("pi");
            String cc    = jsonObject.getString("cc");
            String hrvr  = jsonObject.getString("hrvr");
            String hrvs  = jsonObject.getString("hrvs");
            String ahr   = jsonObject.getString("ahr");
            String maxhr = jsonObject.getString("maxhr");
            String minhr = jsonObject.getString("minhr");
            String hrr   = jsonObject.getString("hrr");
            String hrs   = jsonObject.getString("hrs");
            String ec    = jsonObject.getString("ec");
            String ecr   = jsonObject.getString("ecr");
            String ecs   = jsonObject.getString("ecs");
            String ra    = jsonObject.getString("ra");
            String timestamp = jsonObject.getString("timestamp");
            String datatime  = jsonObject.getString("datatime");
            String hr    = jsonObject.getString("hr");
            String ae    = jsonObject.getString("ae");
            String distance  = jsonObject.getString("distance");
            String time  = jsonObject.getString("time");
            String cadence   = jsonObject.getString("cadence");
            String calorie   = jsonObject.getString("calorie");
            String state = jsonObject.getString("state");
            String zaobo = jsonObject.getString("zaobo");
            String loubo = jsonObject.getString("loubo");
            String latitudeLongitude = jsonObject.getString("latitudeLongitude");

            //后加字段
            String sdnn1 = jsonObject.getString("sdnn1");
            String sdnn2 = jsonObject.getString("sdnn2");
            String lf1 = jsonObject.getString("lf1");
            String lf2 = jsonObject.getString("lf2");
            String hf1 = jsonObject.getString("hf1");
            String hf2 = jsonObject.getString("hf2");
            String hf = jsonObject.getString("hf");
            String lf = jsonObject.getString("lf");
            String chaosPlotPoint = jsonObject.getString("chaosPlotPoint");
            String frequencyDomainDiagramPoint = jsonObject.getString("frequencyDomainDiagramPoint");
            String chaosPlotMajorAxis = jsonObject.getString("chaosPlotMajorAxis");
            String chaosPlotMinorAxis = jsonObject.getString("chaosPlotMinorAxis");


            UploadRecord uploadRecord = new UploadRecord();
            uploadRecord.id = Long.parseLong(id);
            uploadRecord.fi = Integer.parseInt(fi);
            uploadRecord.es = Integer.parseInt(es);
            uploadRecord.pi = Integer.parseInt(pi);
            uploadRecord.cc = Integer.parseInt(cc);
            uploadRecord.hrvr = hrvr;
            uploadRecord.hrvs = hrvs;
            uploadRecord.ahr = Integer.parseInt(ahr);
            uploadRecord.maxhr = Integer.parseInt(maxhr);
            uploadRecord.minhr = Integer.parseInt(minhr);
            uploadRecord.hrr = hrr;
            uploadRecord.hrs = hrs;
            uploadRecord.ec =ec;
            uploadRecord.ecr =Integer.parseInt(ecr);
            uploadRecord.ecs =ecs;
            uploadRecord.ra =Integer.parseInt(ra);
            uploadRecord.timestamp =Long.parseLong(timestamp);
            uploadRecord.datatime =datatime;

            Log.i(TAG,"ae:"+ae);

            /*if (!MyUtil.isEmpty(hr) && !hr.equals(Constant.uploadRecordDefaultString)  && !hr.equals("-1")){
                uploadRecord.hr = gson.fromJson(hr,new TypeToken<List<Integer>>() {}.getType());
            }*/
            /*if (!MyUtil.isEmpty(ae) && !ae.equals(Constant.uploadRecordDefaultString)){
                uploadRecord.ae =gson.fromJson(ae,new TypeToken<List<Integer>>() {}.getType());
            }*/

            uploadRecord.hr = JsonUtil.parseListJson(hr,new TypeToken<List<Integer>>() {}.getType());
            uploadRecord.ae = JsonUtil.parseListJson(ae,new TypeToken<List<Integer>>() {}.getType());
            uploadRecord.cadence = JsonUtil.parseListJson(cadence,new TypeToken<List<Integer>>() {}.getType());
            uploadRecord.calorie =JsonUtil.parseListJson(calorie,new TypeToken<List<String>>() {}.getType());
            uploadRecord.latitudeLongitude = JsonUtil.parseListJson(latitudeLongitude,new TypeToken<List<ParcelableDoubleList>>() {}.getType());

            /*if (!MyUtil.isEmpty(cadence) && !cadence.equals(Constant.uploadRecordDefaultString) ){
                uploadRecord.cadence = gson.fromJson(cadence,new TypeToken<List<Integer>>() {}.getType());
            }
            if (!MyUtil.isEmpty(calorie) && !calorie.equals(Constant.uploadRecordDefaultString)){
                uploadRecord.calorie =gson.fromJson(calorie,new TypeToken<List<String>>() {}.getType());
            }*/

           /* Log.i(TAG,"latitudeLongitude:"+latitudeLongitude);
            Log.i(TAG,"latitudeLongitude:"+latitudeLongitude.length());
            if (!MyUtil.isEmpty(latitudeLongitude) && !latitudeLongitude.equals(Constant.uploadRecordDefaultString) && latitudeLongitude.length()>5){
                uploadRecord.latitudeLongitude = gson.fromJson(latitudeLongitude,new TypeToken<List<ParcelableDoubleList>>() {}.getType());
            }*/

            uploadRecord.distance = Float.parseFloat(distance);
            uploadRecord.time = (long) Float.parseFloat(time);
            uploadRecord.state =Integer.parseInt(state);
            uploadRecord.zaobo =Integer.parseInt(zaobo);
            uploadRecord.loubo =Integer.parseInt(loubo);

            //uploadRecord.localEcgFileName = MyUtil.generateECGFilePath(HeartRateResultShowActivity.this, System.currentTimeMillis());

            if (!TextUtils.isEmpty(sdnn1) && !sdnn1.equals("null")){
                uploadRecord.sdnn1 = (int) Float.parseFloat(sdnn1);
            }
            if (!TextUtils.isEmpty(sdnn2) && !sdnn2.equals("null")){
                uploadRecord.sdnn2 = (int) Float.parseFloat(sdnn2);
            }

            if (!TextUtils.isEmpty(lf1) && !lf1.equals("null")){
                uploadRecord.lf1 = Double.parseDouble(lf1);
            }
            if (!TextUtils.isEmpty(lf2) && !lf2.equals("null")){
                uploadRecord.lf2 = Double.parseDouble(lf2);
            }
            if (!TextUtils.isEmpty(hf1) && !hf1.equals("null")){
                uploadRecord.hf1 = Double.parseDouble(hf1);
            }
            if (!TextUtils.isEmpty(hf2) && !hf2.equals("null")){
                uploadRecord.hf2 = Double.parseDouble(hf2);
            }
            if (!TextUtils.isEmpty(hf) && !hf.equals("null")){
                uploadRecord.hf = Double.parseDouble(hf);
            }
            if (!TextUtils.isEmpty(lf) && !lf.equals("null")){
                uploadRecord.lf = Double.parseDouble(lf);
            }

            uploadRecord.chaosPlotPoint = JsonUtil.parseListJson(chaosPlotPoint,new TypeToken<List<Integer>>() {}.getType());
            uploadRecord.frequencyDomainDiagramPoint = JsonUtil.parseListJson(frequencyDomainDiagramPoint,new TypeToken<List<Double>>() {}.getType());

            /*if (!MyUtil.isEmpty(chaosPlotPoint) && !chaosPlotPoint.equals(Constant.uploadRecordDefaultString) ){
                uploadRecord.chaosPlotPoint = gson.fromJson(chaosPlotPoint,new TypeToken<List<Integer>>() {}.getType());
            }

            if (!MyUtil.isEmpty(frequencyDomainDiagramPoint) && !frequencyDomainDiagramPoint.equals(Constant.uploadRecordDefaultString) ){
                uploadRecord.frequencyDomainDiagramPoint = gson.fromJson(frequencyDomainDiagramPoint,new TypeToken<List<Double>>() {}.getType());
            }*/

            if (!TextUtils.isEmpty(chaosPlotMajorAxis) && !chaosPlotMajorAxis.equals("null") && !chaosPlotMajorAxis.equals(iosDefaultString)){
                uploadRecord.chaosPlotMajorAxis =  (int) Float.parseFloat(chaosPlotMajorAxis);
            }
            if (!TextUtils.isEmpty(chaosPlotMinorAxis) && !chaosPlotMinorAxis.equals("null") && !chaosPlotMajorAxis.equals(iosDefaultString)){
                uploadRecord.chaosPlotMinorAxis =  (int) Float.parseFloat(chaosPlotMinorAxis);
            }

            uploadRecord.uploadState = 1;

            showData(uploadRecord);

            //Log.i(TAG,"mUploadRecord:"+mUploadRecord);
            //Log.i(TAG,"mUploadRecord.ae:"+mUploadRecord.ae);

            Log.i(TAG,"mUploadRecord:"+ uploadRecord);
            Log.i(TAG,"latitudeLongitude:"+latitudeLongitude);
            Log.i(TAG,"mUploadRecord.latitudeLongitude:"+ uploadRecord.latitudeLongitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (NumberFormatException e){
            e.printStackTrace();
            Log.e(TAG,"e:"+e);
        }catch (JsonSyntaxException e){
            e.printStackTrace();
            Log.e(TAG,"e1:"+e);
        }
    }


    protected void showData(UploadRecord uploadRecord) {
        if (uploadRecord == null) {
            finish();
            return;
        }
        float distance = uploadRecord.distance;

        if (uploadRecord.calorie!=null && uploadRecord.calorie.size()>0) { //卡路里
            float[] floats = DataUtil.listToFloatArray(uploadRecord.calorie);
            float allcalorie = 0 ;
            float max= 0;
            for (float i: floats){
                allcalorie+=i;
                //Log.i(TAG,"i:"+(int)i);
                if (i<13 && i>max){
                    max = i;
                }
            }
            Log.i(TAG,"max:"+max);
            detailsCAL.setText((int)allcalorie+ "CAL");

        }

        if (uploadRecord.cadence!=null && uploadRecord.cadence.size()>0){ //步频
            Log.i(TAG,"步频数据： "+uploadRecord.cadence);
            int[] stepData = DataUtil.listToIntArray(uploadRecord.cadence);
            int allcadence = 0 ;
            for (int i: stepData){
                allcadence+=i;
            }
            int averageCadence = allcadence / stepData.length;
            //float averageCadence = (float) Math.ceil( (double) allcadence / ints.length);
            detailsCadence.setText(averageCadence +"步/分");

        }

        long datatime = uploadRecord.timestamp;
        int hr =uploadRecord.ahr;
        int state = uploadRecord.state;

        String myDistance = FormatUtil.getFormatDistance(distance);
        detailsDistance.setText(myDistance);

        String myDuration = FormatUtil.getPaceFormatTime(uploadRecord.time);
        detailsDataTime.setText(myDuration);

        String formatSpeed = FormatUtil.getFormatRunPace(distance, uploadRecord.time);
        detailsSpeed.setText(formatSpeed);

        String date = FormatUtil.getSpecialFormatTime("MM/dd",new Date(datatime));
        String timex = FormatUtil.getSpecialFormatTime("HH:mm",new Date(datatime));
        detailsDate.setText(date);
        detailsTime.setText(timex);

        if (hr!=0) {
            detailsHR.setText(hr + "BPM");
        } else {
            detailsHR.setText("--BPM");
        }

        switch (state) {//0静态1室外2室内
            case 1:
                historyIcon.setImageBitmap(image1);
                break;
            case 2:
                historyIcon.setImageBitmap(image2);
                break;
        }
    }


    private boolean isActiivtyDestroy;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActiivtyDestroy = true;
    }
}
