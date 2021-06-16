package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.didichuxing.datachannel.swan.agent.channel.log.LogChannel;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.didichuxing.datachannel.swan.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.EventMetricsConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-25 21:23
 */
public class HdfsTestUtil {

    private static long         line_num       = 0L;
    private static final Long   defaultModelId = 0L;
    private static final String parentPath     = "/tmp/test/";
    private static final String masterFileName = "didi.log";
    private static final String rootPath       = "hdfs://10.179.132.92:9000/huangjiaweihjw";

    protected String getContentFromHdfs(HdfsSink hdfsSink) {
        FileSystem fileSystem = init();
        HdfsDataFile target = null;
        for (Map<Long, HdfsDataFile> map : hdfsSink.getHdfsDataFileMap().values()) {
            for (HdfsDataFile hdfsDataFile : map.values()) {
                target = hdfsDataFile;
                break;
            }
        }
        String remote = target.getRemoteFilePath();
        String resultStr = null;
        try {
            FSDataInputStream inStream = fileSystem.open(new Path(remote));
            byte[] result = new byte[inStream.available()];
            inStream.read(result);
            resultStr = new String(result);
            System.out.println(resultStr);
            inStream.close();
            fileSystem.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    protected String getContentFromHdfsFromSq(HdfsSink hdfsSink) {
        FileSystem fileSystem = init();
        HdfsDataFile target = null;
        for (Map<Long, HdfsDataFile> map : hdfsSink.getHdfsDataFileMap().values()) {
            for (HdfsDataFile hdfsDataFile : map.values()) {
                target = hdfsDataFile;
                break;
            }
        }
        String remote = target.getRemoteFilePath();
        String resultStr = null;
        try {
            SequenceFile.Reader reader = new SequenceFile.Reader(fileSystem.getConf(),
                SequenceFile.Reader.file(new Path(remote)));
            NullWritable key = (NullWritable) ReflectionUtils.newInstance(reader.getKeyClass(),
                fileSystem.getConf());
            BytesWritable value = new BytesWritable();
            while (reader.next(key, value)) {
                byte[] bytes = value.getBytes();
                String content = new String(bytes);
                System.out.println(content);
                resultStr += content;
            }
            IOUtils.closeStream(reader);// 关闭read流
            fileSystem.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    protected FileSystem init() {
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI(rootPath), conf, "root");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fs;
    }

    protected HdfsSink getHdfsSink() {
        HdfsTargetConfig hdfsTargetConfig = new HdfsTargetConfig();
        hdfsTargetConfig.setRootPath(rootPath);
        hdfsTargetConfig.setHdfsPath("/hdfsPath/${path}/${yyyy}/${MM}/${dd}/${HH}");
        hdfsTargetConfig.setUsername("root");
        hdfsTargetConfig.setPassword("password");
        hdfsTargetConfig.setFilterRule("test content");

        ModelConfig modelConfig = new ModelConfig("log2kafka");

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setModelId(defaultModelId);

        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
        eventMetricsConfig.setOdinLeaf("hna");
        eventMetricsConfig.setOriginalAppName("service_name");
        eventMetricsConfig.setTransName("");

        modelConfig.setCommonConfig(commonConfig);
        modelConfig.setEventMetricsConfig(eventMetricsConfig);
        modelConfig.setTargetConfig(hdfsTargetConfig);

        HdfsFileSystem hdfsFileSystem = new HdfsFileSystem(hdfsTargetConfig.getRootPath(),
            hdfsTargetConfig.getUsername(), hdfsTargetConfig.getPassword());

        LogChannel channel = new LogChannel(null, null);
        channel.setUniqueKey("test");

        HdfsSink hdfsSink = new HdfsSink(modelConfig, channel, 0, getDirPathRule(hdfsTargetConfig),
            getFileNameRule(hdfsTargetConfig), hdfsFileSystem);
        hdfsSink.init(null);
        return hdfsSink;
    }

    protected HdfsSink getSeqHdfsSink() {
        HdfsTargetConfig hdfsTargetConfig = new HdfsTargetConfig();
        hdfsTargetConfig.setRootPath(rootPath);
        hdfsTargetConfig.setHdfsPath("/hdfsPath/${path}/${yyyy}/${MM}/${dd}/${HH}");
        hdfsTargetConfig.setUsername("root");
        hdfsTargetConfig.setPassword("password");
        hdfsTargetConfig.setCompression(3);

        ModelConfig modelConfig = new ModelConfig("log2kafka");

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setModelId(defaultModelId);

        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
        eventMetricsConfig.setOdinLeaf("hna");
        eventMetricsConfig.setOriginalAppName("");
        eventMetricsConfig.setTransName("");

        modelConfig.setCommonConfig(commonConfig);
        modelConfig.setEventMetricsConfig(eventMetricsConfig);
        modelConfig.setTargetConfig(hdfsTargetConfig);

        HdfsFileSystem hdfsFileSystem = new HdfsFileSystem(hdfsTargetConfig.getRootPath(),
            hdfsTargetConfig.getUsername(), hdfsTargetConfig.getPassword());

        LogChannel channel = new LogChannel(null, null);
        channel.setUniqueKey("test");

        HdfsSink hdfsSink = new HdfsSink(modelConfig, channel, 0, getDirPathRule(hdfsTargetConfig),
            getFileNameRule(hdfsTargetConfig), hdfsFileSystem);
        hdfsSink.init(null);
        return hdfsSink;
    }

    protected LogEvent getLogEvent() {
        Long time = System.currentTimeMillis();
        String content = "test content.time is " + time;
        LogEvent logEvent = new LogEvent(content, content.getBytes(), line_num + content.length(),
            time, time + "", line_num, "fileNodeKey", "fileKey", "/tmp/test/", "test.log.didi",
            "test.log", null);
        line_num += content.length();
        return logEvent;
    }

    protected LogEvent getLogEventForHdfs() {
        Long time = System.currentTimeMillis();
        String content = "test content.time is " + time;
        LogEvent logEvent = new LogEvent(content, content.getBytes(), line_num + content.length(),
            time, time + "", line_num, "fileNodeKey", "fileKey", "/tmp/test/", "test.log.didi",
            "test.log", null);
        line_num += content.length();
        logEvent.setUniqueKey("uniqueKey");
        logEvent.setLogId(1000L);
        logEvent.setLogPathId(2000);
        return logEvent;
    }

    protected String getDirPathRule(HdfsTargetConfig hdfsTargetConfig) {
        String hdfsPath = hdfsTargetConfig.getHdfsPath();
        String parentPath = this.parentPath;
        if (parentPath.startsWith("/")) {
            parentPath = parentPath.substring(1);
        }

        if (parentPath.endsWith("/")) {
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        }
        hdfsPath = hdfsPath.replace(LogConfigConstants.PATH_FLAG, parentPath);
        return hdfsPath;
    }

    protected String getFileNameRule(HdfsTargetConfig hdfsTargetConfig) {
        String hdfsFileName = hdfsTargetConfig.getHdfsFileName();
        String masterFileName = this.masterFileName;
        hdfsFileName = hdfsFileName.replace(LogConfigConstants.HOSTNAME_FLAG,
            CommonUtils.getHOSTNAME());
        hdfsFileName = hdfsFileName.replace(LogConfigConstants.FILENAME_FLAG, masterFileName);
        return hdfsFileName;
    }

    /**
     * 删除目录
     * @param hdfsPath
     */
    protected void deleteDirInHdfs(String hdfsPath) {
        FileSystem fileSystem = init();
        try {
            fileSystem.deleteOnExit(new Path(hdfsPath));
            fileSystem.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件数
     * @param hdfsPath
     * @return
     */
    protected List<String> lsFilesInHdfs(String hdfsPath) {
        FileSystem fileSystem = init();
        try {
            List<String> paths = getFiles(fileSystem, hdfsPath);
            fileSystem.close();
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> getFiles(FileSystem fileSystem, String hdfsPath) {
        try {
            FileStatus[] fstat = fileSystem.listStatus(new Path(hdfsPath));
            List<String> paths = new ArrayList<>();
            for (FileStatus fs : fstat) {
                if (fs.isDirectory()) {
                    paths.addAll(getFiles(fileSystem, fs.getPath().toString()));
                } else {
                    paths.add(fs.getPath().toString());
                }
            }
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
