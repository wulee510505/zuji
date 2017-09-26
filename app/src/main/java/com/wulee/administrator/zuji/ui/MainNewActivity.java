package com.wulee.administrator.zuji.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.ui.fragment.CircleFragment;
import com.wulee.administrator.zuji.ui.fragment.JokeFragment;
import com.wulee.administrator.zuji.ui.fragment.MainBaseFrag;
import com.wulee.administrator.zuji.ui.fragment.NewsFragment;
import com.wulee.administrator.zuji.ui.fragment.ZujiFragment;
import com.wulee.administrator.zuji.ui.pushmsg.PushMsgListActivity;
import com.wulee.administrator.zuji.utils.AppUtils;
import com.wulee.administrator.zuji.utils.ConfigKey;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.LocationUtil;
import com.wulee.administrator.zuji.utils.OtherUtil;
import com.wulee.administrator.zuji.utils.PhoneUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

import static cn.bmob.v3.BmobUser.getCurrentUser;
import static com.wulee.administrator.zuji.App.aCache;

/**
 * Created by wulee on 2017/9/6 09:52
 */

public class MainNewActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener,NavigationView.OnNavigationItemSelectedListener{

    private ViewPager mViewPager;
    private RadioGroup mRg;
    private NavigationView navigationView;
    private View menuHeaderView;
    private ImageView ivHeader;
    private TextView mTvName;
    private TextView mTvMobile;

    private MainFPagerAdaper mainFPagerAdaper;


    private static DrawerLayout mDrawerLayout;

    // 标示了当前位置
    private final int POS_ONE = 0, POS_TWO = 1, POS_THREE = 2, POS_FOUR = 3;
    // 是否已经展示过Fragment，默认是false，没有展示过
    private boolean[] hasFragSelected = new boolean[POS_FOUR + 1];
    private MainBaseFrag[] mainBaseFrags = new MainBaseFrag[POS_FOUR + 1];

    private long mLastClickReturnTime = 0l; // 记录上一次点击返回按钮的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_new_activity);

        init();

