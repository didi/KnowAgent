package com.didichuxing.datachannel.agent.integration.test.beans;

/**
 * @description: 切割后缀
 * @author: huangjw
 * @Date: 19/2/11 15:08
 */
public enum FileSuffixEnum {

    HOUR("hour", "yyyyMMddhh"), // 正常运行状态
    ONE_MIN("one_min_1", "yyyyMMddhhmm"), // 1分钟
    FIVE_MIN("one_min_5", "yyyy-MM-dd-hh-mm"), // 5分钟
    FILE_SIZE_10M("file_size_10MB", "1"), // 按大小切割 10M
    FILE_SIZE_10K("file_size_10kB", "1"); // 按大小切割 10K

    public static String FILE_SIZE_TAG = "file_size";
    public static String ONE_MIN_TAG   = "one_min";

    private String       name;

    private String       format;

    private FileSuffixEnum(String name, String format) {
        this.name = name;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
