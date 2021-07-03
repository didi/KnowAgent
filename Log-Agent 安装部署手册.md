# Log-Agent 安装部署手册

## 1. 环境准备

- java 8+ （运行环境）
- Maven 3.5+ （后端打包依赖）

## 2. 源代码编译打包

### Log-Agent

下载好源代码后，进入Log-Agent目录，执行`sh build.sh`命令，得到output目录。

## 3. 配置文件修改

```
# conf/settings.properties 是配置文件，最简单的是仅修改Agent-Manager平台的相关配置即可启动
config.ip=运行Agent-Manager服务的机器ip
config.port=Agent-Manager微服务配置的http端口（默认：8080）
```

## 4. 启动与停止

在`bin`目录下，执行`sh start.sh`即可启动Agent服务，执行`sh stop.sh`即可停止Agent服务。

## 5. 使用

Agent启动成功后，需要在Agent-Manager平台上将安装Agent的宿主机作为新增主机项维护进去，Agent将自动在Agent-Manager平台进行注册，然后即可在Agent-Manager平台管理对刚刚安装的Agent进行管理与采集任务下发。

