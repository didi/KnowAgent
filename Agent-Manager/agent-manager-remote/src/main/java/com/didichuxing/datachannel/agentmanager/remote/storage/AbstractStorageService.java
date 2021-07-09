package com.didichuxing.datachannel.agentmanager.remote.storage;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zengqiao
 * @date 20/4/29
 */
public abstract class AbstractStorageService {
    /**
     * 上传
     */
    public abstract boolean upload(String fileName, String fileMd5, MultipartFile uploadFile);

    /**
     * 下载
     */
    public abstract Result<String> download(String fileName, String fileMd5);

    /**
     * 返回fileName对应文件下载地址
     * @param fileName 待下载文件文件名
     * @param fileMd5 待下载文件md5值
     * @return 返回fileName对应文件下载地址
     */
    public abstract String getDownloadUrl(String fileName, String fileMd5);

}
