package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wulee on 2017/5/25 15:11
 */

public class SwitchMapTypeActivity extends AppCompatActivity {

    public static final String MAP_TYPE = "map_type";

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_SATELLITE = 2;
    public static final int TYPE_TRAFFIC = 3;
    public static final int TYPE_HEATMAP = 4;

    @InjectView(R.id.tv_normal)
    TextView tvNormal;
    @InjectView(R.id.tv_satellite)
    TextView tvSatellite;
    @InjectView(R.id.tv_traffic)
    TextView tvTraffic;
    @InjectView(R.id.tv_heatmap)
    TextView tvHeatmap;

    private int selMapType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.switch_map_type);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.tv_normal, R.id.tv_satellite, R.id.tv_traffic, R.id.tv_heatmap})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_normal:
                  selMapType = TYPE_NORMAL;
                break;
            case R.id.tv_satellite:
                selMapType = TYPE_SATELLITE;
                break;
            case R.id.tv_traffic:
                selMapType = TYPE_TRAFFIC;
                break;
            case R.id.tv_heatmap:
                selMapType = TYPE_HEATMAP;
                break;
        }
        Intent intent = getIntent();
        intent.putExtra(MAP_TYPE,selMapType);
        setResult(RESULT_OK,intent);
        finish();
    }
}
