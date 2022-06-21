​	Know Agent 基于最小依赖、便于体验，采用 MySQL 存储 Agent 的 Metrics 与 Error Logs 数据，受制于 MySQL 性能瓶颈，在单个采集任务对应一个采集路径、Metrics 与 Error Logs 保存周期一周的情况下，支持 50 个 Agent 与 50 个采集任务的管控。如需要管控更多的 Agent 与采集任务，用户可通过如下方式自行扩展、实现，以扩展 Elasticsearch 作为 Agent 指标数据与错误日志数据存储引擎为例：

1. 实现 `MetricsDAOFactory` 与 `ErrorLogsDAOFactory` 接口

   ![image-20220621182846348](assets/ElasticsearchMetricsDAOFactory.png)

   ![image-20220621182956095](assets/ElasticsearchErrorLogsDAOFactory.png)

2. 实现 `MetricsDAOFactory` 与 `ErrorLogsDAOFactory` 接口中需要创建的各 DAO 接口实现类

   

3. 修改 `StorageFactoryBuilder` 类中的 `buildMetricsDAOFactory` 与 `buildErrorLogsDAOFactory` 方法逻辑

   