package com.amsu.healthy.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;

import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.ProvinceModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2016/11/29.
 */
public class ParseXmlDataUtil {
    private static final String TAG = "ParseXmlDataUtil";

    //从xml文件解析数据
    public static List<ProvinceModel> parseXmlDataFromAssets( Context context){
        List<ProvinceModel> provinceModels = new ArrayList<>();
        XmlPullParser xmlPullParser = Xml.newPullParser();
        AssetManager assetManager = context.getAssets();
        try {
            String fileName;
            if (MyApplication.languageType==MyApplication.language_ch){
                fileName = "address_china.xml";
            }
            else {
                fileName = "address_foreign.xml";
            }

            InputStream inputStream = assetManager.open(fileName);
            xmlPullParser.setInput(inputStream,"utf-8");
            int next = xmlPullParser.next();
            ProvinceModel  provinceModel = null;
            List<String> cityList = null;
            while (next != XmlPullParser.END_DOCUMENT) {
                switch (next) {
                    case XmlPullParser.START_DOCUMENT:
                        //Log.i("parse", "START_DOCUMENT");
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        //Log.i("parse", "END_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG:
                        String name = xmlPullParser.getName();
                        //Log.i("parse", "START_TAG" + name);
                        if (name.equals("province")) {
                            provinceModel = new ProvinceModel();
                            cityList = new ArrayList<>();
                            String province = xmlPullParser.getAttributeValue(0);
                            Log.i(TAG,"province:"+province);
                            provinceModel.setName(province);
                        } else if (name.equals("city")) {
                            String city = xmlPullParser.getAttributeValue(0);
                            Log.i(TAG,"city:"+city);
                            cityList.add(city);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        //Log.i("parse", "END_TAG");
                        String edname = xmlPullParser.getName();
                        if (edname.equals("province")) {
                            provinceModel.setCityList(cityList);
                            provinceModels.add(provinceModel);
                            provinceModel = null;
                            cityList = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = xmlPullParser.getText();
                        //Log.i("parse", "TEXT" + text);
                        break;
                }
                next = xmlPullParser.next();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return provinceModels;
    }
}
