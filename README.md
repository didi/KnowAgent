项目概述
Logi-Agent是一款在混合云、容器化时代背景下，创新性提出以“服务/应用”为采集粒度的日志采集平台。并提供健全的指标诊断体系，保障采集安全易用、数据完整准确。

功能支持
Logi-Agent 以服务为单位，进行日志采集行为的配置、治理，功能上支持：

主机/容器管理：用于维护主机、容器信息至系统（含：容器-主机关系）
服务管理：用于维护服务信息至系统（含：服务-主机、服务-容器关系）
日志接收端管理：用于维护Agent上报的数据流、指标流、错误日志流需要写入的下游接收端信息（目前接收端类型仅支持kafka）
Agent管理：用于维护Agent信息（含：Agent指标流、错误日志流对应下游的接收端信息、Agent 限流信息等），Agent被部署、启动后，会自动向管理平台进行注册，注册成功后，即可进行对应管理
日志采集任务管理：用于面向服务配置采集任务，配置好的采集任务通过服务找到部署该服务的主机/容器集，针对采集这些主机、容器的Agent集，进行日志采集任务下发
Agent 指标查看：用于查看Agent运行时全景指标
日志采集任务指标查看：用于查看日志采集任务在各Agent上运行时全景指标
Agent健康度巡检：基于Agent运行时指标对Agent是否运行健康进行检查
日志采集任务健康度巡检：基于日志采集任务在各Agent运行时指标对日志采集任务是否运行健康进行检查
安装部署
管理平台侧

解压缩安装包logi-agent.tar.gz，在MySQL终端运行agent-manager目录下ddl.sql文件中的内容，创建管理平台所需数据库表。
修改配置文件application.yml： 
spring.datasource.druid.url：数据库连接信息，将源连接ip、端口修改为对应数据库连接信息
spring.datasource.druid.username：数据库用户名
spring.datasource.druid.password：数据库用密码
auv-job.jdbc-url：同配置项 spring.datasource.druid.url
auv-job.username：同配置项 spring.datasource.druid.username
auv-job.password：同配置项 spring.datasource.druid.password
启动管理平台：sh start.sh
停止管理平台：sh stop.sh
agent 侧

解压缩安装包 logi-agent.tar.gz，在agent/conf/目录下，修改settings.properties文件：
config.ip：管理平台部署的机器ip地址

config.port：管理平台进程对应端口，见管理平台配置文件 agent-manager/application.yml 配置项 agentmanager.port.web 对应值（默认：8006）

启动 agent：进入agent/bin/目录下，执行 sh start.sh 启动 agent
停止 agent：进入agent/bin/目录下，执行 sh stop.sh 停止 agent
开发指南
下载好源码后，进入Logi-AgentManager的主目录，有 7 个子项目：

agent-manager-common：用于存放POJO类、常量类、枚举类、异常类、各工具类
agent-manager-core：用于存放核心的业务逻辑接口定义及其对应实现类，按模块分包
agent-manager-persistence：用于存放持久化相关接口定义类、字段映射配置文件
agent-manager-extends/agent-manager-thirdpart：用于存放扩展接口定义及对应默认实现类
agent-manager-remote：用于存放远程外部接口定义及默认实现类
agent-manager-rest：用于存放rest接口实现类
agent-manager-task：用于存放定时任务实现类
启动类 AgentManagerApplication 位于项目 agent-manager-rest 中，配置文件 application.yml 位于项目 agent-manager-rest 的 resources 目录下。



滴滴Logi-Agent架构图
滴滴云事业部-商业数据产品 > readme > image2021-6-13_22-7-53.png



