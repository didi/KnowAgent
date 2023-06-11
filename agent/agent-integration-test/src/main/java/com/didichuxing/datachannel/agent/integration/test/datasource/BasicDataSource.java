package com.didichuxing.datachannel.agent.integration.test.datasource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.agent.integration.test.beans.FileSuffixEnum;
import com.didichuxing.datachannel.agent.integration.test.format.Format;
import com.didichuxing.datachannel.agent.integration.test.utils.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.didichuxing.datachannel.agent.source.log.utils.BufferedRandomAccessFile;

/**
 * @description: 数据源
 * @author: huangjw
 * @Date: 19/2/11 14:43
 */
public class BasicDataSource implements Runnable {

    protected long                    interval;

    protected String                  targetFile   = null;
    protected String                  sourceFile   = null;

    private Class                     appenderName = null;

    Logger                            logger;

    private boolean                   isStop       = false;

    Format format;
    FileSuffixEnum fileSuffixEnum;

    ConcurrentHashMap<String, String> map          = new ConcurrentHashMap<>();

    // 最大行数 默认是1000
    int                               maxNum       = 1000;

    public void init(long interval, Logger logger, String sourceFile, String targetFile, Class appenderName,
                     Format format, FileSuffixEnum fileSuffixEnum) {
        this.interval = interval;
        this.logger = logger;
        this.sourceFile = sourceFile;
        this.appenderName = appenderName;
        this.format = format;
        this.fileSuffixEnum = fileSuffixEnum;
        this.targetFile = targetFile;
    }

    @Override
    public void run() {
        try {
            removeOldFiles();

            BufferedRandomAccessFile file = new BufferedRandomAccessFile(new File(sourceFile), "r");
            int num = 0;
            while (!isStop) {
                String line = readLine(file);
                line = new Date(System.currentTimeMillis()) + " " + line;
                if (line != null && line.length() > 0) {
                    String recordToFile = logger.getAppender(appenderName.getName()).getLayout().format(new LoggingEvent(Logger.class.getName(),
                                                                                                                         logger,
                                                                                                                         Level.INFO,
                                                                                                                         line,
                                                                                                                         null));
                    if (map != null) {
                        String md5 = Md5Util.getMd5(format.format(recordToFile));
                        if (StringUtils.isNotBlank(md5)) {
                            map.put(md5, "");
                        }
                    }
                    logger.info(line);

                    Thread.sleep(interval);
                } else {
                    file.seek(0);
                }
                num++;
                if (num > maxNum) {
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void removeOldFiles() {
        File file = new File(this.targetFile);
        String fileName = file.getName();
        File dir = file.getParentFile();
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                String fName = f.getName();
                if ((fName.startsWith(fileName + ".") || fName.equals(fileName) && f.isFile())) {
                    f.delete();
                }
            }
        }
    }

    /**
     * 停止线程
     */
    public void stop() {
        isStop = true;
    }

    /**
     * 按行读取文件
     *
     * @param in
     * @return
     * @throws Exception
     */
    private String readLine(BufferedRandomAccessFile in) throws Exception {
        byte[] lineContent = in.readNewLine(100L);
        if (lineContent == null) {
            return null;
        }
        return new String(lineContent, StandardCharsets.UTF_8);
    }

    public ConcurrentHashMap<String, String> getMap() {
        return map;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public FileSuffixEnum getFileSuffixEnum() {
        return fileSuffixEnum;
    }

    public void setFileSuffixEnum(FileSuffixEnum fileSuffixEnum) {
        this.fileSuffixEnum = fileSuffixEnum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

}
