package com.didichuxing.datachannel.swan.agent.task.log.log2hdfs;

import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.didichuxing.datachannel.swan.agent.common.api.HdfsCompression;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsTargetConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import com.didichuxing.datachannel.swan.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.swan.agent.common.beans.LogPath;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.*;
import com.didichuxing.datachannel.swan.agent.source.log.LogSource;
import com.didichuxing.datachannel.swan.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.swan.agent.source.log.utils.FileUtils;
import com.didichuxing.datachannel.swan.agent.task.log.LogFileUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-26 11:00
 */
public class Log2HdfsTaskTest extends LogFileUtils {

    private static final String rootPath = "hdfs://10.179.132.92:9000/huangjiaweihjw";

    @Test
    public void run() {
        Log2HdfsTask task = getTask();
        task.init(task.getModelConfig());
        LogPath logPath = ((LogSource) (task.getSource())).getLogPath();

        String hdfsPath = getDirPathRule(task.getHdfsTargetConfig(), logPath);
        hdfsPath = hdfsPath.replace("/${yyyy}", "");
        hdfsPath = hdfsPath.replace("/${MM}", "");
        hdfsPath = hdfsPath.replace("/${dd}", "");
        hdfsPath = hdfsPath.replace("/${HH}", "");
        hdfsPath = task.getHdfsTargetConfig().getRootPath() + hdfsPath;

        deleteDirInHdfs(hdfsPath);

        task.start();
        Thread thread = new Thread(task);
        thread.start();

        try {
            Thread.sleep(5 * 60 * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        task.flush();
        task.stop(true);
        assertTrue(getFiles(task.getLog2HdfsModel().getHdfsFileSystem().getFileSystem(), hdfsPath)
            .size() == 4);
    }

    @Test
    public void runLz4() {
        Log2HdfsTask task = getTask();
        ((HdfsTargetConfig) task.getModelConfig().getTargetConfig())
            .setCompression(HdfsCompression.LZ4.getStatus());
        task.init(task.getModelConfig());
        LogPath logPath = ((LogSource) (task.getSource())).getLogPath();

        String hdfsPath = getDirPathRule(task.getHdfsTargetConfig(), logPath);
        hdfsPath = hdfsPath.replace("/${yyyy}", "");
        hdfsPath = hdfsPath.replace("/${MM}", "");
        hdfsPath = hdfsPath.replace("/${dd}", "");
        hdfsPath = hdfsPath.replace("/${HH}", "");
        hdfsPath = task.getHdfsTargetConfig().getRootPath() + hdfsPath;

        deleteDirInHdfs(hdfsPath);

        task.start();
        Thread thread = new Thread(task);
        thread.start();

        try {
            Thread.sleep(5 * 60 * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        task.flush();
        task.stop(true);
        assertTrue(getFiles(task.getLog2HdfsModel().getHdfsFileSystem().getFileSystem(), hdfsPath)
            .size() == 4);
    }

    @Test
    public void runSnappy() {
        Log2HdfsTask task = getTask();
        ((HdfsTargetConfig) task.getModelConfig().getTargetConfig())
            .setCompression(HdfsCompression.SNAPPY.getStatus());
        task.init(task.getModelConfig());
        LogPath logPath = ((LogSource) (task.getSource())).getLogPath();

        String hdfsPath = getDirPathRule(task.getHdfsTargetConfig(), logPath);
        hdfsPath = hdfsPath.replace("/${yyyy}", "");
        hdfsPath = hdfsPath.replace("/${MM}", "");
        hdfsPath = hdfsPath.replace("/${dd}", "");
        hdfsPath = hdfsPath.replace("/${HH}", "");
        hdfsPath = task.getHdfsTargetConfig().getRootPath() + hdfsPath;

        deleteDirInHdfs(hdfsPath);

        task.start();
        Thread thread = new Thread(task);
        thread.start();

        try {
            Thread.sleep(5 * 60 * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        task.flush();
        task.stop(true);
        assertTrue(getFiles(task.getLog2HdfsModel().getHdfsFileSystem().getFileSystem(), hdfsPath)
            .size() == 4);
    }

    @Test
    public void runSeqLz4() {
        Log2HdfsTask task = getTask();
        ((HdfsTargetConfig) task.getModelConfig().getTargetConfig())
            .setCompression(HdfsCompression.SEQ_LZ4.getStatus());
        task.init(task.getModelConfig());
        LogPath logPath = ((LogSource) (task.getSource())).getLogPath();

        String hdfsPath = getDirPathRule(task.getHdfsTargetConfig(), logPath);
        hdfsPath = hdfsPath.replace("/${yyyy}", "");
        hdfsPath = hdfsPath.replace("/${MM}", "");
        hdfsPath = hdfsPath.replace("/${dd}", "");
        hdfsPath = hdfsPath.replace("/${HH}", "");
        hdfsPath = task.getHdfsTargetConfig().getRootPath() + hdfsPath;

        deleteDirInHdfs(hdfsPath);

        task.start();
        Thread thread = new Thread(task);
        thread.start();

        try {
            Thread.sleep(5 * 60 * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        task.flush();
        task.stop(true);
        assertTrue(getFiles(task.getLog2HdfsModel().getHdfsFileSystem().getFileSystem(), hdfsPath)
            .size() == 4);
    }

    @Test
    public void runSqeSnappy() {
        Log2HdfsTask task = getTask();
        ((HdfsTargetConfig) task.getModelConfig().getTargetConfig())
            .setCompression(HdfsCompression.SEQ_SNAPPY.getStatus());
        task.init(task.getModelConfig());
        LogPath logPath = ((LogSource) (task.getSource())).getLogPath();

        String hdfsPath = getDirPathRule(task.getHdfsTargetConfig(), logPath);
        hdfsPath = hdfsPath.replace("/${yyyy}", "");
        hdfsPath = hdfsPath.replace("/${MM}", "");
        hdfsPath = hdfsPath.replace("/${dd}", "");
        hdfsPath = hdfsPath.replace("/${HH}", "");
        hdfsPath = task.getHdfsTargetConfig().getRootPath() + hdfsPath;

        deleteDirInHdfs(hdfsPath);

        task.start();
        Thread thread = new Thread(task);
        thread.start();

        try {
            Thread.sleep(5 * 60 * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        task.flush();
        task.stop(true);
        assertTrue(getFiles(task.getLog2HdfsModel().getHdfsFileSystem().getFileSystem(), hdfsPath)
            .size() == 4);
    }

    @Test
    public void runMulti() {
        int taskNum = 3;
        Log2HdfsTask task = getTaskMutilTask(taskNum);
        task.init(task.getModelConfig());
        LogPath logPath = ((LogSource) (task.getSource())).getLogPath();

        String hdfsPath = getDirPathRule(task.getHdfsTargetConfig(), logPath);
        hdfsPath = hdfsPath.replace("/${yyyy}", "");
        hdfsPath = hdfsPath.replace("/${MM}", "");
        hdfsPath = hdfsPath.replace("/${dd}", "");
        hdfsPath = hdfsPath.replace("/${HH}", "");
        hdfsPath = task.getHdfsTargetConfig().getRootPath() + hdfsPath;

        deleteDirInHdfs(hdfsPath);

        task.start();
        Thread thread = new Thread(task);
        thread.start();

        try {
            Thread.sleep(5 * 60 * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        task.flush();
        task.stop(true);
        assertTrue(getFiles(task.getLog2HdfsModel().getHdfsFileSystem().getFileSystem(), hdfsPath)
            .size() == 4 * taskNum);
    }

    private Log2HdfsTask getTask() {
        ModelConfig modelConfig = getModelConfig();
        LogPath logPath = ((LogSourceConfig) modelConfig.getSourceConfig()).getLogPaths().get(0);
        LogSource logSource = new LogSource(modelConfig, logPath);
        Log2HdfsModel log2HdfsModel = getLog2HdfsModel(modelConfig);

        Log2HdfsTask task = new Log2HdfsTask(log2HdfsModel, modelConfig, logSource);
        return task;
    }

    private Log2HdfsTask getTaskMutilTask(int taskSize) {
        ModelConfig modelConfig = getModelConfig();
        modelConfig.getTargetConfig().setSinkNum(taskSize);
        LogPath logPath = ((LogSourceConfig) modelConfig.getSourceConfig()).getLogPaths().get(0);
        LogSource logSource = new LogSource(modelConfig, logPath);
        Log2HdfsModel log2HdfsModel = getLog2HdfsModel(modelConfig);

        Log2HdfsTask task = new Log2HdfsTask(log2HdfsModel, modelConfig, logSource);
        return task;
    }

    private Log2HdfsModel getLog2HdfsModel(ModelConfig modelConfig) {
        Log2HdfsModel log2HdfsModel = new Log2HdfsModel(modelConfig);
        log2HdfsModel.setHdfsFileSystem(HdfsFileSystemContainer.getHdfsFileSystem(log2HdfsModel));
        return log2HdfsModel;
    }

    private ModelConfig getModelConfig() {
        HdfsTargetConfig hdfsTargetConfig = new HdfsTargetConfig();
        hdfsTargetConfig.setRootPath(rootPath);
        hdfsTargetConfig.setHdfsPath("/hdfsPath/${path}/${yyyy}/${MM}/${dd}/${HH}");
        hdfsTargetConfig.setUsername("root");
        hdfsTargetConfig.setPassword("password");
        hdfsTargetConfig.setFlushBatchSize(10);

        ModelConfig modelConfig = new ModelConfig("log2hdfs");
        modelConfig.setTargetConfig(hdfsTargetConfig);

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setModelId(defaultModelId);
        modelConfig.setCommonConfig(commonConfig);

        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
        eventMetricsConfig.setOdinLeaf("hna");
        eventMetricsConfig.setOriginalAppName("");
        eventMetricsConfig.setTransName("");
        modelConfig.setEventMetricsConfig(eventMetricsConfig);

        LogSourceConfig logSourceConfig = getLogSourceConfig();
        modelConfig.setSourceConfig(logSourceConfig);

        ModelLimitConfig modelLimitConfig = new ModelLimitConfig();
        modelLimitConfig.setRate(10 * 1024 * 1024);
        modelConfig.setModelLimitConfig(modelLimitConfig);

        ChannelConfig channelConfig = new ChannelConfig();
        modelConfig.setChannelConfig(channelConfig);

        return modelConfig;
    }

    protected String getDirPathRule(HdfsTargetConfig hdfsTargetConfig, LogPath logPath) {
        String hdfsPath = hdfsTargetConfig.getHdfsPath();
        String parentPath = FileUtils.getPathDir(logPath.getRealPath());
        if (parentPath.startsWith("/")) {
            parentPath = parentPath.substring(1);
        }

        if (parentPath.endsWith("/")) {
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        }
        hdfsPath = hdfsPath.replace(LogConfigConstants.PATH_FLAG, parentPath);
        return hdfsPath;
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
