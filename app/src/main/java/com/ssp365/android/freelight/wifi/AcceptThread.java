package com.ssp365.android.freelight.wifi;

import android.net.DhcpInfo;

import com.ssp365.android.freelight.activity.WifisetActivity;
import com.ssp365.android.freelight.common.DebugLog;
import com.ssp365.android.freelight.common.GlobalConstants;
import com.ssp365.android.freelight.model.Parameter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * wifiAP状态监控线程
 *
 * @author dl 2016/08/25
 * @version 1.0.0.1
 */
public class AcceptThread extends Thread {
    boolean acceptFlag = true;
    // WIFIAP对象
    private WifiServer wifiServer;
    // 监听客户端用socket
    private ServerSocket serverSocket = null;

    /**
     * 监听进程初始化
     */
    public AcceptThread(WifiServer wifiServer) {
        try {
            this.wifiServer = wifiServer;
            DebugLog.debug(null, "WIFIAP检测：线程初始化开始。");

            serverSocket = new ServerSocket(Parameter.WIFI_SERVER_PORT);
            SocketAddress address = null;
            if (!serverSocket.isBound()) {
                serverSocket.bind(address, Parameter.WIFI_SERVER_PORT_BACKUP);
            }
            DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：AcceptThread_serverSocket.getLocalSocketAddress()：" + serverSocket.getLocalSocketAddress());
            DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：AcceptThread_serverSocket.getReuseAddress()：" + serverSocket.getReuseAddress());

            DhcpInfo info = wifiServer.getmWifiManager().getDhcpInfo();
            DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：AcceptThread_info.serverAddress：" + info.serverAddress);

            DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：线程初始化结束。");
        } catch (IOException e) {
            DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：线程初始化过程报错:" + e);
            e.printStackTrace();
        }
    }

    /**
     * 监听进程启动
     */
    public void run() {
        Socket socket = null;
        try {
            while (acceptFlag) {
                DebugLog.debug(wifiServer.getContext(), "AcceptThread start");

                // 将每一个连接到该服务器的客户端，加到List中
                socket = serverSocket.accept();

                //2016/03/27 chenxy add start
                // 最多添加到4个客户端，如果超过4个的话，关闭
                if (wifiServer.getClientSocketList().size() >= 4) {
                    DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：客户端达到4个，不再添加新的客户端");
                    socket.close();
                    continue;
                }
                //2016/03/27 chenxy add end

                WifisetActivity.ClientList socketClient = new WifisetActivity.ClientList();
                // 顺序取下一个灯柱编号
                socketClient.value_no = String.valueOf(wifiServer.getClientSocketList().size() + 1);
                socketClient.socket = socket;
                wifiServer.getClientSocketList().add(socketClient);

                // 每一个连接到服务器的客户端，服务器开启一个新的线程来处理
                ConnectedThread connectedThread = new ConnectedThread(socket, wifiServer.getClientSocketList().size(), wifiServer.getmHandler());
                connectedThread.start();
                wifiServer.getClientConnectedThreadList().add(connectedThread);

                DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：AcceptThread_serverSocket.getLocalSocketAddress()：" + serverSocket.getLocalSocketAddress());
                DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：AcceptThread_socket.getLocalSocketAddress()：" + socket.getLocalSocketAddress());
                DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：AcceptThread_socket.getRemoteSocketAddress()：" + socket.getRemoteSocketAddress());

                // 发送客户端连接信息
                wifiServer.getmHandler().obtainMessage(GlobalConstants.STATE_WIFI_ACCEPT, -1, -1).sendToTarget();
            }
        } catch (IOException ee) {
            //强行关闭时不需要打印信息
            if (acceptFlag) {
                wifiServer.getmHandler().obtainMessage(GlobalConstants.STATE_WIFI_ACCEPT_FAILE, -1, -1, ee.toString()).sendToTarget();
                DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：线程启动过程报错:" + ee);
            }
            return;
        }
    }

    //监听进程关闭
    public void cancel() {
        try {
            //关闭所有客户端进程
            if (wifiServer.getClientConnectedThreadList() != null && wifiServer.getClientConnectedThreadList().size() > 0) {
                for (int i = 0; i < wifiServer.getClientConnectedThreadList().size(); i++) {
                    if (wifiServer.getClientConnectedThreadList().get(i) != null) {
                        wifiServer.getClientConnectedThreadList().get(i).cancel();
                    }
                    wifiServer.getClientConnectedThreadList().remove(i);
                }
            }
            //关闭监听进程
            if (serverSocket != null) {
                acceptFlag = false;
                serverSocket.close();
            }
            this.interrupt();
        } catch (IOException e) {
            DebugLog.debug(wifiServer.getContext(), "WIFIAP检测：线程关闭过程报错:" + e);
        }
    }
}
