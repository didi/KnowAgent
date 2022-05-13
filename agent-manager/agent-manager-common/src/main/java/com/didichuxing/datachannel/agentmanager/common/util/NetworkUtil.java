package com.didichuxing.datachannel.agentmanager.common.util;

import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkUtil {

    /**
     * ping主机超时时间：默认值3s
     */
    public static final Integer HOST_PING_TIMEOUT_SECOND = 3 * 1000;

    /**
     * 校验给定ip/hostname & port 是否可连通
     * @param ipOrHostname ip 或 主机名
     * @param port 端口
     * @return true：可连通 false：不可连通
     */
    public static boolean connect(String ipOrHostname, Integer port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ipOrHostname, port), HOST_PING_TIMEOUT_SECOND);
            return socket.isConnected();
        } catch (Exception ex) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new ServiceException(
                        String.format("Socket{ipOrHostname=%s, port=%d}关闭失败", ipOrHostname, port),
                        ErrorCodeEnum.SOCKET_CLOSE_FAILED.getCode()
                );
            }
        }
    }

    /**
     * 校验给定主机名/ip是否可ping通
     * @param hostNameOrHostIp 主机名/ip
     * @return true：可ping通 false：无法ping通
     */
    public static boolean ping(String hostNameOrHostIp) {
        try {
            boolean status = InetAddress.getByName(hostNameOrHostIp).isReachable(HOST_PING_TIMEOUT_SECOND);
            if(status) {//ping 通
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * 校验 telnet 是否可连通
     * @param hostName 主机名
     * @param port 端口
     * @return true：可连通 false：无法连通
     */
    public static boolean telnet(String hostName, Integer port) {
        Socket socket = new Socket();
        boolean isConnected = false;
        try {
            socket.connect(new InetSocketAddress(hostName, port), HOST_PING_TIMEOUT_SECOND); // 建立连接
            isConnected = socket.isConnected(); // 通过现有方法查看连通状态
        } catch (IOException e) {
            return false;
        }finally{
            try {
                socket.close(); // 关闭连接
            } catch (IOException e) {
                throw new ServiceException(
                        String.format("Socket{ipOrHostname=%s, port=%d}关闭失败", hostName, port),
                        ErrorCodeEnum.SOCKET_CLOSE_FAILED.getCode()
                );
            }
        }
        return isConnected;
    }

}
