## k8s安装使用及接入文档

### 1. 环境准备

- `docker`
- `kubeadm`, `kubectl`, `kubelet` v1.18+
- 物理机两台以上

### 2. 启动服务

**本节的步骤在所有机器中都需要执行，且均以root身份操作。**

- 安装docker

  ```bash
  $ yum -y install docker
  $ docker -v # 检查安装成功
  Docker version 1.13.1, build 7d71120/1.13.1
  $ systemctl enable docker && systemctl start docker # 启动docker服务
  ```

- 添加k8s的yum源
  注：由于yum默认无k8s源，因此需要手动添加

  ```bash
  $ cat > /etc/yum.repos.d/kubernetes.repo << EOF
  [kubernetes]
  name=Kubernetes
  baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
  enabled=1
  gpgcheck=1
  repo_gpgcheck=1
  gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
  EOF
  ```

- 安装k8s

  ```bash
  $ yum -y install kubeadm kubectl kubelet
  
  $ kubelet --version # 检查安装成功
  Kubernetes v1.22.0
  
  $ systemctl enable kubelet && systemctl start kubelet # 启动docker服务
  $ systemctl status kubelet # 查看kubelet状态
  ```

- 如果启动kubelet时报错`failed to run Kubelet: misconfiguration: kubelet cgroup driver: "cgroupfs" is different from docker cgroup driver: "systemd"` 该错误原因为kubelet的cgroup引擎和docker不一致，可以修改kubelet启动参数，将cgroup driver设置为systemd（默认为cgroupfs），修改后的内容如下

  ```bash
  $ vim /usr/lib/systemd/system/kubelet.service.d/10-kubeadm.conf
  
  # Note: This dropin only works with kubeadm and kubelet v1.11+
  [Service]
  Environment="KUBELET_KUBECONFIG_ARGS=--bootstrap-kubeconfig=/etc/kubernetes/bootstrap-kubelet.conf --kubeconfig=/etc/kubernetes/kubelet.conf"
  Environment="KUBELET_CONFIG_ARGS=--config=/var/lib/kubelet/config.yaml --cgroup-driver=systemd --runtime-cgroups=/systemd/system.slice --kubelet-cgroups=/systemd/system.slice"
  # This is a file that "kubeadm init" and "kubeadm join" generates at runtime, populating the KUBELET_KUBEADM_ARGS variable dynamically
  EnvironmentFile=-/var/lib/kubelet/kubeadm-flags.env
  # This is a file that the user can use for overrides of the kubelet args as a last resort. Preferably, the user should use
  # the .NodeRegistration.KubeletExtraArgs object in the configuration files instead. KUBELET_EXTRA_ARGS should be sourced from this file.
  EnvironmentFile=-/etc/sysconfig/kubelet
  ExecStart=/usr/bin/kubelet $KUBELET_KUBECONFIG_ARGS $KUBELET_CONFIG_ARGS $KUBELET_KUBEADM_ARGS $KUBELET_EXTRA_ARGS
  ```

- 修改完成后，重启kubelet。

  ```bash
  $ systemctl daemon-reload
  $ systemctl restart kubelet
  ```

- 修改网络配置

  ```bash
  $ cat > /etc/sysctl.d/k8s.conf << EOF
  net.bridge.bridge-nf-call-ip6tables=1
  net.bridge.bridge-nf-call-iptables=1
  EOF
  $ sysctl --system
  ```

### 3. 初始化master节点

- 生成配置文件`init.yaml`

  ```bash
  $ kubeadm config print init-defaults > init.yaml
  ```

- 修改文件内容，总共需要修改两处

  ```yml
  localAPIEndpoint:
    advertiseAddress: 10.255.1.24 # 该机器的ip地址
  imageRepository: registry.cn-hangzhou.aliyuncs.com/google_containers # 国内镜像地址
  ```

- 下载镜像

  ```bash
  $ kubeadm config images pull --config init.yaml
  ```

- 关闭内存交换

  ```bash
  $ swapoff -a
  ```

