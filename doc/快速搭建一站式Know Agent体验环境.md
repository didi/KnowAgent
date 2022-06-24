# 1 安装环境准备

- 操作系统：Linux CentOS 7+ 
- 环境：
  - 具备 root 权限
  - yum 命令可用
- 物理资源：
  - CPU：4 Cores+
  - Memory：8 GB+
  - Disk：100GB+

# 2. 安装部署

​	如您准备的安装环境具备外网连接能力，且具备稳定的网络环境与良好的网速，建议您采用`在线安装`模式，否则，建议您采用`离线安装`模式。

## 2.1 在线安装

### 	2.1.1 创建安装目录	

​		在需要安装 Know Agent 一站式体验环境的主机上创建用于存放 Know Agent 一站式体验环境所需的各组件安装包的目录，如下图：

![image-20220624155844938](assets/创建know agent一站式体验环境安装目录.png)				

### 	2.2.2 拷贝安装脚本至安装目录

​		请将[《Know Agent一站式体验环境一键安装脚本》](know_agent_experience_environment_auto_install_script.sh)拷贝至上一步创建的安装目录中，如下图：

![image-20220624160111592](assets/拷贝Know Agent一站式体验环境一键安装脚本.png)

![image-20220624160304804](assets/Know Agent一站式体验环境一键安装脚本copy成功.png)

### 	2.2.3 执行安装脚本

​		执行`sh know_agent_experience_environment_auto_install_script.sh`命令运行 Know Agent 一站式体验环境一键安装脚本，开始 Know Agent 一站式体验环境的安装、配置。遇到 “Do you need to install MySQL（yes/no）”，请输入 “yes”，如下图：

![image-20220624160908157](assets/Know Agent一站式体验环境一键安装脚本开始执行.png)		Know Agent 一站式体验环境安装成功，如下图：

![image-20220624161951303](assets/Know Agent一站式体验环境安装成功.png)

## 2.2 离线安装



# 3. 开始体验

## 3.1 创建 Agent 的指标流、错误日志流对应的 Kafka Topic

​	进入 Know Agent 一站式体验环境安装目录，在 `Kafka安装目录/bin`目录下，先执行`source ~/.bashrc`命令，然后执行`sh kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic 请替换为topic名`命令，创建 Agent 的指标流、错误日志流对应的 Kafka Topic，如下图：

![image-20220624170025875](assets/Know Agent一站式体验环境 Agent Metrics、Error Logs 流 kafka topic 创建.png)

## 3.2 在 Agent-Manager 配置 Agent 的指标流、错误日志流对应的接收端

​	打开浏览器，输入地址：http://请替换为KnowAgent一站式体验环境安装主机ip:9010，进入 Agent-Manager 管理平台。在

