package com.amsu.healthy.utils.map;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class Util {
	private static final String TAG = "Util";

	/**
	 * 将AMapLocation List 转为TraceLocation list
	 * 
	 * @param list
	 * @return
	 */
	public static List<TraceLocation> parseTraceLocationList(
			List<AMapLocation> list) {
		List<TraceLocation> traceList = new ArrayList<TraceLocation>();
		if (list == null) {
			return traceList;
		}
		for (int i = 0; i < list.size(); i++) {
			TraceLocation location = new TraceLocation();
			AMapLocation amapLocation = list.get(i);
			location.setBearing(amapLocation.getBearing());
			location.setLatitude(amapLocation.getLatitude());
			location.setLongitude(amapLocation.getLongitude());
			location.setSpeed(amapLocation.getSpeed());
			location.setTime(amapLocation.getTime());
			traceList.add(location);
		}
		return traceList;
	}

	public static List<TraceLocation> parseTraceLocationListByLatLng(List<LatLng> list) {
		List<TraceLocation> traceList = new ArrayList<TraceLocation>();
		if (list == null) {
			return traceList;
		}
		for (int i = 0; i < list.size(); i++) {
			TraceLocation location = new TraceLocation();
			LatLng latLng = list.get(i);
			location.setLatitude(latLng.latitude);
			location.setLongitude(latLng.longitude);
			traceList.add(location);
		}
		return traceList;
	}
	public static TraceLocation parseTraceLocation(AMapLocation amapLocation) {
		TraceLocation location = new TraceLocation();
		location.setBearing(amapLocation.getBearing());
		location.setLatitude(amapLocation.getLatitude());
		location.setLongitude(amapLocation.getLongitude());
		location.setSpeed(amapLocation.getSpeed());
		location.setTime(amapLocation.getTime());
		return  location;
	}

	/**
	 * 将AMapLocation List 转为LatLng list
	 * @param list
	 * @return
	 */
	public static List<LatLng> parseLatLngList(List<AMapLocation> list) {
		List<LatLng> traceList = new ArrayList<>();
		if (list == null) {
			return traceList;
		}
		for (int i = 0; i < list.size(); i++) {
			AMapLocation loc = list.get(i);
			double lat = loc.getLatitude();
			double lng = loc.getLongitude();
			LatLng latlng = new LatLng(lat, lng);
			traceList.add(latlng);
		}
		return traceList;
	}
	
	public static AMapLocation parseLocation(String latLonStr) {
		if (latLonStr == null || latLonStr.equals("") || latLonStr.equals("[]")) {
			return null;
		}
		String[] loc = latLonStr.split(",");
		AMapLocation location = null;
		if (loc.length == 6) {
			location = new AMapLocation(loc[2]);
			location.setProvider(loc[2]);
			location.setLatitude(Double.parseDouble(loc[0]));
			location.setLongitude(Double.parseDouble(loc[1]));
			location.setTime(Long.parseLong(loc[3]));
			location.setSpeed(Float.parseFloat(loc[4]));
			location.setBearing(Float.parseFloat(loc[5]));
		}else if(loc.length == 2){
			location = new AMapLocation("gps");
			location.setLatitude(Double.parseDouble(loc[0]));
			location.setLongitude(Double.parseDouble(loc[1]));
		}
		
		return location;
	}
	
	public static ArrayList<AMapLocation> parseLocations(String latLonStr) {
		ArrayList<AMapLocation> locations = new ArrayList<AMapLocation>();
		String[] latLonStrs = latLonStr.split(";");
		for (int i = 0; i < latLonStrs.length; i++) {
			AMapLocation location = Util.parseLocation(latLonStrs[i]);
			if (location != null) {
				locations.add(location);
			}
		}
		return locations;
	}

    //保存数据到数据库
   /* public static long saveRecord(List<AMapLocation> list, String time, Context context, long startTime,double allDistance) {
        if (list != null ) {
            long mEndTime = System.currentTimeMillis();
            DbAdapter dbAdapter = new DbAdapter(context);
            dbAdapter.open();
            String duration = getDuration(startTime,mEndTime);
            float distance = getDistance(list);  //室外运动总距离
            //String average = getAverage(distance,startTime,mEndTime);  //室外运动平均速度
            String average = getAverage((float) allDistance,startTime,mEndTime);
            String pathlineSring = getPathLineString(list);
			long createrecord;
			if (list.size()>0){
				AMapLocation firstLocaiton = list.get(0);
				AMapLocation lastLocaiton = list.get(list.size() - 1);
				String stratpoint = amapLocationToString(firstLocaiton);
				String endpoint = amapLocationToString(lastLocaiton);
				createrecord = dbAdapter.createrecord(String.valueOf(allDistance), duration, average, pathlineSring, stratpoint, endpoint, time);
			}
			else{
				createrecord = dbAdapter.createrecord(String.valueOf(allDistance), duration, average, pathlineSring, "", "", time);
			}
            //Log.i(TAG,"createrecord:"+createrecord);
            dbAdapter.close();
            return createrecord;
        } else {
            *//*Toast.makeText(RunTrailMapActivity.this, "没有记录到路径", Toast.LENGTH_SHORT)
                    .show();*//*
            return -1;
        }
    }*/

	//保存数据到数据库
	public static long saveOrUdateRecord(List<AMapLocation> list, long addDuration, String time, Context context, long startTime,double allDistance,long id) {
		if (list != null ) {
			long mEndTime = System.currentTimeMillis();
			DbAdapter dbAdapter = new DbAdapter(context);
			dbAdapter.open();
			String duration = getDuration(startTime,mEndTime,addDuration);

			Log.i(TAG,"startTime:"+startTime);
			Log.i(TAG,"mEndTime:"+mEndTime);
			Log.i(TAG,"duration:"+duration);
			float distance = getDistance(list);  //室外运动总距离
			//String average = getAverage(distance,startTime,mEndTime);  //室外运动平均速度
			String average = getAverage((float) allDistance,startTime,mEndTime,addDuration);
			String pathlineSring = getPathLineString(list);
			long createrecord;
			if (list.size()>0){
				AMapLocation firstLocaiton = list.get(0);
				AMapLocation lastLocaiton = list.get(list.size() - 1);
				String stratpoint = amapLocationToString(firstLocaiton);
				String endpoint = amapLocationToString(lastLocaiton);
				createrecord = dbAdapter.saveOrUdateRecord(String.valueOf(allDistance), duration, average, pathlineSring, stratpoint, endpoint, time,id);
			}
			else{
				createrecord = dbAdapter.saveOrUdateRecord(String.valueOf(allDistance), duration, average, pathlineSring, "", "", time,id);
			}
			//Log.i(TAG,"createrecord:"+createrecord);
			dbAdapter.close();
			return createrecord;
		} else {
            /*Toast.makeText(RunTrailMapActivity.this, "没有记录到路径", Toast.LENGTH_SHORT)
                    .show();*/
			return -1;
		}
	}

    public static String getDuration(long mStartTime,long mEndTime,long addDuration) {
        return String.valueOf((addDuration+mEndTime - mStartTime) );
    }

    public static float getDistance(List<AMapLocation> list) {
		LatLng firstLatLng;
		LatLng secondLatLng;
		double betweenDis;

		float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            firstLatLng = new LatLng(firstpoint.getLatitude(), firstpoint.getLongitude());
            secondLatLng = new LatLng(secondpoint.getLatitude(), secondpoint.getLongitude());
            betweenDis = AMapUtils.calculateLineDistance(firstLatLng, secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

	public static float getDistanceByLatLng(List<LatLng> list) {
		float distance = 0;
		if (list == null || list.size() == 0) {
			return distance;
		}
		for (int i = 0; i < list.size() - 1; i++) {
			LatLng firstLatLng = list.get(i);
			LatLng secondLatLng = list.get(i + 1);
			double betweenDis = AMapUtils.calculateLineDistance(firstLatLng, secondLatLng);
			distance = (float) (distance + betweenDis);
		}
		return distance;
	}

    public static String getAverage(float distance,long mStartTime,long mEndTime,long addDuration) {
		// pathRecord:recordSize:103, distance:4.15064m, duration:206.922s
		//需要速度格式：km/h
		float km = distance / 1000f;
		float time = (addDuration+mEndTime - mStartTime) / (1000*60 * 60f);
		float speed = km / time;
		DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String formatSpeed=decimalFormat.format(speed);//format 返回的是字符串
		return formatSpeed;
    }

	public static String getAverage(float distance,long allScend) {
		// pathRecord:recordSize:103, distance:4.15064m, duration:206.922s
		//需要速度格式：km/h
		float km = distance / 1000f;
		float time = allScend/ (60 * 60f);
		float speed = km / time;
		DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String formatSpeed=decimalFormat.format(speed);//format 返回的是字符串
		return formatSpeed;
	}

    public static String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    public static String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }
}
