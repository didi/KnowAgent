​	Know Agent 基于最小依赖、便于体验，采用 MySQL 存储 Agent 的 Metrics 与 Error Logs 数据，受制于 MySQL 性能瓶颈，在单个采集任务对应一个采集路径、Metrics 与 Error Logs 保存周期一周的情况下，支持 50 个 Agent 与 50 个采集任务的管控。如需要管控更多的 Agent 与采集任务，用户可通过如下方式自行扩展、实现，以扩展 Elasticsearch 作为 Agent 指标数据与错误日志数据存储引擎为例：

1. 修改配置文件 `application.yml` 中关于 Agent 的 Metrics 与 Error Logs 数据存储类型配置项，如下图：

   ![application.yml storage 修改](/Users/didi/Desktop/workspace/KnowAgent/doc/assets/application.yml storage 修改.png)

2. 实现 `MetricsDAOFactory` 与 `ErrorLogsDAOFactory` 接口

   

3. 实现 `MetricsDAOFactory` 与 `ErrorLogsDAOFactory` 接口中需要创建的各 DAO 接口

   

4. 修改 `StorageFactoryBuilder` 类中的 `buildMetricsDAOFactory` 与 `buildErrorLogsDAOFactory` 方法逻辑

   