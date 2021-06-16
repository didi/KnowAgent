package com.didichuxing.datachannel.agentmanager.remote.storage.gift;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author: konglingxu
 * Date: 2020/11/4
 * Time: 上午11:32
 */
@Configuration
public class GiftV3Config {

  @Value("${kcm.gift.endpoint}")
  private String endpoint;

  @Value("${kcm.gift.accessKeyID}")
  private String accessKeyID;

  @Value("${kcm.gift.secretAccessKey}")
  private String secretAccessKey;

  @Bean
  public MinioClient minioClient() {
    MinioClient minioClient = new MinioClient(endpoint, accessKeyID, secretAccessKey);

    return minioClient;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getAccessKeyID() {
    return accessKeyID;
  }

  public String getSecretAccessKey() {
    return secretAccessKey;
  }
}
