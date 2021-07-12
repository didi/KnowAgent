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
```
|-- src
    |-- HeadlessIndex.tsx
    |-- app.tsx
    |-- index.html
    |-- index.less
    |-- index.tsx
    |-- @types
    |   |-- index.d.ts
    |-- actions
    |   |-- actionTypes.ts
    |   |-- agent.ts
    |   |-- collect.ts
    |   |-- echarts.ts
    |   |-- full-screen.ts
    |   |-- hola.ts
    |   |-- index.ts
    |   |-- modal.ts
    |   |-- permissionPoint.ts
    |   |-- resPermission.ts
    |-- api
    |   |-- agent.ts
    |   |-- agentVersion.ts
    |   |-- api.ts
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