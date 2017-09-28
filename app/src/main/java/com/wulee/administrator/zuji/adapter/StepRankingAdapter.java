package com.wulee.administrator.zuji.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.StepInfo;
import com.wulee.administrator.zuji.utils.ImageUtil;

import java.util.ArrayList;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class StepRankingAdapter extends BaseQuickAdapter<StepInfo,BaseViewHolder> {

    private Context mcontext;

    public StepRankingAdapter(Context context, int layoutResId, ArrayList<StepInfo> stepList) {
        super(layoutResId, stepList);
        this.mcontext = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final StepInfo stepInfo) {

        PersonInfo piInfo = stepInfo.personInfo;
        if(null != piInfo){
            if(!TextUtils.isEmpty(piInfo.getName()))
                baseViewHolder.setText(R.id.tv_name,piInfo.getName());
            else
                baseViewHolder.setText(R.id.tv_name,"游客");
            ImageView ivHeader = baseViewHolder.getView(R.id.iv_header);
            ImageUtil.setCircleImageView(ivHeader,piInfo.getHeader_img_url(),R.mipmap.icon_user_def,mcontext);
        }else{
            baseViewHolder.setText(R.id.tv_name,"游客");
        }
        baseViewHolder.setText(R.id.tv_step,stepInfo.getCount()+"步");
        baseViewHolder.setText(R.id.tv_ranking,(baseViewHolder.getLayoutPosition()+ 1)+"");

        final TextView tvZanNum = baseViewHolder.getView(R.id.tv_zannum);
        tvZanNum.setText(stepInfo.getZannum() +"");

        ImageView ivZan = baseViewHolder.getView(R.id.iv_zan);
        ivZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stepInfo.setZannum(stepInfo.getZannum()+1);
                stepInfo.update(stepInfo.getObjectId(),new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            tvZanNum.setText(stepInfo.getZannum()+"");
                        }
                    }
                });
            }
        });
    }
}
