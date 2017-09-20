package com.wulee.administrator.zuji.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.stetho.common.LogUtil;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.CircleComment;
import com.wulee.administrator.zuji.entity.CircleContent;
import com.wulee.administrator.zuji.ui.BigImageActivity;
import com.wulee.administrator.zuji.ui.PersonalInfoActivity;
import com.wulee.administrator.zuji.ui.UserInfoActivity;
import com.wulee.administrator.zuji.utils.DateTimeUtils;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;


public class CircleContentAdapter extends BaseQuickAdapter<CircleContent> {

    private Context mcontext;
    private PersonInfo piInfo;
    private HashMap<Integer,LinearLayout> viewMap = new HashMap<>();

    public CircleContentAdapter(int layoutResId, ArrayList<CircleContent> dataList,Context context) {
        super(layoutResId, dataList);
        this.mcontext = context;
        piInfo = BmobUser.getCurrentUser(PersonInfo.class);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final CircleContent content) {

        ImageView ivAvatar = baseViewHolder.getView(R.id.userAvatar);
        if(content.personInfo != null && !TextUtils.isEmpty(content.personInfo.getHeader_img_url()))
            ImageUtil.setDefaultImageView(ivAvatar,content.personInfo.getHeader_img_url(),R.mipmap.icon_user_def,mcontext);
        else
            ImageUtil.setDefaultImageView(ivAvatar,"",R.mipmap.icon_user_def,mcontext);


        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != piInfo){
                    Intent intent = null;
                    if(TextUtils.equals(piInfo.getUsername(),content.personInfo.getUsername())){
                        intent = new Intent(mcontext, PersonalInfoActivity.class);
                    }else{
                        intent = new Intent(mcontext, UserInfoActivity.class);
                        intent.putExtra("piInfo",content.personInfo);
                    }
                    mcontext.startActivity(intent);
                }
            }
        });

        baseViewHolder.setText(R.id.userNick,content.getUserNick());
        baseViewHolder.setText(R.id.content , content.getContent());

        TextView tvLocation = baseViewHolder.getView(R.id.location);
        if(!TextUtils.isEmpty(content.getLocation())){
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(content.getLocation());
        }else{
            tvLocation.setVisibility(View.GONE);
        }
        baseViewHolder.setText(R.id.time , DateTimeUtils.showDifferenceTime(DateTimeUtils.parseDateTime(content.getCreatedAt()), System.currentTimeMillis())+"前");

        TextView tvDel = baseViewHolder.getView(R.id.tv_delete);

        if(null != piInfo){
            if(TextUtils.equals(piInfo.getUsername(),content.personInfo.getUsername())){
                tvDel.setVisibility(View.VISIBLE);
            }else{
                tvDel.setVisibility(View.GONE);
            }
        }
        final int pos = baseViewHolder.getAdapterPosition();
        tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mListener != null)
                   mListener.onDelBtnClick(pos-1); //因为有headerview
            }
        });

        final boolean[] isToolbarLikeAndCommentVisible = {false};//喜欢、评论按钮是否显示

        final LinearLayout llLikeAndComment = baseViewHolder.getView(R.id.album_toolbar);
        viewMap.put(pos,llLikeAndComment);

        final RelativeLayout rlLike = baseViewHolder.getView(R.id.toolbarLike);
        ImageView ivOpt = baseViewHolder.getView(R.id.album_opt);
        ivOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isToolbarLikeAndCommentVisible[0]){
                    llLikeAndComment.setVisibility(View.GONE);
                    isToolbarLikeAndCommentVisible[0] = false;
                } else{
                    llLikeAndComment.setVisibility(View.VISIBLE);
                    isToolbarLikeAndCommentVisible[0] = true;
                }
            }
        });

        rlLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(content.getLikeList() != null && content.getLikeList().size()>0){
                    for (PersonInfo likePiInfo : content.getLikeList()){
                        if(TextUtils.equals(piInfo.getUsername(),likePiInfo.getUsername())){
                            llLikeAndComment.setVisibility(View.GONE);
                            Toast.makeText(mcontext, "您已经赞过了", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

               //将当前用户添加到CircleContent表中的likes字段值中，表明当前用户喜欢该帖子
                BmobRelation relation = new BmobRelation();
               //将当前用户添加到多对多关联中
                relation.add(piInfo);
                //多对多关联指向CircleContent的`likes`字段
                content.setLikes(relation);
                content.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        llLikeAndComment.setVisibility(View.GONE);
                        if(e == null){
                            EventBus.getDefault().post(new String("refresh"));
                            LogUtil.i("zuji","喜欢成功");
                        }else{
                            LogUtil.i("zuji","喜欢失败："+e.getMessage());
                        }
                    }
                });
            }
        });
        TextView tvLikes = baseViewHolder.getView(R.id.tv_likes);
        StringBuilder sbLikes = new StringBuilder();
        List<PersonInfo> likePiList = content.getLikeList();
        if(likePiList != null && likePiList.size()>0){
            tvLikes.setVisibility(View.VISIBLE);
            for (int i = 0; i < likePiList.size(); i++) {
                PersonInfo pi = likePiList.get(i);
                if(null != pi){
                    sbLikes.append(pi.getName()).append("，");
                }
            }
            String str = sbLikes.toString();
            if(str.length()>0){
                tvLikes.setText(str.substring(0,str.length()-1));
            }
        }else{
            tvLikes.setVisibility(View.GONE);
        }

        final RelativeLayout rlComment = baseViewHolder.getView(R.id.toolbarComment);
        rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComentDialog(content,llLikeAndComment);
            }
        });

        TextView tvComments = baseViewHolder.getView(R.id.tv_comments);
        StringBuilder sbComment = new StringBuilder();
        List<CircleComment> commentList = content.getCommentList();
        if(commentList != null && commentList.size()>0){
            tvComments.setVisibility(View.VISIBLE);
            for (int i = 0; i < commentList.size(); i++) {
                CircleComment comment = commentList.get(i);
                if(null != comment){
                    sbComment.append(comment.getPersonInfo().getName()).append("：").append(comment.getContent()).append("\n");
                }
            }
            String str = sbComment.toString();
            if(str.length()>0){
                tvComments.setText(str.substring(0,str.length()-1));
            }
        }else{
            tvComments.setVisibility(View.GONE);
        }


        NineGridImageViewAdapter<CircleContent.CircleImageBean> mAdapter = new NineGridImageViewAdapter<CircleContent.CircleImageBean>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, CircleContent.CircleImageBean img) {
                Glide.with(context)
                        .load(img.getUrl())
                        .placeholder(R.mipmap.bg_pic_def_rect)
                        .into(imageView);
            }
            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }
            @Override
            protected void onItemImageClick(Context context, ImageView imageView, int index, List<CircleContent.CircleImageBean> photoList) {

            }
        };
        NineGridImageView nineGridImageView = baseViewHolder.getView(R.id.nine_grid_view);
        nineGridImageView.setAdapter(mAdapter);
        nineGridImageView.setImagesData(content.getImageList());
        nineGridImageView.setItemImageClickListener(new ItemImageClickListener<CircleContent.CircleImageBean>() {
            @Override
            public void onItemImageClick(Context context, ImageView imageView, int index, List<CircleContent.CircleImageBean> imgList) {

                if(imgList != null && imgList.size()>0){
                    Intent intent = new Intent(context, BigImageActivity.class);
                    intent.putExtra(BigImageActivity.IMAGE_URL,imgList.get(index).getUrl());
                    context.startActivity(intent);
                }
            }
        });
    }

    //评论Dialog
    private void showComentDialog(final CircleContent content,final View likeAndCommentView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("评论");
        RelativeLayout rlContainer = new RelativeLayout(mcontext);
        final EditText etComment = new EditText(mcontext);
        etComment.setPadding(10,5,10,5);
        etComment.setBackgroundResource(R.drawable.bg_text_rec);
        rlContainer.addView(etComment);

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) etComment.getLayoutParams();
        rlp.leftMargin = UIUtils.dip2px(15);
        rlp.rightMargin = UIUtils.dip2px(15);
        rlp.topMargin = UIUtils.dip2px(10);
        rlp.width = UIUtils.getScreenWidthAndHeight(mcontext)[0]- UIUtils.dip2px(30)*2;
        rlp.height = UIUtils.dip2px(120);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        etComment.setLayoutParams(rlp);

        builder.setView(rlContainer);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final CircleComment comment = new CircleComment();
                comment.setContent(etComment.getText().toString().trim());
                comment.setCircleContent(content);
                comment.setPersonInfo(piInfo);
                comment.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId,BmobException e) {
                        likeAndCommentView.setVisibility(View.GONE);
                        if(e == null){
                            EventBus.getDefault().post(new String("refresh"));
                            LogUtil.i("zuji","评论发表成功");
                        }else{
                            LogUtil.i("zuji","评论失败："+e.getMessage());
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void setLikeAndCommentViewGone(){
        for (int i = 0; i < getData().size(); i++) {
            LinearLayout llLikeAndComment =  viewMap.get(i);
            if(llLikeAndComment != null){
                if(llLikeAndComment.getVisibility() == View.VISIBLE){
                    llLikeAndComment.setVisibility(View.GONE);
                }
            }
        }
    }


    public void setDelBtnClickListenerListener(OnDelBtnClickListener mListener) {
        this.mListener = mListener;
    }
    private OnDelBtnClickListener mListener;
    public interface OnDelBtnClickListener{
        void onDelBtnClick(int postion);
    }
}
