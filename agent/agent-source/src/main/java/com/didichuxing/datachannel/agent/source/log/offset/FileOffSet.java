package com.didichuxing.datachannel.agent.source.log.offset;

import com.alibaba.fastjson.JSONObject;

/**
 * @description: 文件偏移量
 * @author: huangjw
 * @Date: 19/7/4 21:00
 */
public class FileOffSet {

    private Long   logID;

    private Long   pathID;

    private String parentPath;

    private String fileKey;

    // 可变
    private Long   offSet         = 0L;

    private Long   timeStamp      = 0L; // 一定有日志时间

    private String fileName;

    private Long   lastModifyTime = 0L; // 修改时间

    /**
     * 文件头部MD5
     */
    private String fileHeadMd5;

    public FileOffSet(Long logID, Long pathID, String parentPath, String fileKey) {
        this.logID = logID;
        this.pathID = pathID;
        this.parentPath = parentPath;
        this.fileKey = fileKey;
        this.offSet = 0L;
    }

    public FileOffSet(Long logID, Long pathID, String parentPath, String fileKey, Long offSet,
                      String fileName) {
        this.logID = logID;
        this.pathID = pathID;
        this.parentPath = parentPath;
        this.fileKey = fileKey;
        this.offSet = offSet;
        this.fileName = fileName;
    }

    public FileOffSet(JSONObject root) {
        this.logID = root.getLong(LOGID_STR);
        this.pathID = root.getLong(PATHID_STR);
        this.offSet = root.getLong(OFFSET_STR);
        this.timeStamp = root.getLong(TIMESTAMP_STR);
        this.parentPath = root.getString(PARENTPATH_STR);
        this.fileName = root.getString(FILENAME_STR);
        this.lastModifyTime = root.getLong(LAST_MODIFY_TIME_STR);

        if (root.containsKey(FILE_KEY)) {
            this.fileKey = root.getString(FILE_KEY);
        } else {
            this.fileKey = null;
        }

        // 兼容没有老的file offset中没有MD5字段
        if (root.containsKey(FILE_HEAD_MD5)) {
            this.fileHeadMd5 = root.getString(FILE_HEAD_MD5);
        } else {
            this.fileHeadMd5 = null;
        }
    }

    private static final String LOGID_STR            = "logId";
    private static final String PATHID_STR           = "pathId";
    private static final String OFFSET_STR           = "offSet";
    private static final String TIMESTAMP_STR        = "timeStamp";
    private static final String PARENTPATH_STR       = "parentPath";
    private static final String FILENAME_STR         = "fileName";
    private static final String FILE_KEY             = "fileKey";
    private static final String LAST_MODIFY_TIME_STR = "lastModifyTime";
    private static final String FILE_HEAD_MD5        = "fileHeadMd5";

    public JSONObject toJson() {
        JSONObject root = new JSONObject();

        root.put(LOGID_STR, logID);
        root.put(PATHID_STR, pathID);
        root.put(OFFSET_STR, offSet);
        root.put(TIMESTAMP_STR, timeStamp);
        root.put(PARENTPATH_STR, parentPath);
        root.put(FILENAME_STR, fileName);
        root.put(FILE_KEY, fileKey);
        root.put(LAST_MODIFY_TIME_STR, lastModifyTime);
        root.put(FILE_HEAD_MD5, fileHeadMd5);

        return root;
    }

    public String getOffsetKeyForOldVersion() {
        return logID + "_" + pathID + "_" + lastModifyTime;
    }

    public Long getLogID() {
        return logID;
    }

    public void setLogID(Long logID) {
        this.logID = logID;
    }

    public Long getOffSet() {
        return offSet;
    }

    public void setOffSet(Long offSet) {
        this.offSet = offSet;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getPathID() {
        return pathID;
    }

    public void setPathID(Long pathID) {
        this.pathID = pathID;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileKey == null) ? 0 : fileKey.hashCode());
        result = prime * result + ((logID == null) ? 0 : logID.hashCode());
        result = prime * result + ((pathID == null) ? 0 : pathID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileOffSet other = (FileOffSet) obj;
        if (fileKey == null) {
            if (other.fileKey != null) {
                return false;
            }
        } else if (!fileKey.equals(other.fileKey)) {
            return false;
        }
        if (logID == null) {
            if (other.logID != null) {
                return false;
            }
        } else if (!logID.equals(other.logID)) {
            return false;
        }
        if (pathID == null) {
            if (other.pathID != null) {
                return false;
            }
        } else if (!pathID.equals(other.pathID)) {
            return false;
        }
        return true;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileHeadMd5() {
        return fileHeadMd5;
    }

    public void setFileHeadMd5(String fileHeadMd5) {
        this.fileHeadMd5 = fileHeadMd5;
    }

    @Override
    public String toString() {
        return "FileOffSet{" + "logID=" + logID + ", pathID=" + pathID + ", parentPath='"
               + parentPath + '\'' + ", fileKey='" + fileKey + '\'' + ", offSet=" + offSet
               + ", timeStamp=" + timeStamp + ", fileName='" + fileName + '\''
               + ", lastModifyTime=" + lastModifyTime + ", fileHeadMd5='" + fileHeadMd5 + '\''
               + '}';
    }
}
