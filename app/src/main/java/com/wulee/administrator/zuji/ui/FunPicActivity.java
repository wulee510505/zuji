package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.stetho.common.LogUtil;
import com.huxq17.swipecardsview.SwipeCardsView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.FunPicAdapter;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.entity.FunPicInfo;
import com.wulee.administrator.zuji.entity.FunPicUrl;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import okhttp3.Headers;


/**
 * Created by wulee on 2017/9/1 16:04
 */

public class FunPicActivity extends BaseActivity {

    @InjectView(R.id.titlelayout)
    BaseTitleLayout titlelayout;
    @InjectView(R.id.swipCardsView)
    SwipeCardsView swipCardsView;
    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;


    private FunPicAdapter mAdapter;

    private List<FunPicInfo> picDatas = new ArrayList<>();

    private String picUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fun_picture_main);
        ButterKnife.inject(this);

        initView();
        addListener();
        getFunPic();
    }

    private void initView() {
        swipCardsView.retainLastCard(true);
    }

    private void addListener() {
        titlelayout.setOnTitleClickListener(new TitleLayoutClickListener() {
            @Override
            public void onLeftClickListener() {
                super.onLeftClickListener();
                finish();
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            picUrl  = (String)msg.obj;
            if(TextUtils.isEmpty(picUrl)){
                return;
            }
            HttpRequest.get(picUrl, new BaseHttpRequestCallback() {
                //请求网络前
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onResponse(String response, Headers headers) {
                    super.onResponse(response, headers);

                    if(TextUtils.isEmpty(response)){
                        return;
                    }
                    picDatas.clear();
                    picDatas.addAll(jsonParse(response));
                    mAdapter = new FunPicAdapter(picDatas,FunPicActivity.this);
                    swipCardsView.setAdapter(mAdapter);
                }

                //请求失败（服务返回非法JSON、服务器异常、网络异常）
                @Override
                public void onFailure(int errorCode, String msg) {
                    toast("网络异常~，请检查你的网络是否连接后再试");
                }
                //请求网络结束
                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    };


    private String getFunPic(){
        final String[] defUrl = {"http://gank.io/api/data/福利/500/1"};
        BmobQuery<FunPicUrl> query = new BmobQuery<>();
        query.findObjects(new FindListener<FunPicUrl>() {
            @Override
            public void done(List<FunPicUrl> list, BmobException e) {
                if(e == null){
                    if(list != null && list.size()>0){
                        String url = list.get(0).getUrl();
                        if(!TextUtils.isEmpty(url))
                            defUrl[0] = url;

                        Message msg = new Message();
                        msg.obj = defUrl[0];

                        mHandler.sendMessage(msg);
                    }
                }
            }
        });
        return  defUrl[0];
    }


    /**
     * 从网络中获取JSON字符串，然后解析
     * @param json
     * @return
     */
    private List<FunPicInfo> jsonParse(String json) {
        try {
            List<FunPicInfo> piclist = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            boolean error = jsonObject.getBoolean("error");
            if (error == false) {
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    FunPicInfo funPic = new FunPicInfo();
                    JSONObject picData = jsonArray.getJSONObject(i);
                    String id = picData.getString("_id");
                    String url = picData.getString("url");
                    String who = picData.getString("who");

                    funPic.set_id(id);
                    funPic.setUrl(url);
                    funPic.setWho(who);
                    piclist.add(funPic);
                }
                return piclist;
            } else {
                toast("获取数据失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e("JsonParseActivity", "json解析出现了问题");
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!TextUtils.isEmpty(picUrl)){
            HttpRequest.cancel(picUrl);
        }
    }
}
