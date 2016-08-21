package com.ssp365.android.freelight.common;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.ssp365.android.freelight.model.Parameter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
    public static File logFile;
    public static String fileName;
    public static BufferedWriter bwOutputDebug;
    public static String fileHead = "";
    public static int fileNumber = 0;

    /*
     * 输出log
     */
    public static void debug(Context context, String debugLog) {
        if (fileName == null) {
            fileHead = "log_" + new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
            fileName = fileHead + "_" + fileNumber + ".txt";
            fileNumber++;
        } else {
            //文件尺寸》10k时，文件分割，生成新的Log。
            if (logFile.length() > 10000) {
                try {
                    bwOutputDebug.flush();
                    bwOutputDebug.close();
                    bwOutputDebug = null;

                    fileName = fileHead + "_" + fileNumber + ".txt";
                    fileNumber++;

                    String file_path_str = null;
                    File file_path = null;
                    // 优先保存到SD卡中
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        file_path_str = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "freeLight";
                        // 如果SD卡不存在，就保存到本应用的目录下
                    } else {
                        file_path_str = context.getFilesDir().getAbsolutePath();
                    }
                    file_path = new File(file_path_str);
                    logFile = new File(file_path_str + File.separator + fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //DEBUG_FLAG = 0  实际运行模式，不生成log文件
        if (Parameter.WIFI_DEBUG_FLAG) {
            outputDebugFile(context, debugLog, fileName);
            //DEBUG_FLAG = 2  模式器调试模式，会自动输出log到控制台
        } else {
            Log.i(context.toString(), debugLog);
        }
    }

    /*
     * 输出log
     */
    public static void outputDebugFile(Context context, String debugLog, String fileName) {
        try {
            String file_path_str = null;
            File file_path = null;
            // 优先保存到SD卡中
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                file_path_str = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "freeLight";
                // 如果SD卡不存在，就保存到本应用的目录下
            } else {
                file_path_str = context.getFilesDir().getAbsolutePath();
            }
            file_path = new File(file_path_str);
            //路径不存在时，建立路径
            if (!file_path.exists()) {
                file_path.mkdirs();
            }

            if (bwOutputDebug == null) {
                //本次程序启动时，文件初始化
                if (logFile == null) {
                    logFile = new File(file_path_str + File.separator + fileName);
                    bwOutputDebug = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(logFile), "GB2312"), 10240);
                    //在打印时，在本次的Log后追加
                } else {
                    bwOutputDebug = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(logFile, true), "GB2312"), 10240);
                }
            }

            bwOutputDebug.write(new SimpleDateFormat("HHmmssSSS").format(Calendar.getInstance().getTime()) + ":" + debugLog.toString() + "\r\n");

            bwOutputDebug.flush();

        } catch (Exception e) {
        }
    }

    /*
     * 输出log的关闭
     */
    public static void closeOutputDebugFile() {
        try {
            if (bwOutputDebug != null) {
                bwOutputDebug.close();
                bwOutputDebug = null;
            }
        } catch (Exception e) {
        }
    }

}
