package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jph.takephoto.PictureActivity;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.OtherUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by wulee on 2016/12/15 17:25
 */

public class PersonalInfoActivity extends BaseActivity {

    private static final int AVATAR_REQUEST_CODE = 100;
    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.iv_submit)
    ImageView ivSubmit;
    @InjectView(R.id.titlelayout)
    RelativeLayout titlelayout;
    @InjectView(R.id.user_photo)
    ImageView userPhoto;
    @InjectView(R.id.name_tv)
    TextView nameTv;
    @InjectView(R.id.et_name)
    EditText etName;


    private String headerimgurl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_personal_center);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        title.setText("个人中心");
        PersonInfo personInfo = DBHandler.getCurrPesonInfo();
        if (null != personInfo) {
            if (!TextUtils.isEmpty(personInfo.getName()))
                etName.setText(personInfo.getName());
            else
                etName.setText("游客");

            ImageUtil.setCircleImageView(userPhoto, personInfo.getHeader_img_url(), R.mipmap.icon_user_def, this);
        }
    }


    private void updatePersonInfo() {
        final String name = etName.getText().toString().trim();

        PersonInfo personInfo = BmobUser.getCurrentUser(PersonInfo.class);
        personInfo.setName(name);

        personInfo.setHeader_img_url(headerimgurl);
        personInfo.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                stopProgressDialog();
                if (e == null) {
                    PersonInfo pi = DBHandler.getCurrPesonInfo();
                    if (null != pi) {
                        pi.setName(name);
                        if (!TextUtils.isEmpty(headerimgurl))
                            pi.setHeader_img_url(headerimgurl);
                        DBHandler.updatePesonInfo(pi);
                    }
                    OtherUtil.showToastText("更新个人信息成功");
                } else {
                    OtherUtil.showToastText("更新个人信息失败:" + e.getMessage());
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AVATAR_REQUEST_CODE:// 头像的返回
                if (resultCode == RESULT_OK && data != null) {
                    String savePath = data.getStringExtra(PictureActivity.INTENT_KEY_RETURN_SAVE_PATH);
                    if (!TextUtils.isEmpty(savePath)) {
                        Bitmap suitBitmap = BitmapFactory.decodeFile(savePath);
                        if (null != suitBitmap)
                            userPhoto.setImageBitmap(ImageUtil.toRoundBitmap(suitBitmap));
                        if (!checkInternetConnection()) {
                            return;
                        }
                        uploadImgFile(savePath);
                    }
                }
                break;
        }
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
                    headerimgurl = bmobFile.getFileUrl();

                    PersonInfo personInfo = BmobUser.getCurrentUser(PersonInfo.class);
                    personInfo.setHeader_img_url(headerimgurl);
                    personInfo.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            stopProgressDialog();
                            if (e == null) {
                                PersonInfo piInfo = DBHandler.getCurrPesonInfo();
                                if (null != piInfo) {
                                    piInfo.setHeader_img_url(headerimgurl);
                                }
                                DBHandler.updatePesonInfo(piInfo);
                                toast("更新个人头像成功");
                            } else {
                                OtherUtil.showToastText("更新个人头像失败:" + e.getMessage());
                            }
                        }
                    });
                } else {
                    toast("头像上传失败" + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                OtherUtil.showToastText("上传" + value + "%");
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.iv_submit, R.id.user_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_submit:
                showProgressDialog(false);
                updatePersonInfo();
                break;
            case R.id.user_photo:
                Intent intent = new Intent(this, PictureActivity.class);
                //必须传入的输出临时文件夹路径
                intent.putExtra(PictureActivity.INTENT_KEY_PHOTO_TMP_PATH_DIR, Constant.TEMP_FILE_PATH);
                // 按比例剪切
                intent.putExtra(PictureActivity.INTENT_KEY_CAN_CUT_PHOTO, true);
                intent.putExtra(PictureActivity.INTENT_KEY_CUT_PHOTO_ASPECTX, 1);
                intent.putExtra(PictureActivity.INTENT_KEY_CUT_PHOTO_ASPECTY, 1);
                // 压缩，最大长宽在600左右；小于600则原样输出，大于600则进行压缩
                intent.putExtra(PictureActivity.INTENT_KEY_COMPRESS_PHOTO, true);
                intent.putExtra(PictureActivity.INTENT_KEY_ENABLE_CUSCOMPRESS, true);
                intent.putExtra(PictureActivity.INTENT_KEY_COMPRESS_PHOTO_MAXPIXEL, 600);
                startActivityForResult(intent, AVATAR_REQUEST_CODE);
                break;
        }
    }
}
