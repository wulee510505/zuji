package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.adapter.PublishPicGridAdapter;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.CircleContent;
import com.wulee.administrator.zuji.entity.PublishPicture;
import com.wulee.administrator.zuji.widget.BaseTitleLayout;
import com.wulee.administrator.zuji.widget.TitleLayoutClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import de.greenrobot.event.EventBus;

/**
 * Created by wulee on 2017/8/22 11:40
 */

public class PublishCircleActivity extends TakePhotoActivity {


    @InjectView(R.id.edittext)
    AppCompatEditText edittext;
    @InjectView(R.id.gridview_pic)
    GridView gridviewPic;
    @InjectView(R.id.progress_bar)
    ProgressBar progressBar;
    @InjectView(R.id.titlelayout)
    BaseTitleLayout titlelayout;

    private PublishPicGridAdapter mGridAdapter;
    private List<PublishPicture> picList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.circle_publish);
        ButterKnife.inject(this);

        initView();
        addListner();
    }

    private void initView() {
        PublishPicture pic = new PublishPicture();
        pic.setId(-1);
        pic.setPath("");
        picList.add(picList.size(), pic);
        mGridAdapter = new PublishPicGridAdapter(picList, this);
        gridviewPic.setAdapter(mGridAdapter);
    }

    private void addListner() {
        gridviewPic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                PublishPicture pic = picList.get(pos);
                if (null != pic) {
                    if (pic.getId() == -1) {
                        TakePhoto takePhoto = getTakePhoto();
                        takePhoto.onPickMultiple(9);
                    }
                }
            }
        });
        titlelayout.setOnTitleClickListener(new TitleLayoutClickListener() {
            @Override
            public void onLeftClickListener() {
                super.onLeftClickListener();
                finish();
            }
            @Override
            public void onRightTextClickListener() {
                super.onRightTextClickListener();
            }
            @Override
            public void onRightImg1ClickListener() {
                super.onRightImg1ClickListener();
                publishCircleContent();
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
        picList.clear();
        ArrayList<TImage> secImgs = result.getImages();
        for (int i = 0; i < secImgs.size(); i++) {
            TImage img = secImgs.get(i);
            PublishPicture pic = new PublishPicture();
            pic.setId(i);
            pic.setPath(img.getOriginalPath());
            picList.add(pic);
        }
        if (picList.size() < 9) {
            PublishPicture pic = new PublishPicture();
            pic.setId(-1);
            pic.setPath("");
            picList.add(picList.size(), pic);
        }
        mGridAdapter.setSelPic(picList);
    }

    /**
     *发表圈子内容
     */
    private void publishCircleContent() {
        String content = edittext.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "说点什么吧@^@", Toast.LENGTH_SHORT).show();
            return;
        }
        PersonInfo piInfo = BmobUser.getCurrentUser(PersonInfo.class);
        if (null == piInfo)
            return;

        progressBar.setVisibility(View.VISIBLE);

        final CircleContent circlrContent = new CircleContent();
        circlrContent.setId(SystemClock.currentThreadTimeMillis());
        circlrContent.setUserId(piInfo.getUid());
        circlrContent.setUserNick(piInfo.getName());
        circlrContent.setUserAvatar(piInfo.getHeader_img_url());
        circlrContent.setContent(content);
        circlrContent.personInfo = piInfo;
        if (picList.size() > 1) {
            picList.remove(picList.size() - 1);

            final String[] filePaths = new String[picList.size()];
            for (int i = 0; i < picList.size(); i++) {
                filePaths[i] = picList.get(i).getPath();
            }
            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> files, List<String> urls) {
                    //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                    //2、urls-上传文件的完整url地址
                    if (urls.size() == filePaths.length) {//如果数量相等，则代表文件全部上传完成
                        String[] imgUrls = new String[urls.size()];
                        for (int i = 0; i < urls.size(); i++) {
                            imgUrls[i] = urls.get(i);
                        }
                        circlrContent.setImgUrls(imgUrls);
                        circlrContent.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                progressBar.setVisibility(View.GONE);
                                if (e == null) {
                                    EventBus.getDefault().post(new String("publish ok"));
                                    PublishCircleActivity.this.finish();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    Toast.makeText(PublishCircleActivity.this, "错误码" + statuscode + ",错误描述：" + errormsg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                    //1、curIndex--表示当前第几个文件正在上传
                    //2、curPercent--表示当前上传文件的进度值（百分比）
                    //3、total--表示总的上传文件数
                    //4、totalPercent--表示总的上传进度（百分比）
                }
            });
        }
    }


}
