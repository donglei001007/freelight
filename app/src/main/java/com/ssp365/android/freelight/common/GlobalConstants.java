package com.ssp365.android.freelight.common;

/**
 * Created by donglei on 2016/8/19.
 */
public class GlobalConstants {

    /* wifi链接状态*/
    /**
     * we're doing nothing
     */
    public static final int STATE_WIFI_NONE = 10;
    /**
     * AP热点开启
     */
    public static final int STATE_WIFI_START = 11;
    /**
     * 客户端连接
     */
    public static final int STATE_WIFI_ACCEPT = 12;
    /**
     * 从客户端读取信息
     */
    public static final int STATE_WIFI_READ = 13;
    /**
     * 发送信道给客户端
     */
    public static final int STATE_WIFI_WRITE = 14;
    /**
     * 连接断开
     */
    public static final int STATE_WIFI_LOST = 15;
    /**
     * 客户端连接
     */
    public static final int STATE_WIFI_ACCEPT_FAILE = 16;
    /**
     * 通信失败
     */
    public static final int STATE_WIFI_CONNECT_FAILE = 17;
    /**
     * 按钮按钮信息变化
     */
    public static final int STATE_BUTTON_CHANGER = 18;
    /**
     * 计时器计时列表刷新
     */
    public static final int STATE_STOPWATCH_LIST_CHANGER = 19;
    /**
     * 发送秒表按下事件
     */
    public static final int STATE_WIFI_CLICK = 20;
    /**
     * 测试
     */
    public static final int STATE_WIFI_TEST = 99;
}
