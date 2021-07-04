## k8s接入指南

### 以yml方式新建k8s pod

1. 新建k8s pod的yaml文件

2. 在annotations中，指定名为servicename的标签，servicename对应值对应agent-manager中的服务名（见 ”标注1“），如需修改标签名servicename为其他标签名，需要同步修改 agent-manager 配置文件 application.yml 中的metadata.sync.request.k8s.service-key 属性值为新的标签名

3. 将volumeMounts的name属性值和volumes的name属性值需设置为logpath（见 ”标注2、3“），如需要改变name属性值，除了同步修改标注2、3处外，需要修改 agent-manager 配置文件 application.yml 中的metadata.sync.request.k8s.path-name 属性值为新的name属性值

4. 设置volumeMounts的mountPath属性值（见 ”标注4“），该属性值表示容器内路径

5. 设置volumes的type属性值为Directory或DirectoryOrCreate（见 ”标注5“）

6. 设置volumes的hostPath属性值（见 “标注6”），该属性值表示pod在宿主机上对应挂载路径

   举例：如mountPath属性值设置为/workspace/info，hostPath属性值设置为/data，容器内日志文件路径为 /workspace/info/logs/std.log，则该日志文件在宿主机路径为：/data/logs/std.log

   示例如下：

   ```yaml
   apiVersion: v1
   kind: Pod
   metadata: 
   	name: log-collect
      namespace: default
   	annotations:
   		- servicename: k8s_test -- 标注1
   spec: 
   	containers:
   		- name: file1
   			image: test:1.0
   			volumeMounts:
   				- mountPath: /workspace/info -- 标注4
   					name: logpath -- 标注2
   	volumes: 
   		- name: logpath -- 标注3
   			hostPath: /data -- 标注6
   			type: Directory -- 标注5
     nodeName: hostname
   ```



### 导入k8s config

1. 在该k8s集群的任意一台主机上，寻找~/.kube/config文件。

2. 复制config文件到jar包的同级目录下。

3. 在application.yml中，修改如下配置

4. 修改 agent-manager 配置文件 application.yml 中的metadata.sync.request.k8s.enabled 属性值为 true

   应用、主机、容器元数据每10分钟从k8s更新一次，等待更新完成之后即可在agent-manager平台看到应用、主机、容器列表信息



### 新建采集任务注意事项

1. 新建采集任务，指定采集应用名为servicename标签对应的值
2. 容器采集场景下，日志采集路径指定容器内路径

