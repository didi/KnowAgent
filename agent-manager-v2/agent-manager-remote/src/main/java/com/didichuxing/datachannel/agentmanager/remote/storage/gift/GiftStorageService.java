package com.didichuxing.datachannel.agentmanager.remote.storage.gift;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.remote.storage.AbstractStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * author: konglingxu
 * Date: 2020/11/4
 * Time: 上午11:30
 */
@Service("giftStorageService")
public class GiftStorageService extends AbstractStorageService {

  private final static Logger LOGGER = LoggerFactory.getLogger(GiftStorageService.class);

  @Autowired
  private MinioClient minioClient;

  @Value("${kcm.gift.endpoint}")
  private String endpoint;

  @Value("${kcm.gift.bucket}")
  private String bucket;

  @Override
  public boolean upload(String fileName, String fileMd5, MultipartFile uploadFile) {
    InputStream inputStream = null;
    try {
      boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
      if (!found) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }

      inputStream = uploadFile.getInputStream();

      PutObjectArgs objectArgs = PutObjectArgs.builder()
          .bucket(bucket)
          .object(fileName)
          .stream(inputStream, inputStream.available(), -1)
          .build();

      minioClient.putObject(objectArgs);

      return true;
    } catch (Exception e) {
      LOGGER.error("upload object to Gift error...", e);
      return false;
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
        }
      }
    }
  }

  @Override
  public Result<String> download(String fileName, String fileMd5) {
    try {
      Assert.hasLength(fileName, "parameter fileName can not be empty...");
      //return new Result<>(minioClient.getObjectUrl(bucket, fileName));

      return Result.buildSucc(minioClient.presignedGetObject(bucket, fileName));
    } catch (Exception e) {
      LOGGER.error("get object URL from Gift error...", e);
      return Result.build(
              ErrorCodeEnum.FILE_DOWNLOAD_FAILED.getCode(),
              String.format("文件={fileName=%s, fileMd5=%s}下载失败", fileName, fileMd5)
      );
    }
  }

  @Override
  public String getDownloadUrl(String fileName, String fileMd5) {
    try {
      return minioClient.presignedGetObject(bucket, fileName);
    } catch (Exception ex) {
      throw new ServiceException(
              String.format("文件={fileName=%s,fileMd5=%s}下载链接获取失败", fileName, fileMd5),
              ErrorCodeEnum.FILE_DOWNLOAD_FAILED.getCode()
      );
    }
  }

}
