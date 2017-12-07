package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.UIUtils;
import com.wulee.administrator.zuji.widget.DotIndicator;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wulee on 2017/10/13 13:50
 * 查看多图
 */

public class BigMultiImgActivity extends BaseActivity {

    @InjectView(R.id.viewpager_img)
    ViewPager viewpagerImg;
    @InjectView(R.id.dot_indicator)
    DotIndicator dotIndicator;

    public static final String IMAGES_URL = "images_url";
    public static final String IMAGE_INDEX = "image_index";


    private String[] imgUrls;
    private int index;


    private BigImgPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.big_mutil_image);
        ButterKnife.inject(this);

        initData();
        initBottomNavView();
    }

    private void initData() {
        imgUrls = getIntent().getStringArrayExtra(IMAGES_URL);
        index = getIntent().getIntExtra(IMAGE_INDEX, 0);
        mAdapter = new BigImgPagerAdapter(imgUrls);
        viewpagerImg.setAdapter(mAdapter);

        if(imgUrls != null && imgUrls.length>1){
            dotIndicator.setViewPager(viewpagerImg);
        }
        viewpagerImg.setCurrentItem(index);
    }

    /*
     * 初始化底端导航显示内容
	 */
    private void initBottomNavView() {

    }

    @Override
    protected int getStateBarColor() {
        return R.color.color_transparent;
    }


    public class BigImgPagerAdapter extends PagerAdapter {

        private String[] imgUrlArray;

        public BigImgPagerAdapter(String[] imgUrlArray) {
            this.imgUrlArray = imgUrlArray;
        }

        @Override
        public int getCount() {
            return imgUrlArray != null ? imgUrlArray.length : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(BigMultiImgActivity.this).inflate(R.layout.big_single_image, null);
            ImageView imageView = view.findViewById(R.id.iv_bigimg);

            int sw = UIUtils.getScreenWidthAndHeight(BigMultiImgActivity.this)[0];
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            rlp.width = sw;
            rlp.height = sw * 3 / 2;
            imageView.setLayoutParams(rlp);

            ImageUtil.setDefaultImageView(imageView, imgUrlArray[position], R.mipmap.bg_pic_def_rect, BigMultiImgActivity.this);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            RelativeLayout rlLayout = (RelativeLayout) object;
            container.removeView(rlLayout);
        }

    }
}