- 初始化集群

  ```bash
  $ kubeadm init --config init.yaml
  ```

- 保存集群配置
  
  ```bash
  $ mkdir -p $HOME/.kube
  $ sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  $ sudo chown $(id -u):$(id -g) $HOME/.kube/config
  ```

- 查看集群信息，此处node的状态应为not ready。

  ```bash
  $ kubectl get nodes
  NAME           STATUS   ROLES                  AGE   VERSION
  node           NotReady    master                 43m   v1.22.0
  ```

- 配置网络环境

  ```bash
  $ wget https://docs.projectcalico.org/manifests/calico.yaml
  ```

- 修改环境配置，在`CLUSTER_TYPE`下增加两行

  ```yaml
  env:
    - name: CLUSTER_TYPE
    value: "k8s,bgp"
    - name: IP_AUTODETECTION_METHOD # 增加如下两行
    value: "interface=eth.*" # 此处的值取决于系统版本，CentOS7以上为interface=en.*。如不确定，可以用ifconfig查看，找到eth0或en0一项即可确定。
  ```


### 4. 加入worker节点

- 在master节点中获取token

  ```bash
  $ kubeadm token list
  ```

- 如果无法获取，说明token已过期，可以重新生成一个

  ```bash
  $ kubeadm token create
  ```

- 获取sha256密钥

  ```bash
  $ openssl x509 -pubkey -in /etc/kubernetes/pki/ca.crt | openssl rsa -pubin -outform der 2>/dev/null | openssl dgst -sha256 -hex | sed  's/^ .* //'
  ```

- worker节点加入集群

  ```bash
  $ kubeadm join <master节点地址>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<密钥>
  ```

- 在master节点中，检验节点加入

  ```bash
  $ kubectl get nodes
  NAME           STATUS   ROLES                  AGE   VERSION
  name           Ready    <none>                 44h   v1.22.0
  node           Ready    control-plane,master   44h   v1.22.0
  ```

- 看到节点状态均为ready说明集群部署完成。

### 5. 创建示例pod

- 新建一个空目录，命名为work。

- 将示例程序（项目目录下的stream-pivot）打包，然后把jar包和配置文件rate.properties放入目录中。

- 在目录中创建Dockerfile，填写内容如下。

  ```dockerfile
  FROM java
  
  ENV GC_STRATEGY="-XX:+UseParallelGC -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/workspace/logs/heapdump.hprof -Xss2048k -Xmx6G -Xms6G"
  
  RUN mkdir -p /workspace
  ADD stream-pivot-1.0-SNAPSHOT.jar /workspace/
  ADD rate.properties /workspace/
  
  WORKDIR /workspace
  CMD ["java", "-jar", "stream-pivot-1.0-SNAPSHOT.jar"]
  ```

- 将该目录上传至worker节点，然后在节点中执行如下命令。其中collect:1.0为镜像名和版本号。**该镜像只会在本机生效，其他节点无法识别该镜像，除非在所有worker节点中均执行该命令或自行维护镜像仓库。**

  ```bash
  $ docker build -t collect:1.0 work/
  ```

- 编写pod配置文件test.yaml，配置方式见：[k8s容器采集接入手册](./k8s容器采集接入手册)

- 启动该pod。

  ```bash
  $ kubectl apply -f test.yaml
  ```

- 查看该pod状态，看到Status: Running说明该pod运行正常。

  ```bash
  $ kubectl describe pod test-collect
  ```

### 6. 集群接入Agent-Manager

- 在master节点中，找到~/.kube/config文件，复制一份到agent-manager所在jar包的同级目录。

- 修改application.yml文件，修改内容如下：

  ```yaml
  metadata:
    sync:
      request:
        k8s:
          service-key: servicename
          path-name: logpath
          enabled: true
  ```

- 重启agent-manager。主机和服务的列表信息均已和k8s信息同步。

- 创建采集任务，关联应用选择容器关联的应用。采集路径设置为容器内路径。其他与主机采集时相同。

  

  

