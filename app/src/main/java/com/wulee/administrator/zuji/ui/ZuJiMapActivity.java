package com.wulee.administrator.zuji.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.facebook.stetho.common.LogUtil;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.database.bean.LocationInfo;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by wulee on 2017/3/15 11:47
 */

public class ZuJiMapActivity extends AppCompatActivity implements BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener{


    public static final String ACTION_LOCATION_CHANGE = "action_location_change";
    private  final int INTENT_SWITCH_MAP_TYPE = 100;

    private BaseTitleLayout titleLayout;
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private ImageView ivSwitch;

    private List<LatLng> locationList;
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

    private boolean isTrafficEnabled = false;  //是否开启交通图
    private boolean isHeatMapEnabled = false;   //是否开启城市热力图


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.zuji_map);

        initView();
        addListener();
        queryData();
    }

    private void addListener() {
        titleLayout.setOnTitleClickListener(new TitleLayoutClickListener() {
            @Override
            public void onLeftClickListener() {
                finish();
            }
        });
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);
    }

    private void initView() {
        titleLayout = findViewById(R.id.titlelayout);
        mapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();


        ivSwitch = (ImageView) findViewById(R.id.iv_map_type_switch);
        ivSwitch.setOnClickListener(view -> startActivityForResult(new Intent(ZuJiMapActivity.this,SwitchMapTypeActivity.class),INTENT_SWITCH_MAP_TYPE));
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
        //query.setLimit(50);
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
                    LogUtil.d("查询LocationInfo失败"+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void addLocation(List<LocationInfo> dataList) {
        LatLng lastLocation = null;
        locationList = new ArrayList<>();
        LocationInfo.SortClass sort = new LocationInfo.SortClass();
        Collections.sort(dataList,sort);
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
            locationList.add(point);
        }
       /* if(locationList.size()>3){
            OverlayOptions ooPolygon = new PolygonOptions().points(locationList)
                    .stroke(new Stroke(2, 0xAA00FF00)).fillColor(0x00FFFFFF);
            mBaiduMap.addOverlay(ooPolygon);
        }*/
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(lastLocation).zoom(16.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // 获得marker中的数据
        LocationInfo location = (LocationInfo) marker.getExtraInfo().get("info");
        View popView = LayoutInflater.from(this).inflate(R.layout.map_mark_click_pop,null);
        TextView tvTime = popView.findViewById(R.id.tv_1);
        tvTime.setText(location.getUpdatedAt());
        TextView tvLocation = popView.findViewById(R.id.tv_2);
        tvLocation.setText(location.getAddress());
        // 将marker所在的经纬度的信息转化成屏幕上的坐标
        final LatLng ll = marker.getPosition();
        Point p = mBaiduMap.getProjection().toScreenLocation(ll);
        p.y -= 47;
        LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
        InfoWindow window = new InfoWindow(popView,llInfo,-20);
        // 显示InfoWindow
        mBaiduMap.showInfoWindow(window);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // 隐藏InfoWindow
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            switch (requestCode){
                case INTENT_SWITCH_MAP_TYPE:
                      int type = data.getIntExtra(SwitchMapTypeActivity.MAP_TYPE,-1);
                      switch (type){
                          case SwitchMapTypeActivity.TYPE_NORMAL:
                              //普通地图
                              mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                              break;
                          case SwitchMapTypeActivity.TYPE_SATELLITE:
                              //卫星地图
                              mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                              break;
                          case SwitchMapTypeActivity.TYPE_TRAFFIC:
                              if(isTrafficEnabled){
                                  //关闭交通图
                                  isTrafficEnabled = false;
                                  mBaiduMap.setTrafficEnabled(false);
                              }else{
                                  //开启交通图
                                  isTrafficEnabled = true;
                                  mBaiduMap.setTrafficEnabled(true);
                              }
                              break;
                          case SwitchMapTypeActivity.TYPE_HEATMAP:
                              if(isHeatMapEnabled){
                                  //关闭城市热力图
                                  isHeatMapEnabled = false;
                                  mBaiduMap.setBaiduHeatMapEnabled(false);
                              }else{
                                  //开启城市热力图
                                  isHeatMapEnabled = true;
                                  mBaiduMap.setBaiduHeatMapEnabled(true);
                              }
                              break;
                      }
                 break;
            }
        }
    }
}
