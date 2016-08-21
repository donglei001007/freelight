package com.ssp365.android.freelight.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ssp365.android.freelight.R;
import com.ssp365.android.freelight.common.DebugLog;
import com.ssp365.android.freelight.common.MessageHelper;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.common.SmartSportHandler;
import com.ssp365.android.freelight.db.DBChenjiDAO;
import com.ssp365.android.freelight.db.DBNoDAO;
import com.ssp365.android.freelight.model.Chenji;
import com.ssp365.android.freelight.model.ChenjiDetail;
import com.ssp365.android.freelight.model.Model;
import com.ssp365.android.freelight.model.ModelDetail;
import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.model.Sporter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StopWatchActivity extends Activity implements Runnable {

    /**
     * 当前所经过的时间
     */
    public long time = 0;

    /**
     * 开始时间
     */
    private long startTime;

    public Handler handler;

    /**
     * 用于显示时间
     */
    private TextView timeView;

    /**
     * 用于列表显示分记时间
     */
    public ListView listView;

    /**
     * 分记时间数据
     */
    private List<Long> marks;

    /**
     * 秒表的当前状态
     * 分为正在运行、暂停、停止三种状态
     */
    private int state = 0;

    private static int STATE_RUNNING = 1;
    private static int STATE_STOP = 0;
    private static int STATE_PAUSE = 2;

    //停表标志位（默认为不停表）
    public boolean stopWatchFlag = false;

    private int count = 0;

    //public static Handler mHandler;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Debugging
    private static final String TAG = "StopWatch";
    private static final boolean D = true;

    public SmartSportApplication mApplication = null;
    private SmartSportHandler mHandler = null;
    private ArrayList<Sporter> arraySporter = null;
    private Model selectedModel = null;
    private ArrayList<ModelDetail> modelDetails = null;
    private ArrayList<StopWatchList> stopWatchLists = new ArrayList<StopWatchList>();

    //上次的时间
    public long lastTime = 0;
    //圈数
    public int loopCount = 0;

    boolean starFlag = false;

    //计时模式
    public String do_type = "";

    //DB关联
    DBNoDAO dbNoDAO = null;
    DBChenjiDAO dbChenjiDAO = null;

    //停表按钮、计时按钮、取消按钮
    public ImageButton button_cancle = null;
    public Button bt_stop, bt_click = null;

    //查选通信信息
    private LinearLayout testLinearLayout;
    //查选通信信息窗口
    private TextView testText;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (D)  DebugLog.debug(this, "onCreate start");

        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stopwatch_activity);

        // 全局变量
        mApplication = (SmartSportApplication) getApplication();
        dbNoDAO = new DBNoDAO(mApplication.getDb());
        dbChenjiDAO = new DBChenjiDAO(mApplication.getDb());

        // 获得该共享变量实例  
        mHandler = mApplication.getHandler();
        mHandler.setStopWatch(this);

        selectedModel = mApplication.getModel();
        ((TextView) findViewById(R.id.title)).setText(selectedModel.getModel_name());
        modelDetails = mApplication.getModelDetails();

        arraySporter = mApplication.getArraySporter();
        if (arraySporter != null && arraySporter.size() > 0) {
            do_type = Parameter.DO_TYPE_TEST;
        } else {
            do_type = Parameter.DO_TYPE_TRAIN;
        }
        DebugLog.debug(this, "do_type:" + do_type);

        //读取环境信息（偏好 ）
        readEnvironment();

        //停表按钮的初始化
        bt_stop = (Button) findViewById(R.id.bt_stop);
        StopWatchOnClickListener stopWatchOnClickListener = new StopWatchOnClickListener(this);
        bt_stop.setOnClickListener(stopWatchOnClickListener);

        //计时按钮的初始化
        bt_click = (Button) findViewById(R.id.bt_click);
        ClickWatchOnClickListener clickWatchOnClickListener = new ClickWatchOnClickListener(this);
        bt_click.setOnClickListener(clickWatchOnClickListener);
        //手动控制时才能使用计时按钮，蓝牙模式时隐藏
        if (!Parameter.WIFI_NOT_CONTROL) {
            bt_click.setVisibility(View.GONE);
        }

        //添加取消的处理
        button_cancle = (ImageButton) findViewById(R.id.button_cancle);
        button_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        timeView = (TextView) findViewById(R.id.timeView);
        listView = (ListView) findViewById(R.id.chenji_list);

        //测试用显示屏
        testLinearLayout = (LinearLayout) findViewById(R.id.ll_temp_test);
        testText = (TextView) findViewById(R.id.text_temp_test);
        testText.setMovementMethod(ScrollingMovementMethod.getInstance());
        //非调试模式时，不显示信息框
        if(!Parameter.DO_DEBUG_FLAG){
            testLinearLayout.setVisibility(View.GONE);
        }
         ;
        //创建 handler
        handler = new Handler();

        //设置时间显示
        if (state == STATE_STOP) {
            time = 0;
            timeView.setText(getFormatTime(time));
        } else if (state == STATE_PAUSE) {
            timeView.setText(getFormatTime(time));
        }

        //显示列表
        refreshMarkList();

        // 自动控制时
        if (!Parameter.WIFI_NOT_CONTROL) {
            // 给各个测试柱线程设置各柱子信号序列
            MessageHelper.setClientInf(mApplication);
            // 发送信号(第一组)
            MessageHelper.sendMsg(mApplication, true);
        }


        if (D) DebugLog.debug(this, "onCreate over");
    }

    /**
     * 读取环境
     */
    private void readEnvironment() {
        marks = new ArrayList<Long>();
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("environment", MODE_PRIVATE);
    	state = sharedPreferences.getInt("state", STATE_STOP);
    	startTime = sharedPreferences.getLong("startTime", 0);
    	time = sharedPreferences.getLong("time", 0);
    	
    	marks = new ArrayList<Long>();
    	SharedPreferences sharedPreferencesMarks = getSharedPreferences("marks", MODE_PRIVATE);
    	Map<String, Long> mapMarks = (Map<String, Long>) sharedPreferencesMarks.getAll();
    	for (int i = 0; i < mapMarks.size(); i++){
    		Long mark = mapMarks.get("" + i);
    		marks.add(mark);
    	}
    	
    	Toast.makeText(this, "环境已读取", Toast.LENGTH_LONG).show();
    	*/

    }

    /**
     * 保存环境
     */
    private void saveEnvironment() {
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("environment", MODE_PRIVATE);
    	Editor editor = sharedPreferences.edit();
    	editor.putInt("state", state);
    	editor.putLong("time", time);
    	editor.putLong("startTime", startTime);
    	editor.commit();
    	
    	//保存分记数据
    	SharedPreferences sharedPreferencesMarks = getSharedPreferences("marks", MODE_PRIVATE);
    	Editor editorMarks = sharedPreferencesMarks.edit();
    	editorMarks.clear();
    	for(Long mark : marks){
    		editorMarks.putLong(""  + marks.indexOf(mark), mark.longValue());
    	}
    	editorMarks.commit();
    	Toast.makeText(this, "当前环境已保存", Toast.LENGTH_LONG).show();
    	*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugLog.debug(this, "onPause");
        //if (state == STATE_RUNNING){//如果正在运行
        //	handler.removeCallbacks(this);
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugLog.debug(this, "onResume");
        //if (state == STATE_RUNNING){//如果正在运行
        //	handler.post(this);
        //}
    }

    @Override
    protected void onStop() {
        super.onStop();
        DebugLog.debug(this, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DebugLog.debug(this, "onDestroy");
        //停表处理
        stopStopWatch(this);
        //中止DB的连接
        if (mApplication.getDb().inTransaction()) {
            mApplication.getDb().endTransaction();
        }
        mHandler.setStopWatch(null);
        //保存环境
        saveEnvironment();

    }

    /**
     * 点击重置
     *
     * @param
     */
    protected void onResetClick() {
        //设置状态为停止
        state = STATE_STOP;

        //不再刷新
        if (state == STATE_RUNNING) {//如果正在运行
            handler.removeCallbacks(this);
        }

        //初始化分记数组
        marks = new ArrayList<Long>();
        refreshMarkList();

        //设置时间显示
        time = 0;
        timeView.setText(getFormatTime(time));

    }

    /**
     * 点击暂停
     *
     * @param
     */
    public void onPauseClick() {
        //不再刷新
        if (state == STATE_RUNNING) {//如果正在运行
            handler.removeCallbacks(this);
        }

        //测试模式时需要记录成绩
        if (Parameter.DO_TYPE_TEST.equals(do_type)) {
            Chenji chenji = new Chenji();
            DebugLog.debug(this, "dbNoDAO.getNo:" + dbNoDAO.getNo(Parameter.NO_TYPE_CHENJI));
            chenji.setChenji_no(dbNoDAO.getNo(Parameter.NO_TYPE_CHENJI));
            chenji.setSporter_no(arraySporter.get(loopCount - 1).getSporter_no());
            chenji.setModel_no(selectedModel.getModel_no());
            chenji.setModel_total_length(selectedModel.getModel_length());
            chenji.setModel_total_speed(getSpeed(time, chenji.getModel_total_length()));
            chenji.setModel_total_time(time);
            chenji.setChenji_day(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
            dbChenjiDAO.addChenji(chenji);

            mApplication.getDb().setTransactionSuccessful();
            mApplication.getDb().endTransaction();
        }

        //设置状态为暂停
        state = STATE_PAUSE;
    }

    /**
     * 点击开始
     *
     * @param
     */
    public void onStartClick() {
        startTime = new Date().getTime() - time;
        lastTime = 0;

        handler.post(this);

        //设置状态为正在运行
        state = STATE_RUNNING;

        //开始事务处理
        dbNoDAO.getNewNo(Parameter.NO_TYPE_CHENJI);
        //测试模式时需要记录成绩
        if (Parameter.DO_TYPE_TEST.equals(do_type)) {
            mApplication.getDb().beginTransaction();
        }
    }

    /**
     * 点击分记
     *
     * @param
     */
    public void onMarkClick() {

        if (time == 0) {
            return;
        }
        // 添加分记数据, 最近的加在最前面
        marks.add(0, time);

        //刷新列表
        refreshMarkList();

        //测试模式时需要记录成绩
        if (Parameter.DO_TYPE_TEST.equals(do_type)) {
            ModelDetail modelDetail = modelDetails.get(count - 1);
            ChenjiDetail chenjiDetail = new ChenjiDetail();
            chenjiDetail.setChenji_no(dbNoDAO.getNo(Parameter.NO_TYPE_CHENJI));
            chenjiDetail.setModel_sub_length(modelDetail.getModel_sub_length());
            chenjiDetail.setModel_sub_no(modelDetail.getModel_sub_no());
            chenjiDetail.setModel_sub_speed(getSpeed(marks.get(0)));
            chenjiDetail.setModel_sub_time(time - lastTime);
            dbChenjiDAO.addChenjiDetail(chenjiDetail);
        }

        lastTime = time;
    }

    //接收到信号
    public void click() {

        DebugLog.debug(this, "modelDetails.size():" + modelDetails.size());
        DebugLog.debug(this, "count:" + count);

        if (stopWatchFlag) {
            return;
        }

        if (starFlag == false) {
            //考虑到画面动作掩饰，所有控制动作先做
            //自动控制时
            if (!Parameter.WIFI_NOT_CONTROL) {
                //跳过停止信号，自动发送下一轮信号
                MessageHelper.sendMsg(mApplication, true);
            }
            onStartClick();
            loopCount++;
            starFlag = true;
            count++;
            //一个计时周期结束
        } else if (modelDetails.size() == count) {
            //考虑到画面动作掩饰，所有控制动作先做
            //为测试模式，且所有运动员都已经训练结束时，停表
            if (Parameter.DO_TYPE_TEST.equals(do_type) && arraySporter.size() == loopCount) {
                //自动控制时
                if (!Parameter.WIFI_NOT_CONTROL) {
                    //发送最后一组信号
                    MessageHelper.sendMsg(mApplication, false);
                }
                stopWatchFlag = true;
                handler.removeCallbacks(this);
                ((TextView) findViewById(R.id.state)).setText("停表");
            //为训练模式，发送完最后一组信号后，自动开始下一轮
            } else {
                //自动控制时
                if (!Parameter.WIFI_NOT_CONTROL) {
                    //发送最后一组信号
                    MessageHelper.sendMsg(mApplication, false);

                    //给各个测试柱线程设置各柱子信号序列
                    //mApplication.getHandler().setClientInf(mApplication);
                    //跳过停止信号，自动发送下一轮信号
                    //发送最后一组数据后，自动发送下一轮信号
                    //mApplication.getHandler().sendMsg(mApplication,true);

                    //由于硬件反应有延时，等待1秒再开始下一轮测试
                    //给各个测试柱线程设置各柱子信号序列
                    //发送最后一组数据后，自动发送下一轮信号
                    (new TrainWaitThread(mHandler, true)).start();
                }
            }
            onMarkClick();
            onPauseClick();
            time = 0;
            count = 0;
            starFlag = false;
        } else {
            //自动控制时
            if (!Parameter.WIFI_NOT_CONTROL) {
                //跳过停止信号，自动发送下一轮信号
                MessageHelper.sendMsg(mApplication, true);
            }
            onMarkClick();
            count++;
        }
    }

    //测试下一个循环开始等候线程
    public class TrainWaitThread extends Thread {
        SmartSportHandler mHandler = null;
        boolean waitFlag = false;

        public TrainWaitThread(SmartSportHandler mHandler, boolean waitFlag) {
            this.mHandler = mHandler;
            this.waitFlag = waitFlag;
        }

        public synchronized void run() {
            try {
                //由于硬件反应有延时，等待2秒再开始下一轮测试
                if (waitFlag) {
                    try {
                        Thread.sleep(Parameter.CHECK_WAIT_TIME * 2);
                    } catch (Exception e) {
                    }
                }

                MessageHelper.setClientInf(mHandler.getApplication());
                MessageHelper.sendMsg(mHandler.getApplication(), true);
                this.interrupt();
            } catch (Exception e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
    }

    /**
     * 刷新列表
     */
    private void refreshMarkList() {
        //显示
        if (marks.size() > 0) {
            StopWatchList stopWatchList = new StopWatchList();
            //头部情报设定
            stopWatchList.head_sporter = "第" + loopCount + "组";
            if (Parameter.DO_TYPE_TEST.equals(do_type)) {
                stopWatchList.head_sporter_no = arraySporter.get(loopCount - 1).getSporter_no() + "";
                stopWatchList.head_sporter = arraySporter.get(loopCount - 1).getSporter_name();
                //训练时由于没有运动员信息，且不需要记录成绩，只需要记录轮次就可以了
            } else {
                stopWatchList.head_sporter_no = loopCount + "";
            }
            stopWatchList.head_time = "时间（s）";
            stopWatchList.head_speed = "速度（m/s）";
            //单行情报设定
            stopWatchList.point = getString(R.string.stopwatch_point) + count;
            stopWatchList.time = getFormatTime(marks.get(0));
            stopWatchList.speed = getFormatSpeed(marks.get(0));
            stopWatchLists.add(stopWatchList);
            MyListAdapter myListAdapter = new MyListAdapter(this);
            listView.setAdapter(myListAdapter);
            listView.setSelection(stopWatchLists.size());

            //下个计时周期开始前计时界面的刷新
            //mHandler.obtainMessage(WifiServer.STATE_STOPWATCH_LIST_CHANGER, -1, -1,"").sendToTarget();
            //Log.i("STATE_STOPWATCH_LIST_CHANGER", "刷新成绩列表1");
        }
    }


    public void flushText(String text) {
        // 调试模式时，显示信息框
        if(Parameter.DO_DEBUG_FLAG) {
            testText.setText(testText.getText() + "\r\n" + text);
            testText.setScrollY(testText.getLineCount() * testText.getLineHeight() - testText.getHeight());
        }
    }

    public class StopWatchList {
        public String head_sporter_no;
        public String head_sporter;
        public String head_time;
        public String head_speed;
        public String point;
        public String time;
        public String speed;
    }

    public class StopWatchListItem {
        public LinearLayout chenji_head;
        public TextView head_sporter;
        public TextView head_time;
        public TextView head_speed;
        public TextView point;
        public TextView time;
        public TextView speed;
    }

    private class MyListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return stopWatchLists.size();
        }

        public Object getItem(int position) {
            return stopWatchLists.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            StopWatchListItem stopWatchListItem = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.stopwatch_activity_item, null);
                stopWatchListItem = new StopWatchListItem();
                stopWatchListItem.chenji_head = (LinearLayout) convertView.findViewById(R.id.chenji_head);
                stopWatchListItem.head_sporter = (TextView) convertView.findViewById(R.id.head_sporter);
                stopWatchListItem.head_time = (TextView) convertView.findViewById(R.id.head_time);
                stopWatchListItem.head_speed = (TextView) convertView.findViewById(R.id.head_speed);
                stopWatchListItem.point = (TextView) convertView.findViewById(R.id.point);
                stopWatchListItem.time = (TextView) convertView.findViewById(R.id.time);
                stopWatchListItem.speed = (TextView) convertView.findViewById(R.id.speed);
                convertView.setTag(stopWatchListItem);
            } else {
                stopWatchListItem = (StopWatchListItem) convertView.getTag();
            }

            stopWatchListItem.head_sporter.setText(stopWatchLists.get(position).head_sporter);
            stopWatchListItem.head_time.setText(stopWatchLists.get(position).head_time);
            stopWatchListItem.head_speed.setText(stopWatchLists.get(position).head_speed);
            stopWatchListItem.point.setText(stopWatchLists.get(position).point);
            stopWatchListItem.time.setText(stopWatchLists.get(position).time);
            stopWatchListItem.speed.setText(stopWatchLists.get(position).speed);

            //运动员、圈数一致时，提示行不再显示
            if (position > 0) {
                String preSporter_no = stopWatchLists.get(position - 1).head_sporter_no;
                String sporter_no = stopWatchLists.get(position).head_sporter_no;
                if (preSporter_no.equals(sporter_no)) {
                    stopWatchListItem.chenji_head.setVisibility(View.GONE);
                } else {
                    stopWatchListItem.chenji_head.setVisibility(View.VISIBLE);
                }
            } else {
                stopWatchListItem.chenji_head.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }


    public void run() {
        handler.postDelayed(this, 10);
        time = new Date().getTime() - startTime;
        timeView.setText(getFormatTime(time));
    }

    /**
     * 得到一个格式化的时间
     *
     * @param time 时间 毫秒
     * @return 分：秒：毫秒
     */
    private String getFormatTime(long time) {
        long millisecond = time % 1000;
        //long second = (time / 1000) % 60;
        //long minute = time / 1000 / 60;
        long second = time / 1000;

        //秒以下显示二位
        String strMillisecond = "";
        if (millisecond / 10 >= 10) {
            strMillisecond = "" + (millisecond / 10);
        } else {
            strMillisecond = "0" + (millisecond / 10);
        }

        //秒显示二位
        String strSecond = "";
        strSecond = "" + second % 60;
        if (strSecond.length() < 2) {
            strSecond = "0" + second % 60;
        }

        //分钟至少二位
        String strMinute = "";
        if (second >= 60) {
            if (second >= 600) {
                strMinute = "" + second / 60;
            } else {
                strMinute = "0" + second / 60;
            }
        } else {
            strMinute = "00";
        }

        return strMinute + ":" + strSecond + ":" + strMillisecond;
    }

    /**
     * 得到一个格式化的速度
     *
     * @param time 时间 毫秒
     * @return 米/秒
     */
    private String getFormatSpeed(long time) {
        DebugLog.debug(this, "getFormatSpeed.count():" + count);

        String reString = "";
        double speed = getSpeed(time);
        if (speed != 0) {
            String sSpeed = speed + "";
            if (sSpeed.lastIndexOf(".") == sSpeed.length() - 2) {
                sSpeed = sSpeed + "0";
            }
            reString = sSpeed;
        }

        return reString;
    }

    private double getSpeed(long time) {
        BigDecimal showSpeeDecimal = null;
        if (modelDetails.get(count - 1).getModel_sub_length() > 0) {
            long thisTime = time - lastTime;
            double second = thisTime / 1000.00;
            double speed = modelDetails.get(count - 1).getModel_sub_length() / second;
            showSpeeDecimal = new BigDecimal(speed);
            showSpeeDecimal = showSpeeDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        if (showSpeeDecimal == null) {
            return 0;
        } else {
            return showSpeeDecimal.doubleValue();
        }
    }

    /*
     * 计算速度
     *
     * @param time，length
     * @return speed(米/秒)
     */
    private double getSpeed(double time, long length) {
        DebugLog.debug(this, "time:" + time);
        DebugLog.debug(this, "length" + length);

        BigDecimal showSpeed = new BigDecimal("0.00");
        if (length > 0) {
            showSpeed = (new BigDecimal(length * 1.00 * 1000.00 / time)).setScale(2, BigDecimal.ROUND_HALF_UP);
        }

        return showSpeed.doubleValue();
    }

    //停表按钮事件
    class StopWatchOnClickListener implements OnClickListener {
        StopWatchActivity stopWatch;

        public StopWatchOnClickListener(StopWatchActivity stopWatch) {
            this.stopWatch = stopWatch;
        }

        public void onClick(View v) {
            stopStopWatch(stopWatch);
        }
    }

    //停表处理
    public void stopStopWatch(StopWatchActivity stopWatch) {
        stopWatch.stopWatchFlag = true;
        stopWatch.handler.removeCallbacks(stopWatch);
        ((TextView) stopWatch.findViewById(R.id.state)).setText("停表");
        //自动控制时，给所有信号柱发送等待中信号
        if (!Parameter.WIFI_NOT_CONTROL) {
            MessageHelper.sendMsg(stopWatch.mApplication, Parameter.CONNECT_INF_WAIT, false);
        }
    }

    //手动计时按钮事件
    class ClickWatchOnClickListener implements OnClickListener {
        StopWatchActivity stopWatch;

        public ClickWatchOnClickListener(StopWatchActivity stopWatch) {
            this.stopWatch = stopWatch;
        }

        public void onClick(View v) {
            stopWatch.click();
        }
    }

}

