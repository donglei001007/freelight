package com.ssp365.android.freelight.common;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ssp365.android.freelight.model.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 通过Debug来调试程序
 * DEBUG_FLAG = FALSE  实际运行调试模式，会自动生成log文件
 * DEBUG_FLAG = TRUE   模式器调试模式，会自动输出log到控制台
 *
 * @author chenxy 2014/6/8
 * @version 1.0
 */
public class DebugLog {

    //log文件用句柄
    private static BufferedWriter bwOutputDebug = null;

    /**
     * 输出log
     */
    public static void debug(Context context, String debugLog) {
        try {
            // 如果为测试环境才打印测试日志
            if (Parameter.WIFI_DEBUG_FLAG) {
                String msg = new SimpleDateFormat("HHmmssSSS").format(Calendar.getInstance().getTime()) + ":" + debugLog;
                bwOutputDebug.write(msg);
                bwOutputDebug.newLine();
                bwOutputDebug.flush();
                // 在logcat之中也输出日志
                Log.d("freelight", msg);
            }
        } catch (IOException e) {
            Log.e("freelight", "日志输出发生问题！" + e);
        }
    }

    /**
     * 输出log的开启
     */
    public static void openDebugFile(Context context) {
        try {
            String file_path_str;
            // 优先保存到SD卡中
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                file_path_str = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "freeLight";
                // 如果SD卡不存在，就保存到本应用的目录下
            } else {
                file_path_str = context.getFilesDir().getAbsolutePath();
            }

            // 创建日志目录
            File file_path = new File(file_path_str);
            // 路径不存在时，建立路径
            if (!file_path.exists()) {
                file_path.mkdirs();
            }

            // 创建日志文件
            StringBuffer fileNameSb = new StringBuffer();
            fileNameSb.append("log_").append(new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())).append(".txt");
            File logFile = new File(file_path_str + File.separator + fileNameSb.toString());
            bwOutputDebug = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), "GB2312"), 10240);
        } catch (Exception e) {
            Log.e("freelight", "日志打开发生问题！" + e);
        }
    }

    /**
     * 输出log的关闭
     */
    public static void closeDebugFile() {
        try {
            // 测试日志功能开启时，输出日志
            if (Parameter.WIFI_DEBUG_FLAG) {
                if (bwOutputDebug != null) {
                    bwOutputDebug.close();
                    bwOutputDebug = null;
                }
            }
        } catch (Exception e) {
            Log.e("freelight", "日志关闭发生问题！" + e);
        }
    }

}
