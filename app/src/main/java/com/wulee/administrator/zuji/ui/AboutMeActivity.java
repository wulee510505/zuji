package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.utils.AppUtils;
import com.wulee.administrator.zuji.utils.OtherUtil;


/**
 * Created by xian on 2017/2/17.
 */

public class AboutMeActivity extends BaseActivity {

    private TextView tvVersionName;
    private TextView tvSoftWareSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_me);

        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.title)).setText("关于");
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvVersionName= (TextView)findViewById(R.id.tv_version_name);
        String versionName = AppUtils.getVersionName();
        tvVersionName.setText("V "+versionName);

        tvSoftWareSite = (TextView) findViewById(R.id.tv_software_site);
        tvSoftWareSite.setText(getClickableSpan());
        //设置超链接可点击
        tvSoftWareSite.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 获取可点击的SpannableString
     * @return
     */
    private SpannableString getClickableSpan() {
        SpannableString spannableString = new SpannableString("软件官网：http://zuji51.bmob.site/");
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Uri uri = Uri.parse("http://zuji51.bmob.site/");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        }, 5, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void shareClick(View view){
       /* Bitmap iconbmp = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        File dir = new File(Constant.SAVE_PIC);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            String filePath = Constant.SAVE_PIC + "icon"+".jpg";
            if(!FileUtils.isFileExists(filePath)){
                ImageUtil.saveBitmap(iconbmp,filePath);
            }
            OtherUtil.shareTextAndImage(this,"足迹","一款可以记录并查看出行轨迹的工具类软件 \nhttp://zuji51.bmob.site/", !TextUtils.isEmpty(filePath)?filePath:null);//分享图文
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        OtherUtil.shareTextAndImage(this,"足迹","一款可以记录并查看出行轨迹的工具类软件 \nhttp://zuji51.bmob.site/", null);//分享图文
    }

}
