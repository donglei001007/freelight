package com.ssp365.android.freelight.common;

import android.util.Log;

import com.ssp365.android.freelight.model.Parameter;
import com.ssp365.android.freelight.wifi.ConnectedThread;

import java.util.Random;

public class MessageHelper {

    // Debugging
    private static final String TAG = "MessageHelper";


    /*
       * 给测试柱发送指定信号
  	 * true：重复发送信号序列（不发送最后一个通过后信号，直接跳到预备信号）
  	 * false:不重复发送信号序列（发送最后一个通过后信号）
  	 * 
  	 */
    public static void sendMsg(SmartSportApplication mApplication, boolean flag) {
        for (int i = 0; i < mApplication.getModel().getModel_point_count(); i++) {
            //连接正常时发送信号
            if (mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).connected) {
                mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).arrayWriteThread(flag);
            }
        }
    }

    /*
     * 给测试柱发送指定信号
     */
    public static void sendMsg(SmartSportApplication mApplication, String msg, boolean waitFlag) {
        for (int i = 0; i < mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.size(); i++) {
            //连接正常时发送信号
            if (mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).connected) {
                mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).writeThread(msg);
            }
            //需要依次发送时，发完一个信号等待一定时间
            if (waitFlag) {
                try {
                    Thread.sleep(Parameter.CHECK_WAIT_TIME);
                } catch (Exception e) {
                    Log.e(TAG, "ConnectedThreadCheckWriter error!", e);
                }
            }
        }
    }

    //2016/03/27 chenxy add start
  	/*
  	 * 给指定测试柱发送指定信号
  	 */
    public static void sendMsg(SmartSportApplication mApplication,int no,String msg){
        for(int i=0;i<mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.size();i++){
            //连接正常时发送信号
            if(mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).connected){
                int ponintNo = ((ConnectedThread)mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i)).getLightNo();
                Log.i(TAG, "ponintNo:"+ponintNo);
                Log.i(TAG, "no:"+no);
                if(ponintNo==no){
                    mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).writeThread(msg);
                    break;
                }
            }
        }
    }
    //2016/03/27 chenxy add end

    /*
     * 给测试柱设定信号序列
     */
    public static void setClientInf(SmartSportApplication mApplication) {
        String[][] pointStateArray = mApplication.getPointStateArray();
        String[][] waitStateArray = mApplication.getWaitStateArray();

        String[][] tempPointStateArray = new String[pointStateArray.length][pointStateArray[0].length];
        for (int i = 0; i < pointStateArray.length; i++) {
            for (int j = 0; j < pointStateArray[i].length; j++) {
                tempPointStateArray[i][j] = pointStateArray[i][j];
            }
        }
        String[][] tempWaitStateArray = new String[waitStateArray.length][waitStateArray[0].length];
        for (int i = 0; i < waitStateArray.length; i++) {
            for (int j = 0; j < waitStateArray[i].length; j++) {
                tempWaitStateArray[i][j] = waitStateArray[i][j];
            }
        }

        for (int i = 0; i < mApplication.getModel().getModel_point_count(); i++) {
            for (int j = 0; j < pointStateArray[i].length; j++) {
                Log.i(TAG, "修改前_" + i + ":" + tempPointStateArray[i][j] + " " + tempWaitStateArray[i][j]);
            }
        }

        //是否有随机信号
        boolean randomFlag = false;
        int randonIndex = -1;
        for (int i = 0; i < mApplication.getModel().getModel_point_count(); i++) {
            for (int j = 0; j < tempWaitStateArray[i].length; j++) {
                if (tempWaitStateArray[i][j].equals(Parameter.WAIT_STATUS_RANDOM)) {
                    randomFlag = true;
                    randonIndex = j;
                    break;
                }
            }
            if (randomFlag) {
                break;
            }
        }

        //有随机信号时，给定随机信号的具体内容
        if (randomFlag) {
            Log.i(TAG, "random");
            String[] waitStatus = new String[tempWaitStateArray.length];
            String[] sendMsg = new String[tempPointStateArray.length];
            for (int i = 0; i < tempPointStateArray.length; i++) {
                for (int j = 0; j < tempPointStateArray[i].length; j++) {
                    if (j == randonIndex) {
                        waitStatus[i] = tempWaitStateArray[i][j];
                        sendMsg[i] = tempPointStateArray[i][j];
                    }
                }
            }
            String[][] randomInf = getRandomInf(waitStatus, sendMsg);
            for (int i = 0; i < tempPointStateArray.length; i++) {
                for (int j = 0; j < tempPointStateArray[i].length; j++) {
                    if (j == randonIndex) {
                        tempWaitStateArray[i][j] = randomInf[0][i];
                        tempPointStateArray[i][j] = randomInf[1][i];
                    }
                }
            }
        }

        for (int i = 0; i < mApplication.getModel().getModel_point_count(); i++) {
            for (int j = 0; j < pointStateArray[i].length; j++) {
                Log.i(TAG, "修改后_" + i + ":" + tempPointStateArray[i][j] + " " + tempWaitStateArray[i][j]);
            }
        }

        for (int i = 0; i < mApplication.getModel().getModel_point_count(); i++) {
            mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).setSendMsg(tempPointStateArray[i]);
            mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).setWaitStatus(tempWaitStateArray[i]);
            //发送序列的初始化
            mApplication.getHandler().getWifisetActivity().mWifiService.clientConnectedThreadList.get(i).sendMsgIndex = -1;
        }

    }

    /*
     * 得到随机信号
     * 返回值为二维数组
     * String[0][]:得到的随机响应数组
     * String[1][]:得到的随机发送信号数组
     */
    public static String[][] getRandomInf(String[] waitStatus, String[] sendMsg) {

        for (int i = 0; i < waitStatus.length; i++) {
            Log.i(TAG, "getRandomInf前_" + i + ":" + waitStatus[i] + " " + sendMsg[i]);
        }

        String[][] reArray = new String[2][waitStatus.length];
        int count = 0;
        //计算随机数
        //取得随机数的范围
        for (int i = 0; i < waitStatus.length; i++) {
            if (waitStatus[i].equals(Parameter.WAIT_STATUS_RANDOM)) {
                count++;
            }
        }
        //生成一个随机数[(waitStatus.length-count)<=x<=waitStatus.length]
        //例:(1<=x<=4)
        Random random = new Random();
        int randomInt = Math.abs(random.nextInt()) % count + (waitStatus.length - count);
        Log.i(TAG, "随机数:" + randomInt);

        //设定随机数
        for (int i = 0; i < waitStatus.length; i++) {
            if (waitStatus[i].equals(Parameter.WAIT_STATUS_RANDOM)) {
                if (randomInt == i) {
                    waitStatus[i] = Parameter.WAIT_STATUS_DO;
                    sendMsg[i] = Parameter.CONNECT_INF_RECORD_POINT_T;
                } else {
                    waitStatus[i] = Parameter.WAIT_STATUS_NO_DO;
                    if (sendMsg[i].equals(Parameter.CONNECT_INF_RANDOM_NO_CHARGE)) {
                        sendMsg[i] = Parameter.CONNECT_INF_NONE;
                    } else if (sendMsg[i].equals(Parameter.CONNECT_INF_RANDOM_CHARGE)) {
                        //sendMsg[i] = Parameter.CONNECT_INF_RECORD_POINT_F;
                        sendMsg[i] = Parameter.CONNECT_INF_PASS;
                    }
                }
            }
        }

        for (int i = 0; i < waitStatus.length; i++) {
            Log.i(TAG, "getRandomInf后_" + i + ":" + waitStatus[i] + " " + sendMsg[i]);
        }

        reArray[0] = waitStatus;
        reArray[1] = sendMsg;

        return reArray;
    }

}
