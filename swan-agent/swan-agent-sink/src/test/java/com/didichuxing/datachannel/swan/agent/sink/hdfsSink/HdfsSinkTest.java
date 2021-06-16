package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import static org.junit.Assert.assertTrue;

import com.didichuxing.datachannel.swan.agent.common.api.HdfsTransFormat;
import com.didichuxing.datachannel.swan.agent.sink.utils.EventUtils;
import org.junit.Test;

import com.didichuxing.datachannel.swan.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.swan.agent.sink.utils.TimeType;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-25 19:41
 */
public class HdfsSinkTest extends HdfsTestUtil {

    @Test
    public void wrap() {
        HdfsSink hdfsSink = getHdfsSink();
        LogEvent logEvent = getLogEvent();
        HdfsEvent hdfsEvent = hdfsSink.wrap(logEvent);
        assertTrue(hdfsEvent != null);
    }

    @Test
    public void writeToHdfsTest() throws Exception {
        HdfsSink hdfsSink = getHdfsSink();
        hdfsSink.getHdfsTargetConfig().setTransFormate(HdfsTransFormat.HDFS_EVENT.getStatus());
        LogEvent logEvent = getLogEventForHdfs();
        HdfsEvent hdfsEvent = hdfsSink.wrap(logEvent);

        EventUtils.buildHdfsContent(hdfsSink.getByteStream(), hdfsSink, hdfsEvent);
        System.out.println(new String(hdfsSink.getByteStream().toByteArray()));
    }

    @Test
    public void send() {
        HdfsSink hdfsSink = getHdfsSink();
        LogEvent logEvent0 = getLogEvent();
        HdfsEvent hdfsEvent0 = hdfsSink.wrap(logEvent0);
        hdfsSink.send(hdfsEvent0);

        LogEvent logEvent1 = getLogEvent();
        HdfsEvent hdfsEvent1 = hdfsSink.wrap(logEvent1);
        hdfsSink.send(hdfsEvent1);

        hdfsSink.flush();
        System.out.println("after flush");
        String result = getContentFromHdfs(hdfsSink);
        assertTrue(result.length() == hdfsEvent0.getContent().length()
                                      + hdfsEvent1.getContent().length() + 2);
    }

    @Test
    public void sendSequence() {
        HdfsSink hdfsSink = getSeqHdfsSink();
        LogEvent logEvent0 = getLogEvent();
        HdfsEvent hdfsEvent0 = hdfsSink.wrap(logEvent0);
        hdfsSink.send(hdfsEvent0);

        LogEvent logEvent1 = getLogEvent();
        HdfsEvent hdfsEvent1 = hdfsSink.wrap(logEvent1);
        hdfsSink.send(hdfsEvent1);

        LogEvent logEvent3 = getLogEvent();
        HdfsEvent hdfsEvent3 = hdfsSink.wrap(logEvent3);
        hdfsSink.send(hdfsEvent3);

        LogEvent logEvent4 = getLogEvent();
        HdfsEvent hdfsEvent4 = hdfsSink.wrap(logEvent4);
        hdfsSink.send(hdfsEvent4);

        hdfsSink.flush();
        System.out.println("after flush");
        String result = getContentFromHdfsFromSq(hdfsSink);
        hdfsSink.stop(true);
        assertTrue(result.length() == hdfsEvent0.getContent().length()
                                      + hdfsEvent1.getContent().length() + 2);
    }

    @Test
    public void getTimeType() {
        HdfsSink hdfsSink = getHdfsSink();
        String masterFileName = "test.log";
        HdfsEvent hdfsEvent = new HdfsEvent();
        hdfsEvent.setAnotherSourceName(masterFileName);

        hdfsEvent.setSourceName("test.log.2019010101");
        assertTrue(TimeType.YMDH == hdfsSink.getTimeType(hdfsEvent));

        hdfsEvent.setSourceName("test.log.2019-01-01-01");
        assertTrue(TimeType.Y_M_D_H == hdfsSink.getTimeType(hdfsEvent));

        hdfsEvent.setSourceName("test.log.20190101");
        assertTrue(TimeType.YMD == hdfsSink.getTimeType(hdfsEvent));

        hdfsEvent.setSourceName("test.log.2019-01-01");
        assertTrue(TimeType.Y_M_D == hdfsSink.getTimeType(hdfsEvent));

        hdfsEvent.setSourceName("test.log");
        assertTrue(null == hdfsSink.getTimeType(hdfsEvent));

        hdfsEvent.setSourceName("test11.log");
        assertTrue(null == hdfsSink.getTimeType(hdfsEvent));
    }

