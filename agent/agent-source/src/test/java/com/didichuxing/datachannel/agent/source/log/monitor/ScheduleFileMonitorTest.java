package com.didichuxing.datachannel.agent.source.log.monitor;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.didichuxing.datachannel.agent.source.log.FileTest;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.LogSourceTest;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-24 20:33
 */
public class ScheduleFileMonitorTest extends FileTest {

    private static final String addFilePath = "/tmp/new-log-agent/logSourceTest/didi.log.2019010101";
    private static final String delFilePath = baseFilePath;

    @Test
    public void addFile() {
        LogSourceTest logSourceTest = new LogSourceTest();
        logSourceTest.setDefaultModelId(0L);
        logSourceTest.setDefaultPathId(0L);
        LogSource logSource = logSourceTest.getLogSource();
        logSource.init(null);
        try {
            ScheduleFileMonitor.INSTANCE.start();
            ScheduleFileMonitor.INSTANCE.register(logSource);

            createFiles(addFilePath);

            Thread.sleep(2 * 60 * 1000L + 5);
            assertTrue(logSource.getCollectingFileNodeMap().keySet()
                .contains(logSourceTest.getFileNode(addFilePath).getNodeKey()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ScheduleFileMonitor.INSTANCE.unregister(logSource);
            ScheduleFileMonitor.INSTANCE.stop();
            deleteFile(addFilePath);
        }
    }

    @Test
    public void delFile() {
        LogSourceTest logSourceTest = new LogSourceTest();
        logSourceTest.setDefaultModelId(0L);
        logSourceTest.setDefaultPathId(0L);
        LogSource logSource = logSourceTest.getLogSource();
        logSource.init(null);
        try {
            ScheduleFileMonitor.INSTANCE.start();
            ScheduleFileMonitor.INSTANCE.register(logSource);

            deleteFile(delFilePath);

            Thread.sleep(2 * 60 * 1000L + 5);
            assertTrue(!logSource.getCollectingFileNodeMap().keySet()
                .contains(logSourceTest.getFileNode(delFilePath).getNodeKey()));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ScheduleFileMonitor.INSTANCE.unregister(logSource);
            ScheduleFileMonitor.INSTANCE.stop();
        }
    }

    @Test
    public void regAndUnreg() {
        LogSourceTest logSourceTest = new LogSourceTest();
        logSourceTest.setDefaultModelId(0L);
        logSourceTest.setDefaultPathId(0L);
        LogSource logSource0 = logSourceTest.getLogSource();
        register(logSource0);
        assertTrue(ScheduleFileMonitor.INSTANCE.getFileMap().size() == 1);
        assertTrue(ScheduleFileMonitor.INSTANCE.isRunning());

        logSourceTest.setDefaultModelId(1L);
        logSourceTest.setDefaultPathId(2L);
        LogSource logSource1 = logSourceTest.getLogSource();
        register(logSource1);
        assertTrue(ScheduleFileMonitor.INSTANCE.getFileMap().size() == 1);
        assertTrue(ScheduleFileMonitor.INSTANCE.isRunning());

        unregister(logSource0);
        assertTrue(ScheduleFileMonitor.INSTANCE.getFileMap().size() == 1);
        assertTrue(ScheduleFileMonitor.INSTANCE.isRunning());

        unregister(logSource1);
        assertTrue(ScheduleFileMonitor.INSTANCE.getFileMap().size() == 0);
        assertTrue(!ScheduleFileMonitor.INSTANCE.isRunning());
    }

    private void unregister(LogSource logSource) {
        ScheduleFileMonitor.INSTANCE.unregister(logSource);
        ScheduleFileMonitor.INSTANCE.stop();
    }

    private void register(LogSource logSource) {
        ScheduleFileMonitor.INSTANCE.start();
        ScheduleFileMonitor.INSTANCE.register(logSource);
    }
}
