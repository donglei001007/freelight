package com.ssp365.android.freelight.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.ssp365.android.freelight.activity.WifisetActivity.ClientList;
import com.ssp365.android.freelight.common.CharHelper;
import com.ssp365.android.freelight.common.DebugLog;
import com.ssp365.android.freelight.common.GlobalConstants;
import com.ssp365.android.freelight.common.SmartSportApplication;
import com.ssp365.android.freelight.common.SmartSportHandler;
import com.ssp365.android.freelight.model.Parameter;

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * WIFI热点服务器
 *
 * @author chenxy 2015/09/09
 *         donglei 2016/08/28
 * @version 1.0.0.1
 */
public class WifiServer {

    // APP程序句柄
    public final SmartSportHandler mHandler;
    // 全局变量
    public SmartSportApplication mApplication = null;
    // 客户端socket列表
    public ArrayList<ClientList> clientSocketList = new ArrayList<>();
    public ArrayList<ConnectedThread> clientConnectedThreadList = new ArrayList<>();
    // 定义一个WifiLock（防止锁屏时候，android自动断开wifi连接）
    WifiLock mWifiLock;
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 监听客户端用socket
    private ServerSocket serverSocket = null;
    private AcceptThread mAcceptThread = null;
    private Context context = null;
    // wifiserver状态
    private int mState;

