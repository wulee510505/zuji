package com.wulee.administrator.zuji.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.LocationInfo;
import com.wulee.administrator.zuji.database.bean.PersonInfo;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by wulee on 2017/3/15 11:47
 */

public class ZuJiMapActivity extends BaseActivity implements BaiduMap.OnMarkerClickListener{


    public static final String ACTION_LOCATION_CHANGE = "action_location_change";

    private MapView mapView;
    private BaiduMap mBaiduMap;

    private List<LocationInfo> zujiList;
    private final int MSG_QUERY_ZUJI_DATA_OK = 1000;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_QUERY_ZUJI_DATA_OK:
                    List<LocationInfo> list = (List<LocationInfo>) msg.obj;
                    addLocation(list);
                 break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.zuji_map);

        initView();
        queryData();
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setOnMarkerClickListener(this);
    }


    /**
     * 查询数据
     */
    private void queryData(){
        if(!TextUtils.equals("yes",aCache.getAsString("isUploadLocation"))){
            return;
        }
        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        BmobQuery<LocationInfo> query = new BmobQuery<LocationInfo>();
        query.addWhereEqualTo("piInfo", piInfo);    // 查询当前用户的所有位置信息
        query.include("piInfo");// 希望在查询位置信息的同时也把当前用户的信息查询出来
        query.order("-createdAt");
        // 设置每页数据个数
        query.setLimit(50);
        query.findObjects(new FindListener<LocationInfo>() {
            @Override
            public void done(List<LocationInfo> dataList, BmobException e) {
                if(e == null){
                    if(dataList != null && dataList.size()>0){
                      Message msg = new Message();
                      msg.what = MSG_QUERY_ZUJI_DATA_OK;
                      msg.obj =   dataList;
                      mHandler.sendMessage(msg);
                    }
                }else{
                    Toast.makeText(ZuJiMapActivity.this,"查询失败"+e.getMessage()+","+e.getErrorCode(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addLocation(List<LocationInfo> dataList) {
        LatLng lastLocation = null;
        for (int i = 0; i < dataList.size(); i++) {
            LocationInfo location = dataList.get(i);
            //定义Maker坐标点
            LatLng point = new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLontitude()));
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_mark);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            //为marker添加识别标记信息
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", location);
            marker.setExtraInfo(bundle);
            if(i == 0){
                lastLocation = new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLontitude()));
            }
        }
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(lastLocation).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // 获得marker中的数据
        LocationInfo location = (LocationInfo) marker.getExtraInfo().get("info");
        // 生成一个TextView用户在地图中显示InfoWindow
        TextView tvLocation = new TextView(getApplicationContext());
        tvLocation.setBackgroundResource(R.color.light_red);
        tvLocation.setPadding(15, 15, 8, 35);
        tvLocation.setTextColor(Color.WHITE);
        tvLocation.setText(location.getUpdatedAt() + "\n" + location.getAddress());
        tvLocation.setTextSize(14);
        // 将marker所在的经纬度的信息转化成屏幕上的坐标
        final LatLng ll = marker.getPosition();
        Point p = mBaiduMap.getProjection().toScreenLocation(ll);
        p.y -= 47;
        LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
        InfoWindow window = new InfoWindow(tvLocation,llInfo,-20);
        // 显示InfoWindow
        mBaiduMap.showInfoWindow(window);
        return true;
    }


    public class LocationChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            if (action.equals(ACTION_LOCATION_CHANGE)) {
                String currLatitude = aCache.getAsString("lat");
                String currLontitude = aCache.getAsString("lon");
                if(!TextUtils.isEmpty(currLatitude) && !TextUtils.isEmpty(currLontitude)){

                    BDLocation currLocation = new BDLocation();
                    currLocation.setLatitude(Double.parseDouble(currLatitude));
                    currLocation.setLongitude(Double.parseDouble(currLontitude));
                    // 开启定位图层
                    mBaiduMap.setMyLocationEnabled(true);
                    // 构造定位数据
                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(currLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(100).latitude(currLocation.getLatitude())
                            .longitude(currLocation.getLongitude()).build();
                    // 设置定位数据
                    mBaiduMap.setMyLocationData(locData);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

}
