# Agent-Manager

## 1. 环境准备

- java 8+ （运行环境）
- MySQL 5.7 （数据存储）
- Maven 3.5+ （后端打包依赖）
- node 14.15.1+ （用于打包依赖，其中npm版本6.14.8）

## 2. 源代码编译打包

下载好源代码后，执行如下两个步骤：

1. 进入`system-metrcis`项目主目录，执行mvn clean install -DskipTests命令。

2. 进入`agent-manager`主目录，执行`sh build.sh`命令，得到output目录。

## 3. MySQL-DB初始化

执行[create_mysql_table.sql](../agent-manager/create_mysql_table.sql)中的SQL命令，从而创建所需的MySQL库及表，默认创建的库名是`logi_agent_manager`。

```
# 示例：
mysql -uXXXX -pXXX -h XXX.XXX.XXX.XXX -PXXXX < ./create_mysql_table.sql
```

## 4. 配置文件修改

```
# application.yml 是配置文件，最简单的是仅修改MySQL相关的配置即可启动
spring.datasource.druid.url：数据库连接信息，将源连接ip、端口修改为对应数据库连接信息
spring.datasource.druid.username：数据库用户名
spring.datasource.druid.password：数据库用密码
auv-job.jdbc-url：同配置项 spring.datasource.druid.url
auv-job.username：同配置项 spring.datasource.druid.username
auv-job.password：同配置项 spring.datasource.druid.password
```

## 5. 启动与停止

在`output`目录下，执行`sh start.sh`即可启动Agent-Manager服务，执行`sh stop.sh`即可停止Agent-Manager服务。

## 6. 使用

本地启动的话，访问`http://localhost:8080`。更多参考：[agent-manager 用户使用手册](../doc/user_guide/user_guide_cn.md)



# Log-Agent

## 1. 环境准备

- java 8+ （运行环境）
- Maven 3.5+ （后端打包依赖）

## 2. 源代码编译打包

### Log-Agent

在编译打包Log-Agent项目前，请先对Agent-Manager项目进行编译打包（见：[Agent-Manager 安装部署手册.md](Agent-Manager%20安装部署手册.md)）。下载好Log-Agent项目源代码后，进入Log-Agent目录，执行`sh build.sh`命令，得到output目录。

## 3. 配置文件修改

```
# conf/settings.properties 是配置文件，最简单的是仅修改Agent-Manager平台的相关配置即可启动
config.ip=运行Agent-Manager服务的机器ip
config.port=Agent-Manager微服务配置的http端口（默认：8080）
```

## 4. 启动与停止

在`output`目录下，执行`sh start.sh`即可启动Agent服务，执行`sh stop.sh`即可停止Agent服务。

## 5. 使用

Agent启动成功后，需要在Agent-Manager平台上将安装Agent的宿主机作为新增主机项维护进去，Agent将自动在Agent-Manager平台进行注册，待Agent注册成功后，请编辑并配置好该Agent对应的 Metrics & Errorlogs 流对应的接收端配置，然后即可在Agent-Manager平台管理对刚刚安装的Agent进行管理与采集任务下发。