    @Test
    public void getHdfsDateFile1() {
        String masterFileName = "test.log";
        String sourceKey = "2999_192423423";
        HdfsSink hdfsSink = getHdfsSink();
        hdfsSink.setFileSplit(true);

        HdfsEvent hdfsEvent1 = new HdfsEvent();
        hdfsEvent1.setAnotherSourceName(masterFileName);
        hdfsEvent1.setSourceName("test.log.2019010101");
        hdfsEvent1.setLogTime(System.currentTimeMillis());
        hdfsEvent1.setSourceKey(sourceKey);

        HdfsEvent hdfsEvent2 = new HdfsEvent();
        hdfsEvent2.setAnotherSourceName(masterFileName);
        hdfsEvent2.setSourceName("test.log.20190101");
        hdfsEvent2.setLogTime(System.currentTimeMillis() - 70 * 60 * 1000L);
        hdfsEvent2.setSourceKey(sourceKey);

        try {
            String path1 = hdfsSink.getHdfsDateFile(hdfsEvent1).getRemoteFilePath();
            String path2 = hdfsSink.getHdfsDateFile(hdfsEvent2).getRemoteFilePath();
            assertTrue(check(path1, path2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getHdfsDateFile2() {
        String masterFileName = "test.log";
        String sourceKey = "2999_192423423777";
        HdfsSink hdfsSink = getHdfsSink();
        hdfsSink.setFileSplit(false);

        HdfsEvent hdfsEvent1 = new HdfsEvent();
        hdfsEvent1.setAnotherSourceName(masterFileName);
        hdfsEvent1.setSourceName("test.log.2019010101");
        hdfsEvent1.setLogTime(System.currentTimeMillis());
        hdfsEvent1.setSourceKey(sourceKey);

        HdfsEvent hdfsEvent2 = new HdfsEvent();
        hdfsEvent2.setAnotherSourceName(masterFileName);
        hdfsEvent2.setSourceName("test.log.20190101");
        hdfsEvent2.setLogTime(System.currentTimeMillis() - 70 * 60 * 1000L);
        hdfsEvent2.setSourceKey(sourceKey);

        try {
            String path1 = hdfsSink.getHdfsDateFile(hdfsEvent1).getRemoteFilePath();
            String path2 = hdfsSink.getHdfsDateFile(hdfsEvent2).getRemoteFilePath();
            assertTrue(!check(path1, path2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getHdfsDateFile3() {
        String masterFileName = "test.log";
        String sourceKey = "2999_1924234234444";
        HdfsSink hdfsSink = getHdfsSink();
        hdfsSink.setFileSplit(true);

        HdfsEvent hdfsEvent1 = new HdfsEvent();
        hdfsEvent1.setAnotherSourceName(masterFileName);
        hdfsEvent1.setSourceName("test.log.2019010101");
        hdfsEvent1.setLogTime(System.currentTimeMillis());
        hdfsEvent1.setSourceKey(sourceKey);

        HdfsEvent hdfsEvent2 = new HdfsEvent();
        hdfsEvent2.setAnotherSourceName(masterFileName);
        hdfsEvent2.setSourceName("test.log.2019-01-01-01");
        hdfsEvent2.setLogTime(System.currentTimeMillis() - 70 * 60 * 1000L);
        hdfsEvent2.setSourceKey(sourceKey);

        try {
            String path1 = hdfsSink.getHdfsDateFile(hdfsEvent1).getRemoteFilePath();
            String path2 = hdfsSink.getHdfsDateFile(hdfsEvent2).getRemoteFilePath();
            assertTrue(check(path1, path2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean check(String path1, String path2) {
        System.out.println(path1);
        System.out.println(path2);
        return path1.equals(path2);
    }

}
