package com.didichuxing.datachannel.swan.agent.task.log.log2hdfs;

import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsFileSystem;
import org.junit.Before;
import org.junit.Test;

import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsTargetConfig;
import com.didichuxing.datachannel.swan.agent.task.log.LogModelTest;

import static org.junit.Assert.assertTrue;

/**
 * @description:
 * @author: huangjw
 * @Date: 2020-01-10 14:39
 */
public class HdfsFileSystemContainerTest extends LogModelTest {

    private static final String rootPath   = "hdfs://10.179.132.92:9000/huangjiaweihjw";
    private static final String rootPath_1 = "hdfs://10.179.132.92:9000/huangjiaweihjw_1";
    private static final String username   = "root";

    @Before
    public void init() {
        Log2HdfsModel log2HdfsModel = getLog2HdfsModel();
        HdfsFileSystemContainer.register(log2HdfsModel);
    }

    @Test
    public void changeFileSystem() {
        Log2HdfsModel oldOne = getLog2HdfsModel();
        Log2HdfsModel newOne = getLog2HdfsModel();
        ((HdfsTargetConfig) newOne.getModelConfig().getTargetConfig()).setRootPath(rootPath_1);

        HdfsFileSystemContainer.changeFileSystem(oldOne, newOne.getModelConfig(),
            oldOne.getModelConfig());
        HdfsFileSystem newHdfsFileSystem = HdfsFileSystemContainer.getHdfsFileSystem(newOne);
        HdfsFileSystem oldHdfsFileSystem = HdfsFileSystemContainer.getHdfsFileSystem(oldOne);

        assertTrue(newHdfsFileSystem != null && oldHdfsFileSystem != null);

        HdfsFileSystemContainer.release(oldOne, oldOne.getModelConfig());
    }

    Log2HdfsModel getLog2HdfsModel() {
        ModelConfig modelConfig = getModelConfig();
        HdfsTargetConfig hdfsTargetConfig = new HdfsTargetConfig();
        hdfsTargetConfig.setRootPath(rootPath);
        hdfsTargetConfig.setPassword("");
        hdfsTargetConfig.setUsername(username);
        hdfsTargetConfig.setHdfsPath("/test");
        modelConfig.setTargetConfig(hdfsTargetConfig);

        Log2HdfsModel hdfsModel = new Log2HdfsModel(modelConfig);
        return hdfsModel;
    }
}
