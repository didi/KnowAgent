package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import java.lang.reflect.Method;

import org.apache.hadoop.hdfs.DFSOutputStream;

import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description: hdfs stream 是否被close 检测工具
 * @author: huangjw
 * @Date: 2019-08-28 17:58
 */
public class HdfsStreamClosedChecker {

    private static final ILog LOGGER = LogFactory.getLog(HdfsSink.class.getName());
    private static Method     checkClosed;

    private static void initCheckClosedMethod() {
        try {
            Class<?> dsfOutputStreamClass = DFSOutputStream.class;
            checkClosed = dsfOutputStreamClass.getDeclaredMethod("checkClosed");
            checkClosed.setAccessible(true);
        } catch (Exception e) {
            LogGather.recordErrorLog("HdfsStreamClosedChecker error",
                "INIT_METHOD_FAILED init method failed ", e);
        }
    }

    public static boolean invokeCheckClosed(DFSOutputStream dfsOutputStream, String path) {
        try {
            if (checkClosed == null) {
                initCheckClosedMethod();
            }
            checkClosed.invoke(dfsOutputStream);
            return false;
        } catch (Exception e) {
            // 抛出异常则代表被close了，此时需要重启
            LogGather.recordErrorLog("HdfsStreamClosedChecker error",
                "STREAM_ALREADY_CLOSED path is " + path, e);
            return true;
        }
    }
}
