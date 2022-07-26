package com.didichuxing.datachannel.agentmanager.common.util;

import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author william.
 * 文件工具 类
 */
public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 上传给定文件
     * @param uploadFile 待上传文件信息
     * @param fileMd5 待上传文件 md 5 值
     * @return 文件存储 path
     */
    public static String upload(MultipartFile uploadFile, String fileMd5) {

        /*
         * TODO：文件 md 5 签名校验
         */

        String fileSeparator = System.getProperty("file.separator");
        String filePath = FileUtils.class.getResource("/").getPath() + "files" + fileSeparator + UUID.randomUUID().toString() + "_" + uploadFile.getOriginalFilename();
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new ServiceException(
                    String.format("文件上传失败，原因为：上传文件%s在后端创建失败", uploadFile.getOriginalFilename()),
                    ErrorCodeEnum.FILE_CREATE_FAILED.getCode()
            );
        }
        FileOutputStream fops = null;
        try {
            fops = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new ServiceException(
                    String.format("文件上传失败，原因为：上传文件%s在后端创建以后被删除", uploadFile.getOriginalFilename()),
                    ErrorCodeEnum.FILE_CREATE_FAILED.getCode()
            );
        }
        if(null != fops) {
            try {
                fops.write(uploadFile.getBytes());
                fops.flush();
            } catch (IOException e) {
                throw new ServiceException(
                        String.format("文件上传失败，原因为：上传文件%s在后端写入数据时出现异常", uploadFile.getOriginalFilename()),
                        ErrorCodeEnum.FILE_CREATE_FAILED.getCode()
                );
            } finally {
                try {
                    fops.close();
                } catch (IOException ex) {
                    LOGGER.error(
                            String.format("关闭文件%s输出流异常，原因为：%s", filePath, ex.getMessage()),
                            ex
                    );
                }
            }
        }
        return filePath;
    }

}
