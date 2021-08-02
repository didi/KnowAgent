## 运行环境
`node 14.15.1 (node版本不低于12版本)`
`npm 6.4.8`

### 使用方法
1. 安装依赖
```
npm install
```
2. 进入项目，启动项目
```
npm start
```
3. 本地地址 
```
http://localhost:8000/agent
```
4. 打包
```
npm run build

修改打包配置
/config/webpack.dev.config.js 更改webpack配置
/fetk.config.js 更webpack一样，优先级没有webpack.dev.config.js中的配置高

```
### 本地开发代理配置
- fetk.config.js 文件配置
```
devServer: {
  proxy: {
    '/api/v1': {
      target: 'path',
      changeOrigin: true,
    }
  }
},
```

### 目录结构

1. 根目录文件

|根目录|含义|
|:-------:|:-----:|
|config|项目配置目录|
|fetk.config.js|同webpack配置相同|


2. src目录


|src|含义|
|:-------:|:-----:|
|pages|页面目录|
|api|接口相关|
|actions|redux相关|
|constants|配置常量，侧边栏menu|
|container|页面下各个模块|
|interface|TS接口定义目录|
|lib|封装方法目录|
|modal|封装Modal，Drawer的目录|
|reducers|redux相关|
|store|redux相关|
|packages|套件、基座|

3. src详细结构
```
|-- src
    |-- HeadlessIndex.tsx
    |-- app.tsx
    |-- index.html
    |-- index.less
    |-- index.tsx
    |-- @types  -- 类型
    |   |-- index.d.ts
    |-- actions  -- 更新store相关
    |   |-- actionTypes.ts  -- 更新store所有的type
    |   |-- agent.ts
    |   |-- collect.ts
    |   |-- echarts.ts
    |   |-- full-screen.ts
    |   |-- hola.ts
    |   |-- index.ts
    |   |-- modal.ts
    |   |-- permissionPoint.ts
    |   |-- resPermission.ts
    |-- api  -- 接口相关
    |   |-- agent.ts
    |   |-- agentVersion.ts
    |   |-- api.ts  -- 接口url配置
    |   |-- collect.ts
    |   |-- dataSource.ts
    |   |-- hola.ts
    |   |-- index.ts
    |   |-- operationRecord.ts
    |   |-- operationTasks.ts
    |   |-- receivingTerminal.ts
    |-- assets
    |   |-- favicon.ico
    |   |-- logo.png
    |-- component
    |   |-- CustomComponent.tsx
    |   |-- echarts
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- editor
    |   |   |-- index.less
    |   |   |-- monacoEditor.tsx
    |   |-- expand-card
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- time-panel
    |       |-- index.less
    |       |-- index.tsx
    |-- constants
    |   |-- common.ts
    |   |-- menu.ts
    |   |-- reg.ts
    |   |-- table.ts
    |   |-- time.ts
    |-- container
    |   |-- AllModalInOne.tsx
    |   |-- Hola.tsx
    |   |-- add-collect-task
    |   |   |-- CatalogPathList.tsx
    |   |   |-- ClientClearSelfMonitor.tsx
    |   |   |-- CollectLogConfiguration.tsx
    |   |   |-- CollectObjectConfiguration.tsx
    |   |   |-- LogFileType.tsx
    |   |   |-- LogRepeatForm.tsx
    |   |   |-- LoopAddLogFileType.tsx
    |   |   |-- ReceiverAdvancedConfiguration.tsx
    |   |   |-- config.tsx
    |   |   |-- dateRegAndGvar.ts
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- agent-management
    |   |   |-- Detail.tsx
    |   |   |-- List.tsx
    |   |   |-- OperationIndex.tsx
    |   |   |-- config.tsx
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- agent-version
    |   |   |-- List.tsx
    |   |   |-- config.tsx
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- collapse-select
    |   |   |-- CheckboxGroup.tsx
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- collect-task
    |   |   |-- AcquisitionConfiguration.tsx
    |   |   |-- AddAcquisitionTask.tsx
    |   |   |-- AssociateHost.tsx
    |   |   |-- Detail.tsx
    |   |   |-- List.tsx
    |   |   |-- config.tsx
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- common-curve
    |   |   |-- constants.ts
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- data-source
    |   |   |-- AppDetail.tsx
    |   |   |-- AppList.tsx
    |   |   |-- config.tsx
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- full-screen
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- operation-record
    |   |   |-- OperationRecordList.tsx
    |   |   |-- config.tsx
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- operation-tasks
    |   |   |-- List.tsx
    |   |   |-- TaskDetail.tsx
    |   |   |-- TaskTable.tsx
    |   |   |-- config.tsx
    |   |   |-- index.less
    |   |   |-- index.tsx
    |   |-- receiving-terminal
    |       |-- ClusterList.tsx
    |       |-- config.tsx
    |       |-- index.less
    |       |-- index.tsx
    |-- interface
    |   |-- agent.ts
    |   |-- agentVersion.ts
    |   |-- collect.ts
    |   |-- common.ts
    |   |-- dataSource.ts
    |   |-- operationRecord.ts
    |   |-- operationTask.ts
    |   |-- receivingTerminal.ts
    |-- lib
    |   |-- fetch.ts
    |   |-- url-parser.ts
    |   |-- utils.ts
    |-- locales
    |   |-- en.tsx
    |   |-- zh.tsx
    |-- modal
    |   |-- agent
    |   |   |-- AssociateHostDetail.tsx
    |   |   |-- DiagnosisReport.tsx
    |   |   |-- InstallHost.tsx
    |   |   |-- ModifyHost.tsx
    |   |   |-- NewHost.tsx
    |   |   |-- config.tsx
    |   |   |-- index.less
    |   |-- agentVersion
    |   |   |-- actionVersion.tsx
    |   |-- dataSource
    |   |   |-- actionApp.tsx
    |   |-- receivingTerminal
    |       |-- actionCluster.tsx
    |-- pages
    |   |-- agent.tsx
    |   |-- agentVersion.tsx
    |   |-- collect.tsx
    |   |-- common.tsx
    |   |-- dataSource.tsx
    |   |-- hola.tsx
    |   |-- operationRecord.tsx
    |   |-- operationTasks.tsx
    |   |-- receivingTerminal.tsx
    |-- reducers
    |   |-- agent.ts
    |   |-- collect.ts
    |   |-- echarts.ts
    |   |-- full-screen.ts
    |   |-- hola.ts
    |   |-- index.ts
    |   |-- modal.ts
    |   |-- permPoints.ts
    |   |-- resPermission.ts
    |   |-- tenantProject.ts
    |   |-- user.ts
    |-- store
        |-- index.ts
        |-- type.d.ts
```