package com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.util;

import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.domain.PodConfig;
import com.didichuxing.datachannel.agentmanager.thirdpart.metadata.k8s.domain.PodReference;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1HostPathVolumeSource;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * k8s 工具类
 */
public class K8sUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(K8sUtil.class);
    /**
     * k8s client api
     */
    private static CoreV1Api api = null;

    /**
     * 获取 k8s client api
     *
     * @return 返回 k8s client api
     * @throws IOException k8s client 创建出现异常
     */
    public static synchronized CoreV1Api getK8sApi() throws IOException {
        if (null == api) {
            Resource resource;
            resource = new FileSystemResource("config");
            if (!resource.exists()) {
                throw new ServiceException("找不到k8s集群对应的config文件", ErrorCodeEnum.K8S_POD_CONFIG_PULL_FAILED.getCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(br)).build();
            Configuration.setDefaultApiClient(client);
            api = new CoreV1Api();
        }
        return api;
    }

    /**
     * 获取给定 k8s 集群所有 pod 配置信息
     *
     * @return 返回获取到的给定 k8s 集群所有 pod 配置信息
     * @throws Exception 获取给定 k8s 集群所有 pod 配置信息时出现的异常
     */
    private static V1PodList listPods() throws Exception {
        return K8sUtil.getK8sApi().listPodForAllNamespaces(null, null, null, null, null, null, null, null, 30, null);
    }

    /**
     * 将V1Pod对象转化为PodConfig对象
     *
     * @param v1Pod 待转化V1Pod对象
     * @return 将V1Pod对象转化为的PodConfig对象
     * @throws Exception 将V1Pod对象转化为PodConfig对象过程中出现的异常
     */
    private static PodConfig convert(V1Pod v1Pod) throws Exception {
        PodConfig config = new PodConfig();
        V1ObjectMeta meta = v1Pod.getMetadata();
        if (meta == null) {
            throw new Exception(String.format("V1ObjectMeta is null, pod: {%s}", v1Pod.toString()));
        }
        config.setUuid(meta.getUid());
        config.setAnnotations(meta.getAnnotations());
        V1PodSpec spec = v1Pod.getSpec();
        if (spec == null) {
            throw new Exception(String.format("V1PodSpec is null, pod: {%s}", v1Pod.toString()));
        }
        List<V1Container> containers = spec.getContainers();
        List<String> containerNames = new ArrayList<>();

        for (V1Container container : containers) {
            containerNames.add(container.getName());
        }
        config.setContainerNames(containerNames);
        config.setNodeName(spec.getNodeName());

        V1PodStatus status = v1Pod.getStatus();
        if (status == null) {
            throw new Exception(String.format("V1PodStatus is null, pod: {%s}", v1Pod.toString()));
        }
        config.setNodeIp(status.getHostIP());
        config.setPodIp(status.getPodIP());

        List<V1Volume> volumes = spec.getVolumes();
        if (volumes == null) {
            config.setReferenceMap(null);
            return config;
        }
        // 以下为设置host path映射的逻辑，若没有则在上一句中return
        Map<String, PodReference> referenceMap = new HashMap<>();
        for (V1Volume volume : volumes) {
            V1HostPathVolumeSource hostPath = volume.getHostPath();
            if (hostPath == null) {
                continue;
            }
            String name = volume.getName();
            if ((!"Directory".equals(hostPath.getType())) && (!"DirectoryOrCreate".equals(hostPath.getType()))) {
                continue;
            }
            PodReference reference = new PodReference();
            reference.setName(name);
            reference.setHostPath(hostPath.getPath());
            referenceMap.put(name, reference);
        }
        for (V1Container container : spec.getContainers()) {
            for (V1VolumeMount volumeMount : container.getVolumeMounts()) {
                PodReference reference = referenceMap.get(volumeMount.getName());
                if (reference != null) {
                    reference.setMountPath(volumeMount.getMountPath());
                }
            }
        }
        config.setReferenceMap(referenceMap);
        return config;
    }

    /**
     * @return 返回给定 k8s 集群所有 pod 配置信息
     */
    public static List<PodConfig> getAllPodConfig() {
        try {
            V1PodList podList = listPods();
            List<PodConfig> configs = new ArrayList<>();
            for (V1Pod item : podList.getItems()) {
                configs.add(convert(item));
            }
            return configs;
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format("%s: %s", ErrorCodeEnum.K8S_POD_CONFIG_PULL_FAILED.getMessage(), ex.getMessage()),
                    ex,
                    ErrorCodeEnum.K8S_POD_CONFIG_PULL_FAILED.getCode()
            );
        }
    }

    /**
     * 容器与主机通过hostPath共享文件，通过mount和volume的hostPath，获取日志文件的主机路径
     *
     * @param mountPath 容器内挂载的共享目录的路径
     * @param hostPath  共享目录对应的主机路径
     * @param filePath  日志文件的容器内路径
     * @return 日志文件的主机路径
     */
    public static String getRealPath(String mountPath, String hostPath, String filePath) {
        return filePath.replaceFirst(mountPath, hostPath);
    }

}

