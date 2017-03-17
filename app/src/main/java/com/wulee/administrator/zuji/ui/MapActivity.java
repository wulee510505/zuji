package com.wulee.administrator.zuji.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.utils.LocationUtil;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by wulee on 2017/1/11 11:47
 */

public class MapActivity extends BaseActivity {

    public static final String INTENT_KEY_LONTITUDE = "intent_key_lontitude";
    public static final String INTENT_KEY_LATITUDE = "intent_key_latitude";

    public static final String ACTION_LOCATION_CHANGE = "action_location_change";

    private MapView mapView;
    private BaiduMap mBaiduMap;

    private String mLatitude;
    private String mLontitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_map);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mLatitude = getIntent().getStringExtra(INTENT_KEY_LATITUDE);
        mLontitude = getIntent().getStringExtra(INTENT_KEY_LONTITUDE);

        initView();
        addLocation();
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mapView.getLayoutParams();
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        rlp.height = height  / 2;
        mapView.setLayoutParams(rlp);
    }

    private void addLocation() {
        //定义Maker坐标点
        LatLng point = new LatLng(Double.parseDouble(mLatitude), Double.parseDouble(mLontitude));
       //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_mark);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(point).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
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
        // 退出时销毁定位
        LocationUtil.getInstance().stopGetLocation();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

}
