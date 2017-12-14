package com.wulee.administrator.zuji.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huxq17.swipecardsview.BaseCardAdapter;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.JokeInfo;
import com.wulee.administrator.zuji.utils.OtherUtil;

import java.util.List;

/**
 * Created by wulee on 2017/9/4 16:23
 */

public class JokeAdapter extends BaseCardAdapter {
    private List<JokeInfo> datas;
    private Context context;

    public JokeAdapter(List<JokeInfo> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.joke_item;
    }

    @Override
    public void onBindData(final int position, View cardview) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        final TextView tvJoke =  cardview.findViewById(R.id.tv_joke);
        TextView tvCpoy =  cardview.findViewById(R.id.tv_copy);
        TextView tvShare =  cardview.findViewById(R.id.tv_share);

        final JokeInfo joke = datas.get(position);
        tvJoke.setText(joke.getContent());

        tvCpoy.setOnClickListener(view -> {
            OtherUtil.copy(joke.getContent(),context);
            Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
        });
        tvShare.setOnClickListener(v -> {
            OtherUtil.shareTextAndImage(context,"",joke.getContent(),null);
        });

    }

    /**
     * 如果可见的卡片数是3，则可以不用实现这个方法
     * @return
     */
    @Override
    public int getVisibleCardCount() {
        return super.getVisibleCardCount();
    }

}