        int pos = 0;
        try {
            String posStr = aCache.getAsString(ConfigKey.KEY_MAIN_TAB_POS);
            if(!TextUtils.isEmpty(posStr))
                pos = Integer.parseInt(posStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        initSelectTab(pos);
        sendFragmentFirstSelectedMsg(pos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMenuHeaderInfo();

        long lastUpdateCurrPersonInfoTime = 0L;
        try {
            String timeStr = aCache.getAsString(Constant.KEY_LAST_UPDATE_CURR_PERSONINFO_TIME);
            if(!TextUtils.isEmpty(timeStr)){
                lastUpdateCurrPersonInfoTime = Long.parseLong(timeStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        long interal = System.currentTimeMillis() - lastUpdateCurrPersonInfoTime;
        if(interal > Constant.UPDATE_CURR_PERSONINFO_INTERVAL){
            final PersonInfo personInfo = getCurrentUser(PersonInfo.class);
            personInfo.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        aCache.put(Constant.KEY_LAST_UPDATE_CURR_PERSONINFO_TIME,System.currentTimeMillis());
                        personInfo.setMobile(DBHandler.getCurrPesonInfo().getMobile());
                        DBHandler.insertPesonInfo(personInfo);
                    }else{
                        if(e.getErrorCode() == 206){
                            OtherUtil.showToastText("您的账号在其他地方登录，请重新登录");
                            aCache.put("has_login","no");
                            LocationUtil.getInstance().stopGetLocation();
                            AppUtils.AppExit(MainNewActivity.this);
                            PersonInfo.logOut();
                            startActivity(new Intent(MainNewActivity.this,LoginActivity.class));
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BmobUpdateAgent.forceUpdate(this);
    }

    private void init() {
        initView();
        initData();
        addListner();
    }


    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
        mDrawerLayout.setScrimColor(0x80000000);

        mViewPager = (ViewPager) findViewById(R.id.mviewpager);
        mRg = (RadioGroup) findViewById(R.id.mnc_rg);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //自定义menu菜单icon和title颜色
        int[][] states = new int[][]{
                new int[]{ -android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };
        int[] colors = new int[]{              ContextCompat.getColor(this,R.color.ctv_black_2),
                ContextCompat.getColor(this,R.color.colorAccent)
        };
        ColorStateList csl = new ColorStateList(states, colors);

        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(csl);

        menuHeaderView = navigationView.inflateHeaderView(R.layout.nav_menu_header);

        ivHeader = (ImageView) menuHeaderView.findViewById(R.id.circle_img_header);
        mTvName = (TextView) menuHeaderView.findViewById(R.id.tv_name);
        mTvMobile = (TextView) menuHeaderView.findViewById(R.id.tv_mobile);
    }

    private void initData() {
        mainFPagerAdaper = new MainFPagerAdaper(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mainFPagerAdaper);
    }

    private void initMenuHeaderInfo() {
        PersonInfo personalInfo = DBHandler.getCurrPesonInfo();
        if(null != personalInfo){
            if(!TextUtils.isEmpty(personalInfo.getName()))
                mTvName.setText(personalInfo.getName());
            else
                mTvName.setText("游客");
            mTvMobile.setText(PhoneUtil.encryptTelNum(personalInfo.getMobile()));
            ImageUtil.setCircleImageView(ivHeader,personalInfo.getHeader_img_url(),R.mipmap.icon_user_def,this);
        }
    }

    private void addListner() {
        mViewPager.setOnPageChangeListener(this);
        mRg.setOnCheckedChangeListener(this);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
            @Override
            public void onDrawerOpened(View drawerView) {

            }
            @Override
            public void onDrawerClosed(View drawerView) {

            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainNewActivity.this,PersonalInfoActivity.class));
            }
        });
    }

    private void initSelectTab(int pos) {
        if (pos < POS_ONE || pos > POS_FOUR)
            return;
        RadioButton rb = findRadioButtonByPos(pos);
        if (rb != null) {
            rb.setChecked(true);
        }
        mViewPager.setCurrentItem(pos, false);
    }


    private RadioButton findRadioButtonByPos(int position) {
        switch (position) {
            case POS_ONE:
                return (RadioButton) mRg.findViewById(R.id.mnc_rbnt_one);
            case POS_TWO:
                return (RadioButton) mRg.findViewById(R.id.mnc_rbnt_two);
            case POS_THREE:
                return (RadioButton) mRg.findViewById(R.id.mnc_rbnt_three);
            case POS_FOUR:
                return (RadioButton) mRg.findViewById(R.id.mnc_rbnt_four);
        }
        return null;
    }

    private void sendFragmentFirstSelectedMsg(int position) {
        if(false == hasFragSelected[position]) {
            hasFragSelected[position] = true;
            int msgWhat = position + 1000; // 消息和位置的规则为相差1000
            FragmentSelectedHandler.sendEmptyMessageDelayed(msgWhat, 10);
        }
    }

    private Handler FragmentSelectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int position = msg.what - 1000;
            if(position>=POS_ONE && position<=POS_FOUR) {
                if(mainBaseFrags[position] == null) {
                    this.removeMessages(msg.what);
                    this.sendEmptyMessageDelayed(msg.what, 10);
                } else {
                    mainBaseFrags[position].onFragmentFirstSelected();
                }
            }
            super.handleMessage(msg);
        }
    };



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RadioButton rb = findRadioButtonByPos(position);
        if (rb != null) {
            rb.setChecked(true);
        }

        aCache.put(ConfigKey.KEY_MAIN_TAB_POS, position+"");
        sendFragmentFirstSelectedMsg(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.mnc_rbnt_one:
                if (POS_ONE != mViewPager.getCurrentItem()) {
                    mViewPager.setCurrentItem(POS_ONE, false);
                }
                break;
            case R.id.mnc_rbnt_two:
                if (POS_TWO != mViewPager.getCurrentItem()) {
                    mViewPager.setCurrentItem(POS_TWO, false);
                }
                break;
            case R.id.mnc_rbnt_three:
                if (POS_THREE != mViewPager.getCurrentItem()) {
                    mViewPager.setCurrentItem(POS_THREE, false);
                }
                break;
            case R.id.mnc_rbnt_four:
                if (POS_FOUR != mViewPager.getCurrentItem()) {
                    mViewPager.setCurrentItem(POS_FOUR, false);
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==  R.id.item_loginout) {
            showLogoutDialog();
        }else  if (id ==  R.id.item_pushmsg) {
            startActivity(new Intent(this,PushMsgListActivity.class));
        }else  if (id ==  R.id.item_msg_board) {
            PersonInfo personInfo = getCurrentUser(PersonInfo.class);
            startActivity(new Intent(this,MessageBoardActivity.class).putExtra("piInfo",personInfo));
        }else  if (id ==  R.id.item_checkupdate) {
            BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                @Override
                public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                    // TODO Auto-generated method stub
                    if (updateStatus == UpdateStatus.Yes) {//版本有更新

                    }else if(updateStatus == UpdateStatus.No){
                        Toast.makeText(MainNewActivity.this,"版本无更新", Toast.LENGTH_SHORT).show();
                    }else if(updateStatus==UpdateStatus.EmptyField){//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                        Toast.makeText(MainNewActivity.this, "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                    }else if(updateStatus==UpdateStatus.IGNORED){
                        Toast.makeText(MainNewActivity.this, "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
                    }else if(updateStatus==UpdateStatus.ErrorSizeFormat){
                        Toast.makeText(MainNewActivity.this, "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                    }else if(updateStatus==UpdateStatus.TimeOut){
                        Toast.makeText(MainNewActivity.this, "查询出错或查询超时", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            BmobUpdateAgent.forceUpdate(MainNewActivity.this);
        }else  if (id ==  R.id.item_feedback) {
            startActivity(new Intent(this,FeedBackActivity.class));
        }else  if (id ==  R.id.item_setting) {
            startActivity(new Intent(this,SettingActivity.class));
        }else  if (id ==  R.id.item_about) {
            startActivity(new Intent(this,AboutMeActivity.class));
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定要退出吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aCache.put("has_login","no");
                LocationUtil.getInstance().stopGetLocation();
                AppUtils.AppExit(MainNewActivity.this);
                PersonInfo.logOut();
                startActivity(new Intent(MainNewActivity.this,LoginActivity.class));
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }


    /**
     * 页面适配器start
     */
    public class MainFPagerAdaper extends FragmentPagerAdapter {

        public MainFPagerAdaper(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment retFragment = null;
            switch (position) {
                case 0:
                    retFragment = new ZujiFragment();
                    mainBaseFrags[0] = (ZujiFragment) retFragment;
                    break;
                case 1:
                    retFragment = new CircleFragment();
                    mainBaseFrags[1] = (CircleFragment) retFragment;
                    break;
                case 2:
                    retFragment = new JokeFragment();
                    mainBaseFrags[2] = (JokeFragment) retFragment;
                    break;
                case 3:
                    retFragment = new NewsFragment();
                    mainBaseFrags[3] = (NewsFragment) retFragment;
                    break;
            }
            return retFragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

    /**
     * 打开左侧Menu的监听事件
     */
    public static void OpenLeftMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    /**
     * 关闭Menu
     */
    public boolean CloseMenu() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if (CloseMenu())
                    return true;
                if(System.currentTimeMillis() - mLastClickReturnTime > 1000L) {
                    mLastClickReturnTime = System.currentTimeMillis();
                    toast("再按一次退出程序");
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
