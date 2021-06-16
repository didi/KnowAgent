package com.didichuxing.datachannel.swan.agent.sink.hdfsSink;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description: hdfs file system管理器
 * @author: huangjw
 * @Date: 2019-07-16 16:54
 */
public class HdfsFileSystem {

    private final static ILog         LOGGER          = LogFactory.getLog(HdfsFileSystem.class.getName());

    private final static String       US_TAG          = ".us01";

    private FileSystem                fileSystem;
    private String                    username;
    private String                    rootPath;
    private String                    password;

    private Map<String, HdfsDataFile> hdfsDataFileMap = new ConcurrentHashMap<>();

    public HdfsFileSystem(String rootPath, String username, String password){
        this.username = username;
        this.rootPath = rootPath;
        if (StringUtils.isNotBlank(password)) {
            // 密码需要解密
            this.password = CommonUtils.decode(password);
        }
        this.fileSystem = buildFileSystem(this.rootPath, this.username, this.password);
    }

    private FileSystem buildFileSystem(String rootPath, String username, String password) {
        LOGGER.info("begin to build hdfs fileSystem. rootPath is " + rootPath + ",username is " + username);
        try {
            Configuration conf = new Configuration();
            String hostname = CommonUtils.getHOSTNAME();
            if (hostname.endsWith(US_TAG)) {
                LOGGER.info("begin to use us's hdfs config.");
                conf.addResource("hdfs/us/core-site.xml");
                conf.addResource("hdfs/us/hdfs-site.xml");
            } else {
                LOGGER.info("begin to use cn's hdfs config.");
                conf.addResource("hdfs/cn/core-site.xml");
                conf.addResource("hdfs/cn/hdfs-site.xml");
            }
            FileSystem fileSystem = null;
            if (StringUtils.isNotBlank(password)) {
                fileSystem = FileSystem.get(URI.create(rootPath), conf, username, password);
                return fileSystem;
            } else {
                fileSystem = FileSystem.get(URI.create(rootPath), conf, username);
                return fileSystem;
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("HdfsFileSystem error", "HdfsFileSystem error! username is " + username, e);
        }
        return null;
    }

    public void reBuildDataFile() {
        LOGGER.info("begin to reBuildDataFile hdfsFileSystem.rootPath is " + rootPath + ", username is " + username);
        for (HdfsDataFile hdfsDataFile : hdfsDataFileMap.values()) {
            try {
                hdfsDataFile.reBuild();
            } catch (Exception e) {
                LogGather.recordErrorLog("HdfsFileSystem error",
                                         "reBuild error! hdfsDataFile's uniqueKey  is " + hdfsDataFile.getUniqueKey(),
                                         e);
            }
        }
    }

    public void reBuildSystem() {
        LOGGER.info("begin to reBuildSystem hdfsFileSystem.rootPath is " + rootPath + ", username is " + username);
        this.fileSystem = buildFileSystem(this.rootPath, this.username, this.password);
    }

    public void appendHdfsDataFile(HdfsDataFile hdfsDataFile) {
        hdfsDataFileMap.put(hdfsDataFile.getUniqueKey(), hdfsDataFile);
    }

    public void removeHdfsDataFile(HdfsDataFile hdfsDataFile) {
        if (hdfsDataFileMap.containsKey(hdfsDataFile.getUniqueKey())) {
            hdfsDataFileMap.remove(hdfsDataFile.getUniqueKey());
        }
    }

    /**
     *
     * 健康检查
     * 
     * @return
     */
    public boolean checkHealth() {
        try {
            this.fileSystem.getServerDefaults(new Path("/"));
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("HdfsFileSystem error", "checkHealth error!", e);
            return false;
        }
    }

    public boolean close() {
        LOGGER.info("begin to close hdfsFileSystem. username is " + this.username + ", rootPath is " + this.rootPath);
        try {
            hdfsDataFileMap.clear();
            fileSystem.close();
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("HdfsFileSystem error", "close error!", e);
        }
        return false;
    }

    private String getKey() {
        return this.username + "_" + this.password;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
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

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
