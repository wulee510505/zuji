package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TResult;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.OtherUtil;
import com.wulee.administrator.zuji.widget.ActionSheet;

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

public class PersonalInfoActivity extends TakePhotoActivity implements ActionSheet.MenuItemClickListener {

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
    @InjectView(R.id.rl_name)
    RelativeLayout rlName;
    @InjectView(R.id.et_gender)
    TextView etGender;
    @InjectView(R.id.rl_gender)
    RelativeLayout rlGender;
    @InjectView(R.id.container)
    LinearLayout container;


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

            if (!TextUtils.isEmpty(personInfo.getSex()))
                etGender.setText(personInfo.getSex());
            else
                etGender.setText("其他");

            ImageUtil.setCircleImageView(userPhoto, personInfo.getHeader_img_url(), R.mipmap.icon_user_def, this);
        }
    }


    private void updatePersonInfo() {
        final String name = etName.getText().toString().trim();
        String genderStr = etGender.getText().toString().trim();

        PersonInfo personInfo = BmobUser.getCurrentUser(PersonInfo.class);
        personInfo.setName(name);
        personInfo.setSex(genderStr);

        personInfo.setHeader_img_url(headerimgurl);
        personInfo.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
               // stopProgressDialog();
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
                  /*  String savePath = data.getStringExtra(PictureActivity.INTENT_KEY_RETURN_SAVE_PATH);
                    if (!TextUtils.isEmpty(savePath)) {
                        Bitmap suitBitmap = BitmapFactory.decodeFile(savePath);
                        if (null != suitBitmap)
                            userPhoto.setImageBitmap(ImageUtil.toRoundBitmap(suitBitmap));
                        if (!checkInternetConnection()) {
                            return;
                        }
                        uploadImgFile(savePath);
                    }*/
                }
                break;
        }
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
        if (!TextUtils.isEmpty(savePath)) {
            Bitmap suitBitmap = BitmapFactory.decodeFile(savePath);
            if (null != suitBitmap)
                userPhoto.setImageBitmap(ImageUtil.toRoundBitmap(suitBitmap));

            uploadImgFile(savePath);
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
                            //stopProgressDialog();
                            if (e == null) {
                                PersonInfo piInfo = DBHandler.getCurrPesonInfo();
                                if (null != piInfo) {
                                    piInfo.setHeader_img_url(headerimgurl);
                                }
                                DBHandler.updatePesonInfo(piInfo);
                                Toast.makeText(PersonalInfoActivity.this, "更新个人头像成功", Toast.LENGTH_SHORT).show();
                            } else {
                                OtherUtil.showToastText("更新个人头像失败:" + e.getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(PersonalInfoActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                OtherUtil.showToastText("上传" + value + "%");
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.iv_submit, R.id.user_photo,R.id.rl_gender})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_submit:
                //showProgressDialog(false);
                updatePersonInfo();
                break;
            case R.id.user_photo:
               /* Intent intent = new Intent(this, PictureActivity.class);
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
                startActivityForResult(intent, AVATAR_REQUEST_CODE);*/

                TakePhoto takePhoto=getTakePhoto();
                takePhoto.onPickFromGallery();
                break;
            case R.id.rl_gender:
                ActionSheet menuView = new ActionSheet(this);
                menuView.setCancelButtonTitle("取消");
                menuView.addItems("男", "女", "其他");
                menuView.setItemClickListener(this);
                menuView.setCancelableOnTouchMenuOutside(true);
                menuView.showMenu();
                break;
        }
    }

    @Override
    public void onItemClick(int pos) {
         switch (pos){
             case 0:
                  etGender.setText("男");
                 break;
             case 1:
                  etGender.setText("女");
                 break;
             case 2:
                  etGender.setText("其他");
                 break;
         }
        PersonInfo personInfo = DBHandler.getCurrPesonInfo();
        if (null != personInfo) {
            personInfo.setSex(etGender.getText().toString().trim());
        }
    }
}
