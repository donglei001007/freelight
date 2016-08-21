package com.ssp365.android.freelight.model;

import org.achartengine.chart.PointStyle;

public class Parameter {

    //蓝牙测试用标志：false为带蓝牙工作，true为不带蓝牙测试
    public static boolean WIFI_NOT_CONTROL = false;
    //WIFILOG打印用标志：false为不打印，true为打印
    public static boolean WIFI_DEBUG_FLAG = true;
    //调试用标志：false运行,调试界面不显示，true为调试，调试界面显示
    public static boolean DO_DEBUG_FLAG = false;


    //采番种类_成绩
    public static String NO_TYPE_CHENJI = "01";
    //采番种类_运动员
    public static String NO_TYPE_SPORTER = "02";
    //采番种类_队伍
    public static String NO_TYPE_TEAM = "03";
    //采番种类_模式
    public static String NO_TYPE_MODEL = "04";

    //计时模式_训练
    public static final String DO_TYPE_TRAIN = "TRAIN";
    //计时模式_测试
    public static final String DO_TYPE_TEST = "TEST";

    //连接状态_连接正常
    public static final String CONNECT_STATE_CONNECTED = "连接正常";
    //连接状态_连接断开
    public static final String CONNECT_STATE_LOST = "连接断开";

    //分析模式_成绩
    public static final int ANALYSE_TYPE_CHENJI = 0;
    //分析模式_成绩详细
    public static final int ANALYSE_TYPE_CHENJI_DETAIL = 1;

    //2016/03/27 chenxy add start
    public static final String WIFI_SSID = "freelight";
    public static final String WIFI_SHARED_KEY = "freelight";
    //2016/03/27 chenxy add end

    /*
    //控制模式_手动控制
    public static final boolean WIFI_CONTROL_YES = true;
	//控制模式_WIFI控制
    public static final boolean WIFI_CONTROL_NO = false;
    */
    //数据导出模式_保存导出成绩
    public static final int OUTPUT_DATA_SAVE = 0;
    //数据导出模式_删除导出成绩
    public static final int OUTPUT_DATA_DELETE = 1;

    //热点开启状态标志位
    public static boolean apRuningFlag = false;

    //颜色组合（10色）
    public static final int[] COLOR = new int[]{0xFFFFFFFF,                    //白色
            0xFFFF0000,                     //红色
            0xFF8A2BE2,                     //紫罗兰蓝色
            0xFFFFD700,                     //金色
            0xFFFF69B4,                     //热粉红色
            0xFF0000FF,                     //蓝色
            0xFFEE82EE,                     //紫罗兰色
            0xFFFFA500,                     //橙色
            0xFFAFEEEE,                     //苍宝石绿
            0xFF7CFC00                      //草绿色
    };
    //点形组合（5形）
    public static final PointStyle[] POINT_STYLE = new PointStyle[]{
            PointStyle.CIRCLE,
            PointStyle.DIAMOND,
            PointStyle.TRIANGLE,
            PointStyle.SQUARE,
            PointStyle.X};

    /** WIFI服务器连接端口 */
    public static int WIFI_SERVER_PORT = 30000;
    /** WIFI服务器连接端口（备用） */
    public static int WIFI_SERVER_PORT_BACKUP = 33333;

    /** WIFI服务器连接端口 */
    //重复发送信号等待时间（毫秒）
    public static int RESEND_WAIT_TIME = 3000;
    //扫描倒计时
    public static int CHECK_WAIT_TIME = 1000;
    //扫描倒计时次数
    public static int CHECK_TIMES = 5;
    // 最大重发次数
    public static int RESEND_MAX_COUNT = 3;

    //训练的间隔时间
    public static int TRAIN_WAIT_TIME = 3000;

    //传输用信号:起点开始
    public static String CONNECT_INF_START = "fe";
    //传输用信号:信号结束
    public static String CONNECT_INF_END = "ff";
    //传输用信号:等待中(03->11)
    public static String CONNECT_INF_WAIT = "11";
    //传输用信号:通过后(04->12)
    public static String CONNECT_INF_PASS = "12";

    //2016/03/27 chenxy del start
    ////传输用信号:识别中(10->13)
    //public static String CONNECT_INF_CHECK = "13";
    //2016/03/27 chenxy del end
    //2016/03/27 chenxy add start
    //传输用信号:标识一号灯柱
    public static String CONNECT_INF_POINT_1 = "31";
    //传输用信号:标识一号灯柱
    public static String CONNECT_INF_POINT_2 = "32";
    //传输用信号:标识一号灯柱
    public static String CONNECT_INF_POINT_3 = "33";
    //传输用信号:标识一号灯柱
    public static String CONNECT_INF_POINT_4 = "34";
    //2016/03/27 chenxy add end

    //传输用信号:起点中(11->14)
    public static String CONNECT_INF_START_POINT = "14";
    //传输用信号:记录中(真实)(12->15)
    public static String CONNECT_INF_RECORD_POINT_T = "15";
    //传输用信号:记录中(干扰)(13->16)
    public static String CONNECT_INF_RECORD_POINT_F = "16";
    //传输用信号:无信号（灭灯）(14->17)
    public static String CONNECT_INF_NONE = "17";

    //传输用信号:信号OK
    public static String CONNECT_INF_OK = "21";
    //传输用信号:信号NG
    public static String CONNECT_INF_NG = "22";
    //传输用信号:信号柱通过信号
    public static String CONNECT_INF_POINT_PASS = "23";

    //传输用信号:-1:随机跑
    public static String CONNECT_INF_RANDOM_NO_CHARGE = "-1";
    //传输用信号:-2:随机判断跑
    public static String CONNECT_INF_RANDOM_CHARGE = "-2";

    //等待响应信号:0：响应
    public static String WAIT_STATUS_DO = "0";
    //等待响应信号:1：不响应
    public static String WAIT_STATUS_NO_DO = "1";
    //等待响应信号:2：随机判断中
    public static String WAIT_STATUS_RANDOM = "2";
    //等待响应信号:3：响应结束
    public static String WAIT_STATUS_OVER = "3";

    // 运动员选择处理标志
    public static final int REQUEST_SPORTER_SELECT = 1;

    /** WIFI锁名称*/
    public static final String WIFI_LOCK_NAME = "freelight";

}