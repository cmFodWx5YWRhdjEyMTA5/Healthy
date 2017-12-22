package com.amsu.healthy.bean;

import com.amsu.healthy.utils.DateFormatUtils;
import com.amsu.healthy.utils.UStringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * author：WangLei
 * date:2017/11/1.
 * QQ:619321796
 */

public class SportRecord {
    private int id;
    private float distance;
    private int time;
    private long datatime;
    private int ahr;
    private static Map<String, List<SportRecord>> map = new HashMap<>();

    public SportRecord(int id, float distance, int time, long datatime, int ahr) {
        this.id = id;
        this.distance = distance;
        this.time = time;
        this.datatime = datatime;
        this.ahr = ahr;
    }

    public static void clear() {
        map.clear();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getDatatime() {
        return datatime;
    }

    public void setDatatime(long datatime) {
        this.datatime = datatime;
    }

    public int getAhr() {
        return ahr;
    }

    public void setAhr(int ahr) {
        this.ahr = ahr;
    }

    public static Map<String, List<SportRecord>> parse(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String ret = jsonObject.getString("ret");
            List<SportRecord> list;
            if (!UStringUtil.isNullOrEmpty(ret) && ret.equals("0")) {
                JSONArray jsonArray = jsonObject.getJSONArray("errDesc");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    int id = object.getInt("id");
                    float distance = object.getInt("distance");
                    int time = object.getInt("time");
                    long datatime = object.getLong("datatime");
                    String date = DateFormatUtils.getFormatTime(datatime, DateFormatUtils.YYYY_MM_DD);
                    int ahr = object.getInt("ahr");
                    SportRecord record = new SportRecord(id, distance, time, datatime, ahr);
                    if (map.containsKey(date)) {
                        map.get(date).add(record);
                    } else {
                        list = new ArrayList<>();
                        list.add(record);
                        map.put(date, list);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sortMapByKey(map);
    }

    private static Map<String, List<SportRecord>> sortMapByKey(Map<String, List<SportRecord>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, List<SportRecord>> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    private static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            return str2.compareTo(str1);
        }
    }
}
