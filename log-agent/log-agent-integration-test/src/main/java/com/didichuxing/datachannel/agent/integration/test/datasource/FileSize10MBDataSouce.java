package com.didichuxing.datachannel.agent.integration.test.datasource;

import com.didichuxing.datachannel.agent.integration.test.beans.FileSuffixEnum;
import com.didichuxing.datachannel.agent.integration.test.format.Format;
import com.didichuxing.datachannel.agent.integration.test.format.OriginalFormat;
import org.apache.log4j.Logger;

/**
 * @description: 10M一滚动的切割形式
 * @author: huangjw
 * @Date: 19/2/11 16:23
 */
public class FileSize10MBDataSouce extends BasicDataSource {

    String targetFile = null;
    String sourceFile = null;

    public FileSize10MBDataSouce(String sourceFile, String targetFile) {
        this.targetFile = targetFile;
        this.sourceFile = sourceFile;
        init();
    }

    public FileSize10MBDataSouce(Long interval, String sourceFile, String targetFile,
                                 Format format, FileSuffixEnum fileSuffixEnum) {
        this.targetFile = targetFile;
        this.sourceFile = sourceFile;
        this.format = format;
        this.fileSuffixEnum = fileSuffixEnum;
        this.interval = interval;
        init();
    }

    public void init() {
        LogConfiguration logConfiguration = new LogConfiguration();
        Logger logger = logConfiguration.buildLogger(targetFile, FileSuffixEnum.FILE_SIZE_10M,
            FileSize10MBDataSouce.class);
        super.init(interval != 0L ? interval : 10L, logger, sourceFile, targetFile,
            this.getClass(), format != null ? format : new OriginalFormat(),
            FileSuffixEnum.FILE_SIZE_10M);
    }
}
