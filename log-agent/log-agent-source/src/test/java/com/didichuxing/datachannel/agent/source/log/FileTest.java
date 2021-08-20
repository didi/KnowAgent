package com.didichuxing.datachannel.agent.source.log;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;

import com.didichuxing.datachannel.agent.source.log.beans.FileNode;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;
import com.didichuxing.datachannel.agent.source.log.offset.OffsetManager;
import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-24 20:53
 */
public class FileTest {

    protected static final long   _2WEEK         = 2 * 7 * 24 * 60 * 60 * 1000L;
    protected static final String SEPARATORCHAR  = "###";

    protected static final String baseFilePath   = "/tmp/new-log-agent/logSourceTest/didi.log";
    protected static final String baseFilePath_1 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010101";
    protected static final String baseFilePath_2 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010102";
    protected static final String baseFilePath_3 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010103";

    protected static final String baseFilePath_4 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010104";
    protected static final String baseFilePath_5 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010105";
    protected static final String baseFilePath_6 = "/tmp/new-log-agent/logSourceTest/didi.log.wf.2018010105";

    protected static final String baseFilePath_7 = "/tmp/new-log-agent/logSourceTest/didi.log.2018010107";

    protected static final String baseFilePath_8 = "/tmp/new-log-agent/logSourceTest/didi.log.20180101";
    protected static final String baseFilePath_9 = "/tmp/new-log-agent/logSourceTest/didi.log.20180101-11";

    protected static final String offsetParent   = "/tmp/new-log-agent/offset/";
    protected static final String offsetParent_0 = "/tmp/new-log-agent/offset/test/";

    protected Long                defaultModelId = 0L;
    protected Long                defaultPathId  = 0L;

    protected static final int    MAX_LINE       = 100;

    @Before
    public void before() {
        try {
            initMultiLineFile(baseFilePath);
            Thread.sleep(500);
            initFile(baseFilePath_1);
            Thread.sleep(500);
            initFile(baseFilePath_2);
            Thread.sleep(500);
            initFile(baseFilePath_3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        File dir = new File(offsetParent);
        dir.mkdirs();

        File dir0 = new File(offsetParent_0);
        dir0.mkdirs();
    }

    @After
    public void after() {
        deleteFile(baseFilePath);
        deleteFile(baseFilePath_1);
        deleteFile(baseFilePath_2);
        deleteFile(baseFilePath_3);
        deleteFile(baseFilePath_4);
        deleteFile(baseFilePath_5);
        deleteFile(baseFilePath_6);
        deleteFile(baseFilePath_7);
        deleteFile(baseFilePath_8);
        deleteFile(baseFilePath_9);

        FileUtils.delFile(new File(offsetParent));
        OffsetManager.stop();
    }

    protected List<String> getContent(Map<String, FileOffSet> m) {
        List<String> ret = new ArrayList<>();
        for (String key : m.keySet()) {
            FileOffSet fileOffSet = m.get(key);
            if (fileOffSet.getLastModifyTime() != null && !fileOffSet.getLastModifyTime().equals(0L)
                && System.currentTimeMillis() - fileOffSet.getLastModifyTime() > _2WEEK) {
                // 过滤掉2周之前的offset信息,防止offset暴增无法清理
                // LOGGER.warn("offset is over due. ignore! offset is " + fileOffSet);
                continue;
            }
            ret.add(fileOffSet.getOffsetKeyForOldVersion() + SEPARATORCHAR + fileOffSet.toJson().toJSONString());
        }
        return ret;
    }

    protected String getLogMod4PathKey(Long logId, Long pathId, String masterFilePath) {
        if (StringUtils.isNotBlank(masterFilePath)) {
            masterFilePath = masterFilePath.trim();
            String suffix = null;
            if (masterFilePath.contains("/")) {
                suffix = masterFilePath.replace("/", "_");
            } else {
                suffix = masterFilePath;
            }
            return logId + "_" + pathId + "_" + suffix;
        } else {
            return logId + "_" + pathId;
        }
    }

    protected FileOffSet getFileOffset(String filePath) {
        FileNode fileNode = getFileNode(filePath);
        FileOffSet fileOffSet = new FileOffSet(defaultModelId, defaultPathId, filePath,
            fileNode.getFileKey());
        fileOffSet.setLastModifyTime(FileUtils.getModifyTime(new File(filePath)));
        return fileOffSet;
    }

    public FileNode getFileNode(String filePath) {
        File file = new File(filePath);
        FileNode fileNode = new FileNode(defaultModelId, defaultPathId,
            FileUtils.getFileKeyByAttrs(file), file.lastModified(), file.getParent(),
            file.getName(), file.length(), file);
        return fileNode;
    }

    protected long getFileSize(String filePath) {
        return new File(filePath).length();
    }

    protected void createPath(String filePath) {
        File file = new File(filePath);
        File dir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator) + 1));
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    protected void initFile(String path) {
        File file = new File(path);
        createFiles(path);
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            for (int i = 0; i < MAX_LINE; i++) {
                lines.add("timestamp=" + System.currentTimeMillis() + ",line=" + i + "," + bigLine + " from file:"
                        + path);
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initMultiLineFile(String path) {
        File file = new File(path);
        createFiles(path);
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            for (int i = 0; i < MAX_LINE; i++) {
                lines.add("timestamp=" + System.currentTimeMillis() + ",line=" + i + "," + bigLine + " from file:"
                        + path);
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                for (StackTraceElement element : stackTrace) {
                    lines.add(element.toString());
                }
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void createSoftLink(String originalPath, String targetPath) {
        try {
            Files.createSymbolicLink(FileSystems.getDefault().getPath(targetPath), FileSystems
                .getDefault().getPath(originalPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void releaseSoftLink(String targetPath) {
        try {
            Files.deleteIfExists(FileSystems.getDefault().getPath(targetPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void appendFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            initFile(path);
        }
        try {
            List<String> lines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            String bigLine = "bigline";
            for (int i = 0; i < 2 * MAX_LINE; i++) {
                lines.add("timestamp=" + System.currentTimeMillis() + ",line=" + i + "," + bigLine + " from file:"
                          + path);
            }

            FileUtils.writeFileContent(file, lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int getLineNum(String line) {
        if (StringUtils.isNotBlank(line)) {
            line = line.substring(line.indexOf("line=") + 5);
            line = line.substring(0, line.indexOf(","));
            return Integer.parseInt(line);
        }
        return -1;
    }

    public void createFiles(String path) {
        File file = new File(path);
        File dir = new File(path.substring(0, path.lastIndexOf(File.separator) + 1));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                file.delete();
            } catch (Exception e) {

            }
        }
    }

    protected void writeByLine(File file, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(content + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Long getDefaultModelId() {
        return defaultModelId;
    }

    public void setDefaultModelId(Long defaultModelId) {
        this.defaultModelId = defaultModelId;
    }

    public Long getDefaultPathId() {
        return defaultPathId;
    }

    public void setDefaultPathId(Long defaultPathId) {
        this.defaultPathId = defaultPathId;
    }
}
