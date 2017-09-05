package com.wulee.administrator.zuji.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huxq17.swipecardsview.BaseCardAdapter;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.JokeInfo;

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
        final TextView tvJoke = (TextView) cardview.findViewById(R.id.tv_joke);
        TextView tvCpoy = (TextView) cardview.findViewById(R.id.tv_copy);

        final JokeInfo joke = datas.get(position);
        tvJoke.setText(joke.getContent());

        tvCpoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copy(joke.getContent(),context);
                Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
            }
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

    /**
     * 实现文本复制功能
     * add by wangqianzhou
     * @param content
     */
    private   void copy(String content, Context context){
       // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
}
