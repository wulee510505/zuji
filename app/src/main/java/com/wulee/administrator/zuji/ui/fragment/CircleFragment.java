package com.wulee.administrator.zuji.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.LogUtil;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.CircleContentAdapter;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.CircleComment;
import com.wulee.administrator.zuji.entity.CircleContent;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.ui.LoginActivity;
import com.wulee.administrator.zuji.ui.PersonalInfoActivity;
import com.wulee.administrator.zuji.ui.PublishCircleActivity;
import com.wulee.administrator.zuji.utils.AppUtils;
import com.wulee.administrator.zuji.utils.FileProvider7;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.LocationUtil;
import com.wulee.administrator.zuji.utils.OtherUtil;
import com.wulee.administrator.zuji.widget.RecycleViewDivider;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

import static com.wulee.administrator.zuji.App.aCache;

/**
 * Created by wulee on 2017/9/6 09:52
 */
public class CircleFragment extends MainBaseFrag {


    @InjectView(R.id.recyclerview)
    EasyRecyclerView recyclerview;
    @InjectView(R.id.swipeLayout)
    SwipeRefreshLayout swipeLayout;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.iv_publish_circle)
    ImageView ivPublishCircle;
    private View mRootView;
    private AppCompatImageView ivHeaderBg;

    private Context mContext;

    private CircleContentAdapter mAdapter;

    private ArrayList<CircleContent> circleContentList = new ArrayList<>();

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int PAGE_SIZE = 10;
    private int curPage = 0;
    private boolean isRefresh = false;
    private boolean isPullToRefresh = false;

    private String circleHeaderBgUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.circle_main, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        ButterKnife.inject(this, mRootView);

        initView();
        addListener();
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    private void initView() {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.circle_list_header, null);
        ivHeaderBg =  headerView.findViewById(R.id.iv_header_bg);
        ivHeaderBg.setOnLongClickListener(view -> {
            TakePhoto takePhoto = getTakePhoto();
            File file = new File(Constant.TEMP_FILE_PATH, "circle_header_bg" + ".jpg");
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
            Uri imageUri =  FileProvider7.getUriForFile(mContext, file);
            CropOptions cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();
            takePhoto.onPickFromGalleryWithCrop(imageUri, cropOptions);

            return false;
        });

        ImageView ivUserAvatar = headerView.findViewById(R.id.userAvatar);
        TextView tvNick =  headerView.findViewById(R.id.userNick);
        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        if (null != piInfo) {
            if (!TextUtils.isEmpty(piInfo.getName()))
                tvNick.setText(piInfo.getName());
            else
                tvNick.setText("游客");
            ImageUtil.setDefaultImageView(ivUserAvatar, piInfo.getHeader_img_url(), R.mipmap.icon_user_def_rect, mContext);
            ImageUtil.setDefaultImageView(ivHeaderBg, piInfo.getCircle_header_bg_url(), R.mipmap.bg_circle_header, mContext);
        } else {
            tvNick.setText("游客");
        }
        ivUserAvatar.setOnClickListener(view -> startActivity(new Intent(mContext, PersonalInfoActivity.class)));

        mAdapter = new CircleContentAdapter(circleContentList, mContext);
        mAdapter.addHeaderView(headerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerview.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(mContext, R.color.grayline)));
        recyclerview.setAdapter(mAdapter);
    }

    private void addListener() {
        ivPublishCircle.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PublishCircleActivity.class);
            intent.putExtra(PublishCircleActivity.PUBLISH_TYPE,PublishCircleActivity.TYPE_PUBLISH_TEXT_AND_IMG);
            startActivity(intent);
        });
        ivPublishCircle.setOnLongClickListener(view -> {
            Intent intent = new Intent(mContext, PublishCircleActivity.class);
            intent.putExtra(PublishCircleActivity.PUBLISH_TYPE,PublishCircleActivity.TYPE_PUBLISH_TEXT_ONLY);
            startActivity(intent);
            return false;
        });

        swipeLayout.setOnRefreshListener(() -> {
            isPullToRefresh = true;
            isRefresh = true;
            curPage = 0;
            getCircleContnets(curPage, STATE_REFRESH);
        });
        //加载更多
        mAdapter.setEnableLoadMore(true);
        mAdapter.setPreLoadNumber(PAGE_SIZE);
        mAdapter.setOnLoadMoreListener(() -> getCircleContnets(curPage, STATE_MORE));
        mAdapter.setDelBtnClickListenerListener(postion -> showDeleteDialog(postion));
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mAdapter.setLikeAndCommentViewGone();
            }
        });
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        String savePath = result.getImages().get(0).getOriginalPath();
        uploadImgFile(savePath);
    }

    /**
     * 上传图片
     *
     * @param picPath
     */
    private void uploadImgFile(final String picPath) {
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    circleHeaderBgUrl = bmobFile.getFileUrl();

                    PersonInfo personInfo = BmobUser.getCurrentUser(PersonInfo.class);
                    personInfo.setCircle_header_bg_url(circleHeaderBgUrl);
                    personInfo.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            stopProgressDialog();
                            if (e == null) {
                                ImageUtil.setDefaultImageView(ivHeaderBg, circleHeaderBgUrl, -1, mContext);
                            } else {
                                if (e.getErrorCode() == 206) {
                                    OtherUtil.showToastText("您的账号在其他地方登录，请重新登录");
                                    aCache.put("has_login", "no");
                                    LocationUtil.getInstance().stopGetLocation();
                                    AppUtils.AppExit(mContext);
                                    PersonInfo.logOut();
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                } else {
                                    OtherUtil.showToastText("上传失败:" + e.getMessage());
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(mContext, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                OtherUtil.showToastText("上传" + value + "%");
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onPublishOkEvent(String event) {
        if (TextUtils.equals(event, "refresh")) {
            isRefresh = true;
            getCircleContnets(0, STATE_REFRESH);
        }
    }


    public void getCircleContnets(final int page, final int actionType) {
        BmobQuery<CircleContent> query = new BmobQuery<>();
        query.include("personInfo");
        query.order("-createdAt");
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            // 跳过之前页数并去掉重复数据
            query.setSkip(page * PAGE_SIZE + 1);
        } else {
            query.setSkip(0);
        }
        // 设置每页数据个数
        query.setLimit(PAGE_SIZE);
        query.findObjects(new FindListener<CircleContent>() {
            @Override
            public void done(List<CircleContent> list, BmobException e) {
                stopProgressDialog();
                if (swipeLayout != null && swipeLayout.isRefreshing()){
                    swipeLayout.setRefreshing(false);
                }
                if (e == null) {
                    curPage++;
                    if (isRefresh) {//下拉刷新需清理缓存
                        mAdapter.setNewData(processCircleContent(list));
                        isRefresh = false;
                    } else {//正常请求 或 上拉加载更多时处理流程
                        if (list.size() > 0) {
                            mAdapter.addData(processCircleContent(list));
                            mAdapter.loadMoreComplete();
                        } else {
                            mAdapter.loadMoreEnd();
                        }
                    }
                } else {
                    LogUtil.d("查询CircleContent失败" + e.getMessage() + "," + e.getErrorCode());
                    mAdapter.loadMoreFail();
                }
            }
        });
    }

    private List<CircleContent> processCircleContent(List<CircleContent> list) {
        for (int i = 0; i < list.size(); i++) {
            final CircleContent content = list.get(i);
            // 查询喜欢这个帖子的所有用户，因此查询的是用户表
            BmobQuery<PersonInfo> query = new BmobQuery<>();
            //likes是CircleContent表中的字段，用来存储所有喜欢该帖子的用户
            query.addWhereRelatedTo("likes", new BmobPointer(content));
            query.findObjects(new FindListener<PersonInfo>() {
                @Override
                public void done(List<PersonInfo> list, BmobException e) {
                    if (e == null) {
                        if (null != list && list.size() > 0) {
                            content.setLikeList(list);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        LogUtil.i("zuji", "失败：" + e.getMessage());
                    }
                }
            });

            BmobQuery<CircleComment> queryComment = new BmobQuery<>();
            queryComment.addWhereEqualTo("circleContent", new BmobPointer(content));
            //希望同时查询该评论的发布者的信息，以及该帖子的作者的信息，这里用到上面`include`的并列对象查询和内嵌对象的查询
            queryComment.include("personInfo,circleContent.personInfo");
            queryComment.findObjects(new FindListener<CircleComment>() {
                @Override
                public void done(List<CircleComment> comments, BmobException e) {
                    if (e == null) {
                        if (null != comments && comments.size() > 0) {
                            content.setCommentList(comments);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        LogUtil.i("zuji", "失败：" + e.getMessage());
                    }
                }
            });

            String[] imgUrls = content.getImgUrls();
            if (imgUrls != null && imgUrls.length > 0) {
                ArrayList<CircleContent.CircleImageBean> imgList = new ArrayList<>();
                for (int j = 0; j < imgUrls.length; j++) {
                    String imgUrl = imgUrls[j];
                    CircleContent.CircleImageBean circleImg = new CircleContent.CircleImageBean();
                    circleImg.setId(j + "");
                    circleImg.setUrl(imgUrl);
                    imgList.add(circleImg);
                }
                content.setImageList(imgList);
            }
        }
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onFragmentFirstSelected() {
        showProgressDialog(getActivity(),false);
        getCircleContnets(0, STATE_REFRESH);
    }


    //显示删除对话框
    private void showDeleteDialog(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("确定要删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final List<CircleContent> dataList = mAdapter.getData();
                String objectId = null;
                if (dataList != null && dataList.size() > 0) {
                    CircleContent circleContent = dataList.get(pos);
                    objectId = circleContent.getObjectId();
                }
                final CircleContent circleContent = new CircleContent(CircleContent.TYPE_TEXT_AND_IMG);
                circleContent.setObjectId(objectId);
                final String finalObjectId = objectId;

                showProgressDialog(getActivity(), false);
                circleContent.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        stopProgressDialog();
                        if (e == null) {
                            List<CircleContent> list = dataList;
                            Iterator<CircleContent> iter = list.iterator();
                            while (iter.hasNext()) {
                                CircleContent content = iter.next();
                                if (content.equals(finalObjectId)) {
                                    iter.remove();
                                    break;
                                }
                            }
                            isRefresh = true;
                            getCircleContnets(0, STATE_REFRESH);
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "删除失败：" + e.getMessage() + "," + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
