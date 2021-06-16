package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.didichuxing.datachannel.swan.agent.engine.bean.LogEvent;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-25 21:22
 */
public class HdfsFileSystemTest extends HdfsTestUtil {

    private static final int maxLine = 100;

    @Test
    public void reBuild() {
        HdfsSink hdfsSink = getHdfsSink();

        String hdfsPath = getDirPathRule(hdfsSink.getHdfsTargetConfig());
        hdfsPath = hdfsPath.replace("/${yyyy}", "");
        hdfsPath = hdfsPath.replace("/${MM}", "");
        hdfsPath = hdfsPath.replace("/${dd}", "");
        hdfsPath = hdfsPath.replace("/${HH}", "");
        hdfsPath = hdfsSink.getHdfsTargetConfig().getRootPath() + hdfsPath;

        deleteDirInHdfs(hdfsPath);

        for (int i = 0; i < maxLine; i++) {
            if (i == maxLine / 2) {
                hdfsSink.getHdfsFileSystem().reBuildDataFile();
            }
            LogEvent logEvent = getLogEvent();
            HdfsEvent hdfsEvent = hdfsSink.wrap(logEvent);
            hdfsSink.send(hdfsEvent);
        }
        hdfsSink.flush();
        assertTrue(hdfsSink.getHdfsDataFileMap().size() == 1);

        List<String> filePaths = lsFilesInHdfs(hdfsPath);
        assertTrue(filePaths.size() == 2);
        hdfsSink.getHdfsFileSystem().close();
    }

    @Test
    public void checkHealth() {
        HdfsSink hdfsSink = getHdfsSink();
        assertTrue(hdfsSink.getHdfsFileSystem().checkHealth());
        hdfsSink.getHdfsFileSystem().close();
    }
}