    /**
     * 构造函数
     *
     * @param context     上下文
     * @param application Application对象
     */
    public WifiServer(Context context, SmartSportApplication application) {
        // 获得程序参数
        this.mApplication = application;
        // 获得上下文
        this.context = context;
        // 获得事件处理器
        this.mHandler = mApplication.getHandler();
        // 设定当前状态为什么也没做
        mState = GlobalConstants.STATE_WIFI_NONE;
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();

        // WIFI信息打印
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("WIFIAP服务器创建，网络信息：");
        sb.append("\nipAddress：" + CharHelper.intToIp(dhcpInfo.ipAddress));
        sb.append("\nnetmask：" + CharHelper.intToIp(dhcpInfo.netmask));
        sb.append("\ngateway：" + CharHelper.intToIp(dhcpInfo.gateway));
        sb.append("\nserverAddress：" + CharHelper.intToIp(dhcpInfo.serverAddress));
        sb.append("\ndns1：" + CharHelper.intToIp(dhcpInfo.dns1));
        sb.append("\ndns2：" + CharHelper.intToIp(dhcpInfo.dns2));
        sb.append("\n");
        sb.append("Wifi信息：");
        sb.append("\nIpAddress：" + CharHelper.intToIp(mWifiInfo.getIpAddress()));
        sb.append("\nMacAddress：" + mWifiInfo.getMacAddress());
        DebugLog.debug(context, sb.toString());
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        // 如果AP已经开启，先关闭热点
        if (isApEnabled()) {
            // 关闭热点
            setWifiApEnabled(false);
        }
        // 打开热点
        setWifiApEnabled(true);

        // 重新初始化所有连接的客户端
        for (int i = 0; i < clientConnectedThreadList.size(); i++) {
            if (clientConnectedThreadList.get(i) != null) {
                // 关闭客户端连接线程
                clientConnectedThreadList.get(i).cancel();
            }
            // 移除客户端
            clientConnectedThreadList.remove(i);
        }
        // 初始化客户端列表
        clientConnectedThreadList = new ArrayList<>();

        // 启动监听进程
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(this);
            mAcceptThread.start();
        }
        setState(GlobalConstants.STATE_WIFI_START);
        // 发送热点启动信息
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_START, -1, -1).sendToTarget();
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        // 关闭监听进程
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // 关闭所有客户端
        for (int i = clientConnectedThreadList.size() - 1; i >= 0; i--) {
            if (clientConnectedThreadList.get(i) != null) {
                // 如果线程已经结束，释放线程资源
                clientConnectedThreadList.get(i).cancel();
            }
            clientConnectedThreadList.remove(i);
        }

        // 关闭热点
        setWifiApEnabled(false);
        // 设置wifi状态
        setState(GlobalConstants.STATE_WIFI_NONE);
        // 发送热点关闭信息
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_NONE, -1, -1).sendToTarget();
    }

    /**
     * 设置WIFI状态
     */
    private synchronized void setState(int state) {
        mState = state;
    }

    /**
     * 发送连接断开信号
     */
    private void connectionLost(int no) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_LOST, no, -1).sendToTarget();
    }

    /**
     * 发送通信失败信号
     */
    private void connectionFail(int no) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_CONNECT_FAILE, no, -1).sendToTarget();
    }

    /**
     * 发送读取信息信号
     */
    private void connectionRead(int no, String str) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_READ, -1, no, str).sendToTarget();
    }

    /**
     * 发送写入信息信号
     */
    private void connectionWrite(int no, String msg) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_WRITE, -1, no, msg).sendToTarget();
    }

    /**
     * 处理灯柱的通过时间
     */
    private void connectionClick(int no, String str) {
        mHandler.obtainMessage(GlobalConstants.STATE_WIFI_CLICK, -1, no, str).sendToTarget();
    }

    /**
     * 打开AP热点
     *
     * @param enabled true：打开AP；false：关闭AP
     * @return 操作是否成功
     */
    public boolean setWifiApEnabled(boolean enabled) {
        // disable WiFi in any case
        if (enabled) {
            mWifiManager.setWifiEnabled(false);
        }

        try {
            // WIFI设定
            WifiConfiguration apConfig = new WifiConfiguration();
            // 2016/03/27 chenxy update start
            apConfig.SSID = Parameter.WIFI_SSID;
            // 2016/03/27 chenxy update end
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            // 2016/03/27 chenxy update start
            apConfig.preSharedKey = Parameter.WIFI_SHARED_KEY;
            // 2016/03/27 chenxy update end

            // 关闭热点前，先解开AP锁（AP锁可以防止黑屏时WIFI自动关闭）
            if (!enabled) {
                releaseWifiLock();
            }

            // 获得设定WIFIAP的方法
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean reFlag = (Boolean) method.invoke(mWifiManager, apConfig, enabled);

            // 打开热点后，先打开AP锁（AP锁可以防止黑屏时WIFI自动关闭）
            if (enabled) {
                creatWifiLock();
            }
            DebugLog.debug(context, "WifiApEnabled:" + enabled);

            return reFlag;
        } catch (Exception e) {
            DebugLog.debug(context, "Cannot set WiFi AP state：" + e);
            return false;
        }
    }

    /*
     * 返回AP状态
     */
    public int getWifiApState() {
        try {
            // 因为此方法是@hide标注，故只能反射取得（可能存在兼容性问题）
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            return (Integer) method.invoke(mWifiManager);
        } catch (Exception e) {
            DebugLog.debug(context, "Cannot get WiFi AP state：" + e);
            return WifiManager.WIFI_STATE_UNKNOWN;
        }
    }

    /*
     * 返回AP状态，如果AP已经启动或者启动中，
     * 则返回true
     */
    public boolean isApEnabled() {
        int state = getWifiApState();
        return WifiManager.WIFI_STATE_ENABLING == state || WifiManager.WIFI_STATE_ENABLED == state;
    }

    /*
     * 返回WIFI状态，如果WIFI已经启动或者启动中，
     * 则返回true
     */
    public boolean isWifiEnabled() {
        int state = mWifiManager.getWifiState();
        return WifiManager.WIFI_STATE_ENABLING == state || WifiManager.WIFI_STATE_ENABLED == state;
    }

    //打开WIFI
    public void OpenWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭WIFI
    public void CloseWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    //解锁WifiLock
    public void releaseWifiLock() {
        // 判断wifi是否被lock锁持用
        if (mWifiLock.isHeld()) {
            // 释放WIFI锁
            mWifiLock.release();
        }
    }

    /**
     * 创建一个WifiLock
     */
    public void creatWifiLock() {
        // 创建WIFILOCK
        mWifiLock = mWifiManager.createWifiLock(Parameter.WIFI_LOCK_NAME);
        // WIFI加锁
        mWifiLock.acquire();
    }

    public Context getContext() {
        return context;
    }

    public WifiManager getmWifiManager() {
        return mWifiManager;
    }

    public ArrayList<ClientList> getClientSocketList() {
        return clientSocketList;
    }

    public ArrayList<ConnectedThread> getClientConnectedThreadList() {
        return clientConnectedThreadList;
    }

    public SmartSportHandler getmHandler() {
        return mHandler;
    }
}
