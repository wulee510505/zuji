package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.entity.Notice;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.wulee.administrator.zuji.App.aCache;

/**
 * Created by wulee on 2017/10/10 14:27
 */

public class NoticeActivity extends BaseActivity {

    @InjectView(R.id.iv_close)
    ImageView ivClose;
    @InjectView(R.id.tv_content)
    TextView tvContent;
    @InjectView(R.id.container)
    RelativeLayout container;

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notice_activity);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        BmobQuery<Notice> query = new BmobQuery<>();
        query.findObjects(new FindListener<Notice>() {
            @Override
            public void done(List<Notice> list, BmobException e) {
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        Notice notice = list.get(0);
                        if (null != notice && !TextUtils.isEmpty(notice.getContent())) {
                            container.setVisibility(View.VISIBLE);
                            aCache.put(Constant.KEY_LAST_SHOW_NOTICE_TIME, String.valueOf(System.currentTimeMillis()));
                            tvContent.setText(notice.getContent());
                        } else {
                            container.setVisibility(View.GONE);
                            finish();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected int getStateBarColor() {
        return R.color.color_transparent;
    }

    @OnClick(R.id.iv_close)
    public void onViewClicked() {
        finish();
    }

    /**
     * 屏蔽返回键
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    /**
     * 屏蔽Home键
     */
    @Override
    public void onAttachedToWindow() {
        //关键：在onAttachedToWindow中设置FLAG_HOMEKEY_DISPATCHED
        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
        super.onAttachedToWindow();
    }
}

