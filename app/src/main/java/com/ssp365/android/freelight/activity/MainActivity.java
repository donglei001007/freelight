package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.DebugLog;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.common.SmartSportHandler;
import com.ssp365.android.freelight.db.DBOpenHelper;
import com.ssp365.android.freelight.model.Body;
import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.model.Sporter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 主界面
 *
 * @author 传博科技
 */
public class MainActivity extends FragmentActivity implements OnClickListener {

    protected static final String TAG = "MainActivity";
    // 运动模式选择界面
    private static final int MODE_SELECT_ACTIVITY = 0;
    public static SmartSportApplication mApplication = null;

    public SmartSportHandler mHandler = null;
    // 当前选中页签编号（-1：未选择状态；1：训练状态；2：测试状态；3：分析状态）
    private int selectedModelNo = -1;
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private AwesomeTextView mTextTopModeSelect;
    private TextView mTextTopTitle;
    private ImageView mTextTopWifi;
    private ImageView mTextTopAddSporter;
    private LinearLayout mTabWeixin;
    private LinearLayout mTabFrd;
    private LinearLayout mTabAddress;
    private LinearLayout mTabSettings;
    private String[] topTitles = new String[]{"训练", "测试", "分析", "设置"};
    // 选择的运动员列表
    private List<Body> sporterListRes = new ArrayList<Body>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // android-bootstrap
        TypefaceProvider.registerDefaultIconSets();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // 全局变量
        mApplication = (SmartSportApplication) getApplication();
        mApplication.setHelper(new DBOpenHelper(this));
        mApplication.setDb(mApplication.getHelper().getWritableDatabase());

        mHandler = mApplication.getHandler();
        if (mHandler == null) {
            mHandler = new SmartSportHandler();
            // 获得该共享变量实例
            mApplication.setHandler(mHandler);
            mHandler.setApplication(mApplication);
        }

        initView();
        initEvent();

        setSelect(0);
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity-->onDestroy");
        // 关闭数据库连接
        SQLiteDatabase db = mApplication.getDb();
        Log.i(TAG, "db:" + db);
        if (db != null && db.isOpen()) {
            db.close();
        }
        // 关闭WIFI及连接的测试柱
        if (mApplication.getmWifiService() != null) {
            mApplication.getmWifiService().stop();
        }
        // 关闭打印处理
        DebugLog.closeOutputDebugFile();
        // 中止整个程序
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void initEvent() {
        mTabWeixin.setOnClickListener(this);
        mTabFrd.setOnClickListener(this);
        mTabAddress.setOnClickListener(this);
        mTabSettings.setOnClickListener(this);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mTextTopModeSelect = (AwesomeTextView) findViewById(R.id.id_top_item_select);
        mTextTopTitle = (TextView) findViewById(R.id.id_top_title);
        mTextTopWifi = (ImageView) findViewById(R.id.id_top_wifi);
        mTextTopAddSporter = (ImageView) findViewById(R.id.id_add_sporter);
        mTabWeixin = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);
        mTabSettings = (LinearLayout) findViewById(R.id.id_tab_settings);

        // 界面下部四个导航按钮
        mTabWeixin = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabFrd = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabAddress = (LinearLayout) findViewById(R.id.id_tab_address);
        mTabSettings = (LinearLayout) findViewById(R.id.id_tab_settings);

