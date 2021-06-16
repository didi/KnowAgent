package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import com.didichuxing.datachannel.swan.agent.common.api.HdfsCompression;
import com.didichuxing.datachannel.swan.agent.common.api.HdfsTransFormat;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.targetConfig.TargetConfig;
import com.didichuxing.datachannel.swan.agent.common.constants.Tags;

/**
 * @description: hdfs config
 * @author: huangjw
 * @Date: 2019-07-11 14:28
 */
public class HdfsTargetConfig extends TargetConfig {

    /**
     * hdfs root path
     */
    private String  rootPath                = "/swan";

    /**
     * 用户名
     */
    private String  username;

    /**
     * 密码
     */
    private String  password;

    /**
     * 压缩方式
     */
    private int     compression             = HdfsCompression.TEXT.getStatus();

    /**
     * hdfs 目录
     * /huangjiaweihjw/${path}/${yyyy}/${MM}/${dd}/${HH}
     */
    private String  hdfsPath;

    /**
     * hdfs 文件
     */
    private String  hdfsFileName            = "/${hostname}_${filename}_${createTime}_${sourceId}_${sinkId}_${compression}";

    /**
     * 执行flush的批次大小
     */
    private Integer flushBatchSize          = 300;

    /**
     * 执行flush的超时时间
     */
    private Long    flushBatchTimeThreshold = 30000L;

    /**
     * 过滤字符串，以“，”分隔，可以有多个
     */
    private String  filterRule;

    /**
     * 过滤类型，0表示包含，1表示不包含，只有当filterRule字符不为空时才起作用
     */
    private Integer filterOprType           = 0;

    /**
     * 发送类型，默认异步发送
     */
    private Boolean isAsync                 = true;

    /**
     * 失败重试次数
     */
    private int     retryTimes              = 10;

    /**
     * 发送至hdfs数据格式，0表示纯字符串，1表示包装后的内容
     */
    private Integer transFormate            = HdfsTransFormat.HDFS_STRING.getStatus();

    public HdfsTargetConfig() {
        super(Tags.TARGET_HDFS);
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCompression() {
        return compression;
    }

    public void setCompression(int compression) {
        this.compression = compression;
    }

    public Integer getFlushBatchSize() {
        return flushBatchSize;
    }

    public void setFlushBatchSize(Integer flushBatchSize) {
        this.flushBatchSize = flushBatchSize;
    }

    public Long getFlushBatchTimeThreshold() {
        return flushBatchTimeThreshold;
    }

    public void setFlushBatchTimeThreshold(Long flushBatchTimeThreshold) {
        this.flushBatchTimeThreshold = flushBatchTimeThreshold;
    }

    public String getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(String filterRule) {
        this.filterRule = filterRule;
    }

    public Integer getFilterOprType() {
        return filterOprType;
    }

    public void setFilterOprType(Integer filterOprType) {
        this.filterOprType = filterOprType;
    }

    public Boolean getAsync() {
        return isAsync;
    }

    public void setAsync(Boolean async) {
        isAsync = async;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public String getHdfsFileName() {
        return hdfsFileName;
    }

    public void setHdfsFileName(String hdfsFileName) {
        this.hdfsFileName = hdfsFileName;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public Integer getTransFormate() {
        return transFormate;
    }

    public void setTransFormate(Integer transFormate) {
        this.transFormate = transFormate;
    }

    @Override
    public String toString() {
        return "HdfsTargetConfig{" + "rootPath='" + rootPath + '\'' + ", username='" + username
               + '\'' + ", password='" + password + '\'' + ", compression=" + compression
               + ", hdfsPath='" + hdfsPath + '\'' + ", hdfsFileName='" + hdfsFileName + '\''
               + ", flushBatchSize=" + flushBatchSize + ", flushBatchTimeThreshold="
               + flushBatchTimeThreshold + ", filterRule='" + filterRule + '\''
               + ", filterOprType=" + filterOprType + ", isAsync=" + isAsync + ", retryTimes="
               + retryTimes + ", transFormate=" + transFormate + '}';
    }
}
