# 1. 公有云环境下Agent-Manager无法消费Agent指标与错误日志

​	请检查 Agent-Manager 配置文件 application.yml 中配置的 Kafka ConsumerGroupId 配置项值（配置项：consumer.id） 在公有云 Kafka 服务是否开启使用，如未开启，需要开启。

# 2. Agent 没配置 errorLogs & metrics 数据接收端报错

​	报错信息：get agent config from remote agent-manager failed, ip=xxxxx,port=xxxxx,url=xxxxx,param=xxxxx, response unknown code=26000.

​	注意 code 26000 这个状态码，这个状态码表示没在 agent-manager 平台为 agent 配置 errorLogs & metrics 数据接收端，此时，须在 agent-manager 平台为 agent 配置 errorLogs & metrics 数据接收端信息。

