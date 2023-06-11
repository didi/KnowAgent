package com.didichuxing.datachannel.agent.integration.test.datasource;

import com.didichuxing.datachannel.agent.integration.test.beans.FileSuffixEnum;
import com.didichuxing.datachannel.agent.integration.test.format.Format;
import com.didichuxing.datachannel.agent.integration.test.format.OriginalFormat;
import org.apache.log4j.Logger;

/**
 * @description: 5分钟切割一次的文件
 * @author: huangjw
 * @Date: 19/2/11 16:31
 */
public class DailyFiveMinDataSource extends BasicDataSource {

    public DailyFiveMinDataSource(String sourceFile, String targetFile) {
        this.targetFile = targetFile;
        this.sourceFile = sourceFile;
        init();
    }

    public DailyFiveMinDataSource(Long interval, String sourceFile, String targetFile,
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
        Logger logger = logConfiguration.buildLogger(targetFile, FileSuffixEnum.FIVE_MIN,
            DailyFiveMinDataSource.class);
        super.init(interval != 0L ? interval : 10L, logger, sourceFile, targetFile,
            this.getClass(), format != null ? format : new OriginalFormat(),
            FileSuffixEnum.FIVE_MIN);
    }
}
