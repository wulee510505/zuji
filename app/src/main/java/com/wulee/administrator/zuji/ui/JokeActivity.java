package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
import com.huxq17.swipecardsview.SwipeCardsView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.FunPicAdapter;
import com.wulee.administrator.zuji.adapter.JokeAdapter;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.entity.FunPicInfo;
import com.wulee.administrator.zuji.entity.FunPicUrl;
import com.wulee.administrator.zuji.entity.JokeInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import okhttp3.Headers;


/**
 * Created by wulee on 2017/9/4 17:04
 */

public class JokeActivity extends BaseActivity {

    @InjectView(R.id.swipCardsView)
    SwipeCardsView swipCardsView;
    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;
    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.title_left)
    TextView titleLeft;
    @InjectView(R.id.title_right)
    TextView titleRight;

    private JokeAdapter mAdapter;
    private FunPicAdapter mPicAdapter;

    private List<JokeInfo> mJokecDatas = new ArrayList<>();
    List<FunPicInfo> mJokePicDatas = new ArrayList<>();

    private  final int TYPE_JOKE_TEXT = 2;
    private  final int TYPE_JOKE_PIC= 3;
    private  int jokeType = TYPE_JOKE_TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.joke_main);
        ButterKnife.inject(this);

        initView();
        getJokeText();
    }

    private void initView() {
        swipCardsView.retainLastCard(true);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            HttpRequest.get((String) msg.obj, new BaseHttpRequestCallback() {
                //请求网络前
                @Override
                public void onStart() {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onResponse(String response, Headers headers) {
                    super.onResponse(response, headers);

                    if(jokeType == TYPE_JOKE_TEXT){
                        mJokecDatas.clear();
                        mJokecDatas.addAll(jsonParse(response));
                        mAdapter = new JokeAdapter(mJokecDatas, JokeActivity.this);
                        swipCardsView.setAdapter(mAdapter);
                    }else{
                        mJokePicDatas.clear();
                        mJokePicDatas.addAll(processJokePicInfo(jsonParse(response)));
                        mPicAdapter = new FunPicAdapter(mJokePicDatas, JokeActivity.this);
                        swipCardsView.setAdapter(mPicAdapter);
                    }
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


    private String getJokeText() {
        final String[] defUrl = {""};
        BmobQuery<FunPicUrl> query = new BmobQuery<>();
        query.findObjects(new FindListener<FunPicUrl>() {
            @Override
            public void done(List<FunPicUrl> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        String url = list.get(1).getUrl();
                        if (!TextUtils.isEmpty(url))
                            defUrl[0] = url;

                        Message msg = new Message();
                        msg.obj = defUrl[0];

                        mHandler.sendMessage(msg);
                    }
                }
            }
        });
        return defUrl[0];
    }

    private String getJokePic() {
        final String[] defUrl = {""};
        BmobQuery<FunPicUrl> query = new BmobQuery<>();
        query.findObjects(new FindListener<FunPicUrl>() {
            @Override
            public void done(List<FunPicUrl> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        String url = list.get(2).getUrl();
                        if (!TextUtils.isEmpty(url))
                            defUrl[0] = url;

                        Message msg = new Message();
                        msg.obj = defUrl[0];

                        mHandler.sendMessage(msg);
                    }
                }
            }
        });
        return defUrl[0];
    }


    /**
     * 从网络中获取JSON字符串，然后解析
     *
     * @param json
     * @return
     */
    private List<JokeInfo> jsonParse(String json) {
        try {
            List<JokeInfo> jokelist = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            int errorCode = jsonObject.getInt("error_code");
            if (errorCode == 0) {
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JokeInfo joke = new JokeInfo();
                    JSONObject picData = jsonArray.getJSONObject(i);
                    String id = picData.getString("hashId");
                    String content = picData.getString("content");
                    String url = "";
                    if(jokeType == TYPE_JOKE_PIC){
                         url = picData.optString("url");
                    }
                    joke.setHashId(id);
                    joke.setContent(content);
                    joke.setUrl(url);
                    jokelist.add(joke);
                }
                return jokelist;
            } else {
                toast("获取数据失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e("JsonParseActivity", "json解析出现了问题");
        }
        return null;
    }

    private List<FunPicInfo>  processJokePicInfo(List<JokeInfo> list){
        List<FunPicInfo> jokePicDatas = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JokeInfo joke  = list.get(i);

            FunPicInfo picInfo = new FunPicInfo();
            picInfo.setUrl(joke.getUrl());
            picInfo.set_id(joke.getHashId());

            jokePicDatas.add(picInfo);
        }
        return  jokePicDatas;
    }


    @OnClick({R.id.iv_back, R.id.title_left, R.id.title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.title_left:
                jokeType = TYPE_JOKE_TEXT;
                getJokeText();
                break;
            case R.id.title_right:
                jokeType = TYPE_JOKE_PIC;
                getJokePic();
                break;
        }
    }
}
