# 开发环境搭建

## Agent Manager 本地开发环境搭建

### 1 环境准备

#### 1.1 环境依赖

- `Java 8+`
- `Maven 3.5+`
- `Node 14.15.1 (前端环境)`
- `Mysql 5.7+ (数据存储)`
- `kafka 2.3+`
- `IDEA`

#### 1.2 数据库初始化

​	执行`agent-manager`目录中的`create_mysql_table.sql`这个sql文件，初始化mysql表。

```
# 示例：
mysql -uXXXX -pXXX -h XXX.XXX.XXX.XXX -PXXXX < ./create_mysql_table.sql
```

### 2 本地启动

​	因为本地直接使用`IDEA`启动，并不会将前端资源文件生成，因此在第一次启动之前，需要在`agent-manager`目录下执行`mvn clean install -DskipTests`命令，将前端的静态资源文件打包出来。命令执行完成以后，修改`application.yml`配置文件，然后点击启动，本地启动成功之后，访问 http://localhost:9010。具体的`IDEA`启动及配置见图：

#### **2.1 IDEA 打包**

![agent-manager IDEA 打包](assets/agent-manager打包.png)

#### **2.2 修改 Agent-Manager 配置文件**

![agent-manager配置文件修改](assets/agent-manager%20配置.png)

#### **2.3 启动 Agent-Manager**

![agent-manager 启动](assets/agent-manager%20启动.png)

#### **2.4 Agent-Manager 浏览器访问**

![agent-manager 浏览器访问](assets/agent-manager%20浏览器访问.png)

### 3 Agent-Manager 简要介绍

#### 3.1 整体架构

![agent-manager整体架构](assets/agent-manager%20整体架构.png)

#### 3.2 模块介绍

| 模块                      | 说明                        | 详细说明                                                     |
| ------------------------- | --------------------------- | ------------------------------------------------------------ |
| agent-manager-console     | 前端模块                    | Agent-Manager前端相关的代码                                  |
| agent-manager-common      | 公共模块                    | 存放公共内容，包括POJO类、常量/枚举类、工具类等              |
| agent-manager-core        | 核心模块                    | Agent-Manager的核心模块，存放最基本及最重要的功能，比如采集任务的增删改查等 |
| agent-manager-persistence | DAO 模块                    | 主要是操作MySQL、ElasticSearch的相关类                       |
| agent-manager-extends     | 扩展模块                    | 非核心模块，可根据实际需要进行扩展                           |
| agent-manager-thirdpart   | 扩展模块-默认第三方扩展模块 | 如元数据（主机、容器、Pod、服务）的获取，默认基于标准k8s，也可以对接自己公司内部的容器管理平台 |
| agent-manager-remote      | 远程访问模块                | 用于与其他系统进行交互，例如kafka-manager、对象存储等        |
| agent-manager-task        | 定时任务模块                | 基于MySQL通过抢占方式实现的一个定时任务的负载均衡以及定时任务的执行模块，用于执行既有定时任务，比如日志采集任务、Agent的健康度巡检 |
| agent-manager-rest        | web 模块                    | 接受外部 Rest Http 请求及进行相关控制                        |

## Agent 本地开发环境搭建

### 1 环境准备

#### 1.1 环境依赖

- `Java 8+`
- `Maven 3.5+`
- `kafka 2.3+`
- `IDEA`

**注意：在搭建 Agent 本地开发环境前，请先对 `System-Metrics`项目与 `Agent-Manager` 项目进行 Maven 打包、安装。**

### 2 本地启动

​	修改`settings.properties`配置文件，将`agent-node`项目中的`com.didichuxing.datachannel.agent.node.Agent`类作为启动类运行即可，如下图：

​	

本地启动成功之后，访问http://localhost:8080。

具体的`IDEA`启动及配置见图：

## 3 Log-Agent 简要介绍

### 3.1 整体架构