        mFragments = new ArrayList<Fragment>();
        Fragment mTab01 = new TestFragment(mApplication, Parameter.DO_TYPE_TRAIN);
        Fragment mTab02 = new TestFragment(mApplication, Parameter.DO_TYPE_TEST);
        Fragment mTab03 = new AnalyseFragment(mApplication);
        Fragment mTab04 = new MoreFragment(mApplication);
        mFragments.add(mTab01);
        mFragments.add(mTab02);
        mFragments.add(mTab03);
        mFragments.add(mTab04);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            /**
             * 选择页签时候的操作
             *
             * @param arg0
             */
            @Override
            public void onPageSelected(int arg0) {
                // 清空运动模式选择状态（设定为-1）
                selectedModelNo = -1;
                // 清空选择的运动员列表
                sporterListRes = new ArrayList<Body>();
                int currentItem = mViewPager.getCurrentItem();
                setSelect(currentItem);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        // wifi按钮的监听事件
        mTextTopWifi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WifisetActivity.class);
                startActivity(intent);
            }
        });

        // 运动模式选择按钮的监听事件
        mTextTopModeSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SportItemSelectActivity.class);
                startActivityForResult(intent, MODE_SELECT_ACTIVITY);
            }
        });

        // 运动员选择按钮的监听事件
        mTextTopAddSporter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SporterListActivity.class);
                intent.putExtra("start_type", SporterListActivity.START_TYPE_GET);
                // 设定选定的运动员
                ArrayList<Sporter> arrayListSporter = new ArrayList<Sporter>();
                for (int i = 0; i < sporterListRes.size(); i++) {
                    arrayListSporter.add((sporterListRes.get(i)).getSporter());
                }
                mApplication.setArraySporter(arrayListSporter);
                startActivityForResult(intent, Parameter.REQUEST_SPORTER_SELECT);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tab_weixin:
                setSelect(0);
                break;
            case R.id.id_tab_frd:
                setSelect(1);
                break;
            case R.id.id_tab_address:
                setSelect(2);
                break;
            case R.id.id_tab_settings:
                setSelect(3);
                break;

            default:
                break;
        }
    }

    private void setSelect(int i) {
        setTab(i);
        mViewPager.setCurrentItem(i);
        mTextTopTitle.setText(topTitles[i]);
        // 训练模式和测试模式可以设置wifi
        if (i < 2) {
            mTextTopWifi.setVisibility(View.VISIBLE);
            // 其他模式不可以设置wifi
        } else {
            mTextTopWifi.setVisibility(View.GONE);
        }
        // 选择运动项目按钮
        if (i == 0 || i == 1 || i == 2) {
            mTextTopModeSelect.setVisibility(View.VISIBLE);
        } else {
            mTextTopModeSelect.setVisibility(View.GONE);
        }
        // 添加运动员按钮
        if (i == 1 || i == 2) {
            mTextTopAddSporter.setVisibility(View.VISIBLE);
        } else {
            mTextTopAddSporter.setVisibility(View.GONE);
        }

    }

    private void setTab(int i) {
        resetImgs();
        // 设置图片为亮色
        // 切换内容区域
        switch (i) {
            case 0:
                mTabWeixin.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            case 1:
                mTabFrd.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            case 2:
                mTabAddress.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            case 3:
                mTabSettings.setBackgroundColor(getResources().getColor(R.color.black));
                break;
        }
    }

    /**
     * 切换图片至暗色
     */
    private void resetImgs() {
        mTabWeixin.setBackgroundColor(getResources().getColor(R.color.color1));
        mTabFrd.setBackgroundColor(getResources().getColor(R.color.color1));
        mTabAddress.setBackgroundColor(getResources().getColor(R.color.color1));
        mTabSettings.setBackgroundColor(getResources().getColor(R.color.color1));
    }

    @Override
    // 退出程序确认
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setTitle("退出程序").setMessage("是否要退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDestroy();
                    System.exit(0);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create().show();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 它Activity操作回调处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 选择运动模式后操作
            case MODE_SELECT_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    // 选择的运动模式
                    selectedModelNo = (int) bundle.get("selected_model_no");
                    mTextTopTitle.setText((String) bundle.get("selected_model_name"));
                } else {
                }
                break;
            // 选择运动员后操作
            case Parameter.REQUEST_SPORTER_SELECT:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    AtomicReference<ArrayList<Sporter>> vecSelectedSporters = new AtomicReference<>((ArrayList<Sporter>) bundle.get("arraySporter"));
                    sporterListRes = new ArrayList<Body>();
                    for (int i = 0; i < vecSelectedSporters.get().size(); i++) {
                        Body b = new Body();
                        Sporter sporter = vecSelectedSporters.get().get(i);
                        b.setNo(i + 1);
                        b.setCoin(sporter.getSporter_name());
                        b.setSporter(sporter);
                        sporterListRes.add(b);
                    }
                    // 取得当前选中的页签
                    int currentFragment = mViewPager.getCurrentItem();
                    switch (currentFragment) {
                        case 1:
                            // 取得测试页签
                            TestFragment mTab02 = (TestFragment) mAdapter.getItem(1);
                            // 调用测试界面的运动员设定处理
                            mTab02.sporterSelected(sporterListRes);
                            break;
                        case 2:
                            // 取得分析界面
                            AnalyseFragment mTab03 = (AnalyseFragment) mAdapter.getItem(2);
                            // 调用分析界面的运动员设定处理
                            mTab03.sporterSelected(sporterListRes);
                            break;
                    }
                } else {
                    // 运动员选择界面报错，不进行任何处理
                }
                break;
        }
    }

    /**
     * 运动模式值取得
     *
     * @return 已选择的运动模式值
     */
    public int getSelectedModelNo() {
        return selectedModelNo;
    }

    /**
     * 获得选择的运动员列表
     *
     * @return 运动员列表
     */
    public List<Body> getSporterListRes() {
        return sporterListRes;
    }
}
