package com.amsu.wear.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;

import java.lang.reflect.Field;
import java.net.HttpCookie;
import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy-master
 * @class name：com.amsu.wear.util
 * @time 2018-03-09 10:58 AM
 * @describe
 */
public class HttpUtil {

    private static final String TAG = HttpUtil.class.getSimpleName();
    public static final int HttpUploadData_success = 1;
    public static final int HttpUploadData_fail = 0;


    //添加Cookie
    public static void addCookieForHttp(RequestParams requestParams){
        String cookie = SPUtil.getStringValueFromSP("Cookie");
        Log.i(TAG,"cookie:"+cookie);
        if (!TextUtils.isEmpty(cookie)){
            requestParams.addHeader("Cookie",cookie);
        }
        else {
            Log.e("com.amsu.healthy","Cookie is null");
        }
    }

    public static void saveCookieToSP() {
        /*ci_session,94efd294d41da5b13b0740f476a93950df0c4159
        uid,18689463192
        id,9
        token,66-7550512929-106-27-7353-46-269-33-31-109
        userParam,1481855331071*/
        DbCookieStore instance = DbCookieStore.INSTANCE;
        List<HttpCookie> cookies = instance.getCookies();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cookies.size(); i++) {
            String name = cookies.get(i).getName();
            String value = cookies.get(i).getValue();
            //Log.i(TAG,"cookies:"+ name +","+ value);
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)){
                sb.append(name + "=" );
                sb.append(value + ";" );
            }
        }
        Log. i("Cookie", sb.toString());
        SPUtil.putStringValueToSP("Cookie", sb.toString());
    }

    //将javabean封装到http的请求参数RequestParams中（键值对的方式：参数名为类的属性名，参数值为类的属性值）
    public  static void addObjectToHttpParm(Object o,RequestParams params){
        Field fields[]=o.getClass().getDeclaredFields();//cHis 是实体类名称
        try {
            Field.setAccessible(fields, true);
            for (Field field : fields) {
                String name = field.getName();
                Object value = field.get(o);//cHis 是实体类名称
                if (value!=null){
                    LogUtil.i(TAG, "参数 " + name + ":" + value);
                    params.addBodyParameter(name, value.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(TAG, "e " + e);
        }
    }

    //同上面方法addObjectToHttpParm()，添加了可变参数intValueName，表示这些参数值必须是Int整型值
    public  static void addObjectToHttpParmHaveInt(Object o,RequestParams params,String ...intValueName){
        Field fields[]=o.getClass().getDeclaredFields();//cHis 是实体类名称
        try {
            Field.setAccessible(fields, true);
            for (Field field : fields) {
                String name = field.getName();
                Object value = field.get(o);//cHis 是实体类名称

                if (value!=null){
                    boolean isIntValue = false;
                    for (String intName:intValueName){
                        if (name.equals(intName)){
                            isIntValue = true;
                            break;
                        }
                    }
                    LogUtil.i(TAG, "参数 " + name + ":" + value);
                    if (isIntValue){
                        int i = (int) Double.parseDouble(value+"");
                        params.addBodyParameter(name, i+"");
                    }
                    else {
                        params.addBodyParameter(name, value.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(TAG, "e " + e);
        }
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }

}
