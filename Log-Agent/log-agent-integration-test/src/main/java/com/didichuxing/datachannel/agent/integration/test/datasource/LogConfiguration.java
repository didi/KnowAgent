package com.didichuxing.datachannel.agent.integration.test.datasource;

import com.didichuxing.datachannel.agent.integration.test.beans.FileSuffixEnum;
import org.apache.log4j.*;

/**
 * @description: 日志配置
 * @author: huangjw
 * @Date: 19/2/11 15:35
 */
public class LogConfiguration {

    public Logger buildLogger(String logFilePath, FileSuffixEnum fileSuffixEnum, Class clazz) {
        // 使logger生效
        Logger logger = Logger.getLogger(clazz.getName());
        logger.removeAllAppenders();
        logger.setLevel(Level.INFO);
        logger.setAdditivity(false);

        if (fileSuffixEnum.getName().startsWith(FileSuffixEnum.FILE_SIZE_TAG)) {
            // 按大小滚动
            RollingFileAppender appender = new RollingFileAppender();
            PatternLayout layout = new PatternLayout();
            layout.setConversionPattern("%p, %t, %m%n");
            appender.setEncoding("UTF-8");
            appender.setName(clazz.getName());
            appender.setAppend(true);
            appender.setFile(logFilePath);
            appender.setMaxFileSize(fileSuffixEnum.getName().substring(
                FileSuffixEnum.FILE_SIZE_TAG.length() + 1));
            appender.setMaxBackupIndex(10);
            appender.setLayout(layout);
            appender.activateOptions();

            logger.addAppender(appender);
        } else {
            // 按时间滚动
            if (fileSuffixEnum.getName().startsWith(FileSuffixEnum.ONE_MIN_TAG)) {
                // 按照分钟滚动
                MinDailyRollingFileAppender appender = new MinDailyRollingFileAppender(
                    Integer.parseInt(fileSuffixEnum.getName().substring(
                        FileSuffixEnum.ONE_MIN_TAG.length() + 1)));
                PatternLayout layout = new PatternLayout();
                layout.setConversionPattern("%p, %t, %m%n");
                appender.setEncoding("UTF-8");
                appender.setName(clazz.getName());
                appender.setAppend(true);
                appender.setFile(logFilePath);
                appender.setDatePattern("'.'" + fileSuffixEnum.getFormat());
                appender.setLayout(layout);
                appender.activateOptions();

                logger.addAppender(appender);
            } else {
                // 按小时滚动
                DailyRollingFileAppender appender = new DailyRollingFileAppender();
                PatternLayout layout = new PatternLayout();
                layout.setConversionPattern("%p, %t, %m%n");
                appender.setEncoding("UTF-8");
                appender.setName(clazz.getName());
                appender.setAppend(true);
                appender.setFile(logFilePath);
                appender.setDatePattern("'.'" + fileSuffixEnum.getFormat());
                appender.setLayout(layout);
                appender.activateOptions();

                logger.addAppender(appender);
            }
        }

        return logger;
    }
}
