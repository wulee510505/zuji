package com.wulee.administrator.zuji.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.utils.ConfigKey;
import com.wulee.administrator.zuji.utils.PhoneUtil;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

import static com.wulee.administrator.zuji.App.aCache;

/**
 * Created by wulee on 2017/12/12 11:58
 * 附近的人
 */

public class NearUserActivity extends BaseActivity implements RadarSearchListener,BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener {
    private static final String TAG = "NearUserActivity";

    private BaseTitleLayout titleLayout;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private RadarSearchManager mManager;

    private LatLng currPt;
    private String currLatitude;
    private String currLontitude;

    private List<RadarNearbyInfo> infoList;

    private final int MSG_RADAR_SEARCH_OK = 100;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_RADAR_SEARCH_OK:
                    addLocation(infoList);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_user_map);
        // 初始化周边雷达
        mManager = RadarSearchManager.getInstance();
        initView();
        addListener();
        radarInfoUpload();
    }

    private void initView() {
        titleLayout= findViewById(R.id.titlelayout);
        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void addListener() {
        titleLayout.setOnTitleClickListener(new TitleLayoutClickListener() {
            @Override
            public void onLeftClickListener() {
                finish();
            }
        });
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
    }


    /** 周边雷达信息上传 */
    private void radarInfoUpload() {
        // 周边雷达设置监听
        mManager.addNearbyInfoListener(this);
        //周边雷达设置用户身份标识，id为空默认是设备标识
        String mobile  = aCache.getAsString(ConfigKey.KEY_CURR_LOGIN_MOBILE);
        mManager.setUserID(mobile);
        //上传位置
        RadarUploadInfo info = new RadarUploadInfo();
        currLatitude = aCache.getAsString("lat");
        currLontitude = aCache.getAsString("lon");
        try {
            currPt = new LatLng(Double.parseDouble(currLatitude), Double.parseDouble(currLontitude));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        PersonInfo personInfo = BmobUser.getCurrentUser(PersonInfo.class);
        info.comments = personInfo != null ? personInfo.getName() : "";
        info.pt = currPt;
        mManager.uploadInfoRequest(info);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(currPt).zoom(16.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 雷达周边位置检索 默认查询自己周围5公里的用户
     * */
    private void radarLocationSearch() {
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(currPt)
                .pageNum(0)
                .radius(5*1000);
        // 发起查询请求
        mManager.nearbyInfoRequest(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        //移除监听
        mManager.removeNearbyInfoListener(this);
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //释放资源
        mManager.destroy();
        mManager = null;
    }


    private void addLocation(List<RadarNearbyInfo> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            RadarNearbyInfo nearInfo = dataList.get(i);
            //定义Maker坐标点
            LatLng point = new LatLng(nearInfo.pt.latitude, nearInfo.pt.longitude);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_user_mark);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            Marker marker = (Marker) mBaiduMap.addOverlay(option);
            //为marker添加识别标记信息
            Bundle bundle = new Bundle();
            PersonInfo piInfo = new PersonInfo();
            piInfo.setMobile(nearInfo.userID);
            piInfo.setName(nearInfo.comments);
            bundle.putSerializable("info", piInfo);
            marker.setExtraInfo(bundle);
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        // 获得marker中的数据
        PersonInfo personInfo = (PersonInfo) marker.getExtraInfo().get("info");
        View popView = LayoutInflater.from(this).inflate(R.layout.map_mark_click_pop,null);
        TextView tvMobile = popView.findViewById(R.id.tv_1);
        tvMobile.setText(PhoneUtil.encryptTelNum(personInfo.getMobile()));
        TextView tvName = popView.findViewById(R.id.tv_2);
        tvName.setText(personInfo.getName());
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

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            infoList = new ArrayList<>();
            infoList.addAll(radarNearbyResult.infoList);

            handler.sendEmptyMessage(MSG_RADAR_SEARCH_OK);

            Log.i(TAG,radarNearbyResult.infoList.get(0).userID+"\n"+
                    radarNearbyResult.infoList.get(0).distance+"\n"+
                    radarNearbyResult.infoList.get(0).pt+"\n"+
                    radarNearbyResult.infoList.get(0).timeStamp);
        } else {
            toast("查询周边失败");
            Log.i(TAG,"查询错误："+radarSearchError.toString());
        }
    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            //上传成功
            Log.i(TAG,"单次上传位置成功");

            radarLocationSearch();
        } else {
            //上传失败
            Log.i(TAG,"单次上传位置失败："+radarSearchError.toString());
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            //清除成功
            Log.i(TAG,"清除位置成功");
        } else {
            //清除失败
            Log.i(TAG,"清除位置失败");
        }
    }
}
