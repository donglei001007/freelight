package com.ssp365.android.freelight.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Vector;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DBNAME = "ac.db";

    private static final String TAG = "DBTeamOpenHelper";

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreateDB");

        StringBuffer sb = new StringBuffer();

        //表格：队伍
        sb.append("create table t_team (");
        sb.append("		[team_no] integer NOT NULL,");                        //编号
        sb.append("		[team_name] nvarchar(15) NOT NULL,");                //队名
        sb.append("		[team_name_py] varchar(15) NOT NULL,");                //队名拼音首字母
        sb.append("		Primary Key(team_no));");
        db.execSQL(sb.toString());

        //表格：运动员
        sb = new StringBuffer();
        sb.append("create table t_sporter (");
        sb.append("		[sporter_no] integer NOT NULL,");                    //编号
        sb.append("		[sporter_name] nvarchar(5) NOT NULL,");                //姓名
        sb.append("		[sporter_name_py] varchar(5) NOT NULL,");            //姓名拼音首字母
        sb.append("		[sporter_xingbie] char(1) NOT NULL,");                //性别
        sb.append("		[sporter_team_no] integer NOT NULL,");                //所在队
        sb.append("		[sporter_birthday] char(10) NULL,");                //生日
        sb.append("		[sporter_shengao] varchar(20) NULL,");                //身高
        sb.append("		[sporter_tizhong] varchar(20) NULL,");                //体重
        sb.append("		Primary Key(sporter_no));");
        db.execSQL(sb.toString());

        //表格：模式
        sb = new StringBuffer();
        sb.append("create table t_model (");
        sb.append("		[model_no] integer NOT NULL,");                        //编号
        sb.append("		[model_name] varchar(50) NOT NULL,");                //模式名字
        sb.append("		[model_name_py] varchar(50) NOT NULL,");            //模式名字拼音
        sb.append("		[model_pic] varchar(50) NOT NULL,");                //模式关联图片(自定义的模式没有图片，设为空格)
        sb.append("		[model_count] integer NOT NULL,");                    //模式计时次数
        sb.append("		[model_point_count] integer NOT NULL,");            //模式测试柱个数（当随机跑和往返跑时，计时次数和测试柱个数不一致）
        sb.append("		[model_length] integer NOT NULL,");                    //模式总距离(自定义的模式没有距离，设为-1)
        sb.append("		[model_fdji] integer NOT NULL,");                    //分段计时标志位(0:无分段计时，1:有分段计时)
        sb.append("		Primary Key(model_no));");
        db.execSQL(sb.toString());

        Vector<String> vecSQL = new Vector<String>();
        //冲刺跑_10m
        vecSQL.add("insert into t_model (model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji) values(1,'冲刺跑_10m','ccp_10m','pb_ccp',2,2,10,0);");
        //冲刺跑_30m
        vecSQL.add("insert into t_model (model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji) values(2,'冲刺跑_30m','ccp_30m','pb_ccp',2,2,30,0);");
        //冲刺跑_50m
        vecSQL.add("insert into t_model (model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji) values(3,'冲刺跑_50m','ccp_50','pb_ccp',2,2,50,0);");
        //折返跑_20m
        vecSQL.add("insert into t_model (model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji) values(4,'折返跑_20m','zfp_20m','pb_zfp',4,3,20,1);");
        //分段计时跑_30m
        vecSQL.add("insert into t_model (model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji) values(5,'分段计时跑_30m','fdjip_30m','pb_fdjsp',4,4,30,1);");
        //随机反应跑
        vecSQL.add("insert into t_model (model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji) values(6,'随机反应跑','sjfyp','pb_sjfyp',2,4,5,0);");
        //随机判断反应跑
        vecSQL.add("insert into t_model (model_no,model_name,model_name_py,model_pic,model_count,model_point_count,model_length,model_fdji) values(7,'随机判断反应跑','sjpdfyp','pb_sjpdfyp',2,4,5,0);");
        for (int i = 0; i < vecSQL.size(); i++) {
            db.execSQL(vecSQL.get(i));
        }

        //表格：模式详细
        sb = new StringBuffer();
        sb.append("create table t_model_detail (");
        sb.append("		[model_no] integer NOT NULL,");                        //编号
        sb.append("		[model_sub_no] integer NOT NULL,");                    //计时点编号(起始点为1，-1表示除前一点外的随机一点亮
        //			-2表示除前一点外的随机全亮，只有一点为计时点）
        sb.append("		[model_pre_no] integer NOT NULL,");                    //前计时点编号
        sb.append("		[model_sub_length] integer NOT NULL,");                //计时区间距离(0为不在意距离)
        sb.append("		[model_sub_position_type] char(1) NOT NULL,");        //和前计时点的位置关系
        //0:和前点同一位置，1:0点方向，2:1.5点方向，3:3点方向，4:4.5点方向，
        //5:6点方向,6:7.5点方向，7:9点方向，8:11.5点方向
        sb.append("		[model_sub_check_type] char(1) NOT NULL,");            //计时点类别(0:压控开关，1:激光开关)
        sb.append("		Primary Key(model_no,model_sub_no));");
        db.execSQL(sb.toString());

        vecSQL = new Vector<String>();
        //冲刺跑_10m
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(1,2,1,10,3,'1');");
        //冲刺跑_30m
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(2,2,1,30,3,'1');");
        //冲刺跑_50m
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(3,2,1,50,3,'1');");
        //折返跑_20m
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(4,2,1,5,3,'1');");
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(4,3,2,10,7,'1');");
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(4,1,3,5,3,'1');");
        //分段计时跑_30m
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(5,2,1,10,3,'1');");
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(5,3,2,10,3,'1');");
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(5,4,3,10,3,'1');");
        //随机反应跑
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(6,-1,1,5,3,'1');");
        //随机判断反应跑
        vecSQL.add("insert into t_model_detail (model_no,model_sub_no,model_pre_no,model_sub_length,model_sub_position_type,model_sub_check_type) values(7,-2,1,5,3,'1');");
        for (int i = 0; i < vecSQL.size(); i++) {
            db.execSQL(vecSQL.get(i));
        }


        //表格：测试柱状态
        sb = new StringBuffer();
        sb.append("create table t_point_state (");
        sb.append("		[model_no] integer NOT NULL,");                        //模式编号
        sb.append("		[model_index] integer NOT NULL,");                    //测试状态编号（1为预备，按测试顺序编号）
        sb.append("		[point_no] integer NOT NULL,");                        //测试柱编号
        sb.append("		[wait_state] char(1) NOT NULL,");                    //响应信号否
        //	0：响应  , 1：不响应  , 2：随机判断中
        //	3：响应结束
        sb.append("		[point_state]  char(2) NOT NULL,");                    //给信号柱发送的状态
        //14:起点中
        //12:通过后
        //15:记录中
        //16:干扰信号
        //17:灭灯
        //-1:随机跑
        //-2:随机判断跑
        sb.append("		Primary Key(model_no,model_index,point_no));");
        db.execSQL(sb.toString());

        vecSQL = new Vector<String>();
        //冲刺跑_10m
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(1,1,1,'0','14');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(1,1,2,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(1,2,1,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(1,2,2,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(1,3,1,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(1,3,2,'3','12');");
        //冲刺跑_30m
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(2,1,1,'0','14');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(2,1,2,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(2,2,1,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(2,2,2,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(2,3,1,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(2,3,2,'3','12');");
        //冲刺跑_50m
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(3,1,1,'0','14');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(3,1,2,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(3,2,1,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(3,2,2,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(3,3,1,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(3,3,2,'3','12');");
        //折返跑_20m
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,1,1,'0','14');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,1,2,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,1,3,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,2,1,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,2,2,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,2,3,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,3,1,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,3,2,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,3,3,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,4,1,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,4,2,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,4,3,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,5,1,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,5,2,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(4,5,3,'3','12');");
        //分段计时跑_30m
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,1,1,'0','14');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,1,2,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,1,3,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,1,4,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,2,1,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,2,2,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,2,3,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,2,4,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,3,1,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,3,2,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,3,3,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,3,4,'1','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,4,1,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,4,2,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,4,3,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,4,4,'0','15');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,5,1,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,5,2,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,5,3,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(5,5,4,'3','12');");
        //随机反应跑
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,1,1,'0','14');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,1,2,'1','17');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,1,3,'1','17');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,1,4,'1','17');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,2,1,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,2,2,'2','-1');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,2,3,'2','-1');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,2,4,'2','-1');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,3,1,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,3,2,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,3,3,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(6,3,4,'3','12');");
        //随机判断反应跑
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,1,1,'0','14');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,1,2,'1','17');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,1,3,'1','17');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,1,4,'1','17');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,2,1,'1','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,2,2,'2','-2');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,2,3,'2','-2');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,2,4,'2','-2');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,3,1,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,3,2,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,3,3,'3','12');");
        vecSQL.add("insert into t_point_state (model_no,model_index,point_no,wait_state,point_state) values(7,3,4,'3','12');");
        for (int i = 0; i < vecSQL.size(); i++) {
            db.execSQL(vecSQL.get(i));
        }


        //表格：采番表
        Log.i(TAG, "create table t_no start");
        sb = new StringBuffer();
        sb.append("create table t_no (");
        sb.append("		[no_type] char(2) NOT NULL,");                        //编号
        sb.append("		[no] integer NOT NULL,");                            //运动员编号
        sb.append("		Primary Key(no_type));");
        db.execSQL(sb.toString());
        Log.i(TAG, "create table t_no end");

        //模式自定义扩展使用(前500个作为系统预备项)
        sb = new StringBuffer();
        sb.append("insert into t_no (no_type,no) values('04',500);");
        db.execSQL(sb.toString());


        //表格：成绩（总）
        sb = new StringBuffer();
        sb.append("create table t_chenji (");
        sb.append("		[chenji_no] integer NOT NULL,");                    //编号
        sb.append("		[chenji_day] char(19) NOT NULL,");                    //测试日(yyyy-MM-dd HH:mm:ss)
        sb.append("		[sporter_no] integer NOT NULL,");                    //运动员编号
        sb.append("		[model_no] integer NOT NULL,");                        //模式编号
        sb.append("		[model_total_length] integer NOT NULL,");            //模式总距离（单位：米）
        sb.append("		[model_total_time] double(6,2) NOT NULL,");            //总时间（单位：秒，保留两位小数）
        sb.append("		[model_total_speed] double(6,2) NOT NULL,");        //平均速度（单位：米/秒，保留两位小数）
        sb.append("		Primary Key(chenji_no));");
        db.execSQL(sb.toString());

        //表格：成绩（分区间）e
        sb = new StringBuffer();
        sb.append("create table t_chenji_detail (");
        sb.append("		[chenji_no] integer NOT NULL,");                    //编号
        sb.append("		[model_sub_no] integer NOT NULL,");                    //计时点编号
        sb.append("		[model_sub_length] integer NOT NULL,");                //计时区间距离（单位：米）
        sb.append("		[model_sub_time] double(6,2) NOT NULL,");            //计时区间时间（单位：秒，保留两位小数）
        sb.append("		[model_sub_speed] double(6,2) NOT NULL,");            //计时区间速度（单位：米/秒，保留两位小数）
        sb.append("		Primary Key(chenji_no,model_sub_no));");
        db.execSQL(sb.toString());

        Log.i(TAG, "onCreateDB over");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgradeDB:" + oldVersion + "->" + newVersion);

        db.execSQL("DROP TABLE IF EXISTS t_team;");
        db.execSQL("DROP TABLE IF EXISTS t_sporter;");
        db.execSQL("DROP TABLE IF EXISTS t_model;");
        db.execSQL("DROP TABLE IF EXISTS t_model_detail;");
        db.execSQL("DROP TABLE IF EXISTS t_chenji;");
        db.execSQL("DROP TABLE IF EXISTS t_chenji_detail;");
        db.execSQL("DROP TABLE IF EXISTS t_no;");

        onCreate(db);
    }
}
