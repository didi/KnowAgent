# Agent-Manager 安装部署手册

## 1. 环境准备

- java 8+ （运行环境）
- MySQL 5.7 （数据存储）
- Maven 3.5+ （后端打包依赖）
- node 14.15.1 （用于打包依赖，其中npm版本6.14.8）

## 2. 源代码编译打包

### Agent-Manager

下载好源代码后，进入`Agent-Manager`的主目录，执行`sh build.sh`命令，得到output目录。

## 3. MySQL-DB初始化

执行[create_mysql_table.sql](../agent-manager-v2/create_mysql_table.sql)中的SQL命令，从而创建所需的MySQL库及表，默认创建的库名是`logi_agent_manager`。

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

本地启动的话，访问`http://localhost:8080`。
