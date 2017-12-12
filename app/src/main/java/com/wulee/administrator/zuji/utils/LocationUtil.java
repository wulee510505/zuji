package com.wulee.administrator.zuji.utils;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.liangmayong.text2speech.Text2Speech;
import com.wulee.administrator.zuji.App;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.LocationInfo;
import com.wulee.administrator.zuji.database.bean.PersonInfo;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.wulee.administrator.zuji.App.aCache;
import static com.wulee.administrator.zuji.utils.PhoneUtil.getDeviceBrand;
import static com.wulee.administrator.zuji.utils.PhoneUtil.getNativePhoneNumber;
import static com.wulee.administrator.zuji.utils.PhoneUtil.getSystemModel;

/**
 * Created by wulee on 2016/12/8 09:35
 */
public class LocationUtil{
    private static LocationUtil mLocationUtil = null;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    public static final String ACTION_LOCATION_CHANGE = "action_location_change";

    private LocationUtil() {
        mLocationClient = new LocationClient(App.context);     //声明LocationClient类
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span= 1000 * 60 * 2; // 2分钟
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
    }

    public static LocationUtil getInstance() {
        if (null == mLocationUtil) {
            mLocationUtil = new LocationUtil();
        }
        return mLocationUtil;
    }


    public void startGetLocation() {
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    public void stopGetLocation() {
        mLocationClient.stop();
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            if(!TextUtils.isEmpty(location.getCity()))
               aCache.put("location_city",location.getCity());

            if(location.getLatitude() == 0 || location.getLongitude()  == 0)
                return;

            Log.i("location","获取位置信息成功---->"+ DateTimeUtils.getFullTime(System.currentTimeMillis()));
            if(!TextUtils.isEmpty(aCache.getAsString("lat")) &&!TextUtils.isEmpty(aCache.getAsString("lon"))  ){
                if(OtherUtil.equal(location.getLatitude(),Double.parseDouble(aCache.getAsString("lat"))) && OtherUtil.equal(location.getLongitude(),Double.parseDouble(aCache.getAsString("lon"))))//避免上传相同的位置到云端
                    return;

                double lastlat = Double.parseDouble(aCache.getAsString("lat"));
                double lastlon = Double.parseDouble(aCache.getAsString("lon"));

                if(lastlat > 0 && lastlon >0){
                    LatLng lastLatLng = new LatLng(lastlat,lastlon);
                    boolean isContains  = SpatialRelationUtil.isCircleContainsPoint(lastLatLng, 5, new LatLng(location.getLatitude(),location.getLongitude()));
                    if(isContains){
                        return;
                    }
                }
            }
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setLatitude(location.getLatitude()+"");
            locationInfo.setLontitude(location.getLongitude()+"");
            locationInfo.setAddress(location.getAddrStr());
            locationInfo.setLocationdescribe(location.getLocationDescribe());

            StringBuilder sbdeviceInfo = new StringBuilder();
            if(!TextUtils.isEmpty(getNativePhoneNumber())){
                locationInfo.setNativePhoneNumber(sbdeviceInfo.append(getDeviceBrand()).append(" ").append(getSystemModel()).append(" ").append(getNativePhoneNumber()).toString());
            }else{
                locationInfo.setNativePhoneNumber(sbdeviceInfo.append(getDeviceBrand()).append(" ").append(getSystemModel()).toString());
            }
            if(!TextUtils.isEmpty(PhoneUtil.getDeviceId()))
                locationInfo.setDeviceId(PhoneUtil.getDeviceId());
            if(!TextUtils.isEmpty(location.getAddrStr()))
                submitLocationInfo(locationInfo);

            App.context.sendBroadcast(new Intent(ACTION_LOCATION_CHANGE).putExtra("curr_location",location));
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("Location", sb.toString());
        }

    }


    private  void submitLocationInfo(final LocationInfo locationInfo){
        if(null == locationInfo)
            return;
        if(!OtherUtil.hasLogin())
            return;
        PersonInfo user = BmobUser.getCurrentUser(PersonInfo.class);
        //添加一对一关联
        locationInfo.piInfo = user;
        locationInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    DBHandler.insertLocationInfo(locationInfo);
                    aCache.put("lat",locationInfo.getLatitude());
                    aCache.put("lon",locationInfo.getLontitude());

                    aCache.put("isUploadLocation","yes");
                    //speak("您当前位置是"+ locationInfo.getAddress() + locationInfo.getLocationdescribe());
                    System.out.println("—— 位置同步成功 ——");
                }else{
                    System.out.println("—— 位置同步失败 ——");
                }
            }
        });
    }

    private void speak(String addrStr) {
        Text2Speech.speech(App.context,addrStr,false);
    }

}
