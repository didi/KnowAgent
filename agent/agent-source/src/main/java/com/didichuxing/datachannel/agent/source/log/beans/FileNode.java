package com.didichuxing.datachannel.agent.source.log.beans;

import java.io.File;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.source.log.offset.FileOffSet;

/**
 * @description: 文件
 * @author: huangjw
 * @Date: 19/7/4 21:12
 */
public class FileNode {

    private Long       modelId;
    private Long       pathId;
    private String     parentPath;
    private String     fileKey;

    // 可变
    private Long       modifyTime;
    private String     fileName;
    private Long       length;
    private FileOffSet fileOffSet;

    private Boolean    needCollect = true;
    private Boolean    isDelete    = false;

    private File       file;

    public FileNode(Long modelId, Long pathId, String fileKey, Long modifyTime, String parentPath,
                    String fileName, Long length, File file) {
        this.modelId = modelId;
        this.pathId = pathId;
        this.parentPath = parentPath;
        this.fileKey = fileKey;

        this.modifyTime = modifyTime;
        this.fileName = fileName;
        this.length = length;

        this.file = file;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getModelId() {
        return modelId;
    }

    public Long getPathId() {
        return pathId;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
        this.fileOffSet.setLastModifyTime(modifyTime);
    }

    public void setFileOffSet(FileOffSet fileOffSet) {
        this.fileOffSet = fileOffSet;
    }

    public FileOffSet getFileOffSet() {
        return fileOffSet;
    }

    public String getParentPath() {
        return parentPath;
    }

    public Long getOffset() {
        return fileOffSet.getOffSet();
    }

    public void setOffset(Long offset) {
        fileOffSet.setOffSet(offset);
    }

    public void setOffsetTimeStamp(Long timeStamp) {
        fileOffSet.setTimeStamp(timeStamp);
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public boolean isDelete() {
        return this.isDelete;
    }

    public Boolean getNeedCollect() {
        return needCollect;
    }

    public void setNeedCollect(Boolean needCollect) {
        this.needCollect = needCollect;
    }

    public String getNodeKey() {
        return modelId + LogConfigConstants.UNDERLINE_SEPARATOR + pathId
               + LogConfigConstants.UNDERLINE_SEPARATOR + fileKey;
    }

    public String getFileKey() {
        return fileKey;
    }

    public String getAbsolutePath() {
        return parentPath + File.separator + fileName;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getLength() {
        return length;
    }

    @Override
    public FileNode clone() {
        FileNode newFileNode = new FileNode(this.modelId, this.pathId, this.fileKey,
            this.modifyTime, this.parentPath, this.fileName, this.length, this.file);
        return newFileNode;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "FileNode{" + "modelId=" + modelId + ", pathId=" + pathId + ", parentPath='"
               + parentPath + '\'' + ", fileKey='" + fileKey + '\'' + ", modifyTime=" + modifyTime
               + ", fileName='" + fileName + '\'' + ", length=" + length + ", fileOffSet="
               + fileOffSet + ", needCollect=" + needCollect + ", isDelete=" + isDelete + ", file="
               + file + '}';
    }

}
