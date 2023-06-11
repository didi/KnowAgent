# 元数据管理

​	元数据包括：接收端、主机、应用。

## 接收端

### 前置说明

​	在您配置接收端信息前，请您安装好 Kafka，确保 Kafka 与 Agent-Manager、Agent 所在主机的网络连通性，并创建用于存放 Agent 数据流、指标流、错误日志流数据的 Topic。

### 新增接收端

​	接收端表示 Agent 数据流、指标流、错误日志流对应数据流向的下游组件，目前 Agent-Manager 仅支持 Kafka 类型接收端。当您进入Agent-Manager管理平台以后，点击左侧菜单栏中`元数据中心 - 接受端管理`，如下图：

<img src="./assets/user-manual/1.png" />

​	初始状态下，未配置任何接收端信息，因此请您先统一配置 Agent 数据流、指标流、错误日志流数据对应下游接收端信息。点击右上方的`新增集群`按钮，进入新增集群页，如下图：

<img src="./assets/user-manual/2.png" />

<img src="./assets/user-manual/3.png" />

- `集群名`输入框：请填入您安装好的Kafka集群名。
- `集群地址`输入框：请填入您安装好的Kafka集群对应broker地址列表（如：10.255.0.49:9092，多个 broker 地址间采用逗号分隔）。
- `生产端初始化属性`输入框：具有默认值，您也可以根据您的特殊场景需求进行调整（如：序列化方式、压缩方式、安全等）。

​	如您需要将该 Kafka 集群作为 Agent 指标流、错误日志流数据的下游接收端，请您勾选`设置为默认指标流接受集群`与`设置为默认错误日志流接受集群`复选框，并在`指标流接收Topic`与`错误日志流接收Topic`输入框中，选择您为存储 Agent 指标流、错误日志流数据创建好的 Topic，如下图：

<img src="./assets/user-manual/4.png" />

​	点击`确定`按钮，接收端新增成功，在接收端管理页可看到刚刚新增成功的接收端记录，Agent 接入以后将自动采用该接收端信息作为其指标流、错误日志流数据的下游接收端，如下图：

<img src="./assets/user-manual/5.png" />

### 查询接收端

​	在接收端管理页可根据`集群名、集群地址、新增时间`对系统已有接收端信息进行查询，`集群名、集群地址`支持模糊查询，如下图：

<img src="./assets/user-manual/6.png" />

### 修改接收端

​	在接收端管理页下方表格中针对需要修改的接收端记录，在其右侧操作列点击对应`修改`按钮，即可对其信息进行修改，如下图：

<img src="./assets/user-manual/7.png" />

<img src="./assets/user-manual/8.png" />

### 删除接收端

​	在接收端管理页下方表格中针对需要删除的接收端记录，在其右侧操作列点击对应`删除`按钮，即可对其进行删除，如下图：

<img src="./assets/user-manual/9.png" />

​	如需要删除多条接收端记录，在接收端管理页下方表格中针对需要删除的接收端记录，在其左侧复选框进行勾选，勾选完毕以后点击页面右上方的`删除`按钮进行批量删除，如下图：	<img src="./assets/user-manual/10.png" />

## 主机

​	主机信息可通过如下三种方式进行配置：

#### 页面手动配置

##### 新增主机

​	进入 `Agent中心 - Agent管理`页，点击页面右上侧`新增主机`按钮，进入新增主机页，如下图：

<img src="./assets/user-manual/11.png" />

- `主机类型`下拉列表框：请选择主机类型，目前仅支持物理机。
- `主机名`输入框：请填入需要新增主机的主机名。
- `主机IP`输入框：请填入需要新增主机的主机 IP。

​	在您填写完上述信息以后，可点击`连接测试`按钮测试您填入主机信息的连通性，如下图：

<img src="./assets/user-manual/12.png" />

​	点击`确定`按钮，主机新增成功，在Agent管理页可看到刚刚新增成功的主机记录，如下图：

<img src="./assets/user-manual/13.png" />

##### 删除主机

​	在Agent管理页下方表格中针对需要删除的主机记录，在其右侧操作列点击对应`删除`按钮，即可对其进行删除，如下图：

<img src="./assets/user-manual/14.png" />

​	需要注意的是，如待删除主机有关联的 Agent，请先停止主机关联的 Agent 进程，主机及其关联的 Agent 将被一并删除。

##### 查询主机

​	在Agent管理页可根据`主机名、主机 IP、主机类型、新增时间`对系统已有主机信息进行查询，`主机名、主机 IP`支持模糊查询，如下图：

<img src="./assets/user-manual/15.png" />

#### Agent 注册时自动导入

​	在 Agent 启动以后，会携带其宿主机信息向配置的 Agent-Manager 管理平台注册，注册时，如其宿主机信息在 Agent-Manager 平台不存在，将自动添加 Agent 宿主机信息，如下图：

<img src="./assets/user-manual/16.png" />

#### Excel 导入

##### 前置说明

​	如选择以 Excel 导入方式进行主机与应用数据维护，意味着系统内目前所有的主机与应用数据会以 Excel 中的数据为准，不论 Agent 注册时自动新增的主机数据或手动维护的主机、应用数据，全部更新为 Excel 中的数据。

##### Excel 导入流程

​	进入`元数据中心 - 元数据管理`页，点击页面右上侧`上传元数据`按钮，进入元数据上传页，如下图：

<img src="./assets/user-manual/17.png" />

​	点击`元数据Excel模板文件下载`链接，下载并打开元数据Excel模板文件，如下图：

<img src="./assets/user-manual/18.png" />

​	在 host sheet 与 application sheet 填入需要导入的主机、应用信息，并保存 Excel 如下图：

<img src="./assets/user-manual/19.png" />

<img src="./assets/user-manual/20.png" />

​	在元数据上传页，点击`上传`按钮，选择刚刚填写完保存的 Excel 文件，并填入描述信息，点击确定按钮，元数据文件即被上传成功，如下图：

<img src="./assets/user-manual/21.png" />

<img src="./assets/user-manual/22.png" />

​	在元数据管理页，在上一步上传成功的元数据文件对应记录的操作列，点击`预览`按钮，即可预览元数据文件中的内容，并校验是否与预期一致，如下图：

<img src="./assets/user-manual/23.png" />

<img src="./assets/user-manual/24.png" />

​	在元数据管理页，在上一步上传成功的元数据文件对应记录的操作列，点击`导入元数据`按钮，即可将元数据文件中的内容导入系统，如下图：

<img src="./assets/user-manual/25.png" />

<img src="./assets/user-manual/26.png" />

<img src="./assets/user-manual/27.png" />

<img src="./assets/user-manual/28.png" />

## 应用

​	应用信息可通过如下两种方式进行配置：

### 页面手动配置

#### 新增应用

​	进入 `元数据中心 - 应用管理`页，点击页面右上侧`新增应用`按钮，进入新增应用页，如下图：

<img src="./assets/user-manual/29.png" />

- `应用名`输入框：请填入需要新增应用的应用名。
- `关联主机`穿梭框：请选择应用关联的主机名集。

​	在您填写完上述信息以后，点击`确定`按钮，应用新增成功，在应用管理页可看到刚刚新增成功的应用记录，如下图：

<img src="./assets/user-manual/30.png" />

<img src="./assets/user-manual/31.png" />

#### 修改应用

​	在应用管理页下方表格中针对需要修改的应用记录，在其右侧操作列点击对应`修改`按钮，即可对其信息进行修改，如下图：	<img src="./assets/user-manual/32.png" />

<img src="./assets/user-manual/33.png" />

​	这里，将之前关联的 2 个主机改为仅关联其中 1 个主机，如下图：

<img src="./assets/user-manual/34.png" />

<img src="./assets/user-manual/35.png" />

#### 删除应用

​	删除应用前，在修改应用页，解绑待删除应用与主机的关联关系，如下图：	<img src="./assets/user-manual/36.png" />

<img src="./assets/user-manual/37.png" />

​	在应用管理页下方表格中针对需要删除的应用记录，在其右侧操作列点击对应`删除`按钮，即可对其进行删除，如下图：

<img src="./assets/user-manual/38.png" />

#### 查询应用

​	在应用管理页可根据`应用名、新增时间`对系统已有应用信息进行查询，`应用名`支持模糊查询，如下图：

<img src="./assets/user-manual/39.png" />

### Excel 导入

​	同上述`主机 - Excel 导入`部分。

# Agent 管理

## 修改 Agent 配置

​	启动 Agent 进程以后，Agent 会自动接入 Agent-Manager，在 Agent管理页可以看到接入进来的 Agent 列表，如下图：

<img src="./assets/user-manual/40.png" />

​	在Agent管理页下方表格中针对需要修改Agent配置的Agent记录，在其右侧操作列点击对应`修改`按钮，即可对其信息进行修改，如下图：

<img src="./assets/user-manual/41.png" />

<img src="./assets/user-manual/42.png" />

​	配置信息栏采用默认值，可满足大多数场景，如用户有高阶需求，可通过该配置对原有默认配置进行修改，具体配置方法可咨询（微信：WilliamHu66）。

## 删除 Agent

​	在删除 Agent 前，请先停止待删除 Agent 进程，待 Agent 进程停止以后，在Agent管理页下方表格中针对需要删除的Agent记录，在其右侧操作列点击对应`删除`按钮，即可对其进行删除，如下图：

<img src="./assets/user-manual/43.png" />

## 查询 Agent	

​	在 Agent 管理页可根据`主机名、IP、Agent版本号、健康度、承载应用、主机类型、新增时间`对系统已有 Agent 信息进行查询，`主机名、IP`支持模糊查询，如下图：

<img src="./assets/user-manual/44.png" />

## 查看 Agent 健康度信息

​	Agent 健康度分三种类型：

​	红：Agent 故障，对数据完整性存在影响。

​	黄：Agent 存在风险，暂对数据完整性无影响。

​	绿：Agent 健康，无须关注。

​	在 Agent 管理页下方表格的 Agent 健康度列，将鼠标移至对应图标上方，即可看到对应健康度描述信息，如下图：	<img src="./assets/user-manual/45.png" />

​	也可在详情页，查看 Agent 健康度描述信息，如下图：

<img src="./assets/user-manual/46.png" />

<img src="./assets/user-manual/47.png" />

## 查看 Agent 运行时黄金指标

​	在Agent管理页下方表格中针对需要查看黄金指标的Agent记录，在其右侧操作列点击对应`指标`按钮，如下图：

<img src="./assets/user-manual/48.png" />

<img src="./assets/user-manual/49.png" />

​	如须查看更多 Agent 指标，可点击左下方`前往 Agent 指标看板`按钮。

# 采集任务管理

## 新增采集任务

进入 `采集任务管理`页，点击页面右上侧`新增任务`按钮，进入新增主机页，如下图：

<img src="./assets/user-manual/50.png" />

<img src="./assets/user-manual/51.png" />

### 采集对象配置

​	请填入采集任务名，并选择需要采集的应用，如下图：

<img src="./assets/user-manual/52.png" />

### 采集路径配置

​	请填入待采集的日志文件路径，多个日志文件路径可通过点击右侧的➕按钮进行添加，如下图：

<img src="./assets/user-manual/53.png" />

​	请选填采集文件后缀名匹配正则，该配置项用于确定在待采集日志文件路径对应同级目录下，需要关联哪些文件采集，如：日志文件路径配置为“/root/ka/lp/work/error/output.log”，采集文件后缀名匹配正则配置为“.\d”，此时在/root/ka/lp/work/error/目录下所有output.log.数字的文件都将被采集，并与/root/ka/lp/work/error/output.log作为同一个文件组。如下图：

<img src="./assets/user-manual/54.png" />

​	点击`路径预览`按钮，在路径预览页检查上述日志文件路径与采集文件后缀名匹配正则配置是否正确，如下图：

<img src="./assets/user-manual/55.png" />

<img src="./assets/user-manual/56.png" />

​	路径预览页中映射主机下拉列表框中的主机名集为第一步采集任务选择的的应用所关联的主机名集。

### 切片规则配置

​	点击`加载远程日志`按钮，将日志加载进原始日志文本框中，如下图：

<img src="./assets/user-manual/57.png" />

<img src="./assets/user-manual/58.png" />

<img src="./assets/user-manual/59.png" />

​	根据`切片配置划取方法说明`，在原始日志文本框中对原始日志日期/时间格式字符串进行划取，日志切片配置将自动填入，如下图：

<img src="./assets/user-manual/60.png" />

​		如日志切片配置无法被程序自动识别并填入，可点击`切片规则配置样例`链接，参照切片规则配置样例页面中的配置方法对切片规则配置进行手动配置，如下图：

<img src="./assets/user-manual/61.png" />

<img src="./assets/user-manual/62.png" />

​	日志切片配置完成以后，点击`日志切片结果预览`链接，可查看原始日志文本框内原始日志的切片结果，如下图：

<img src="./assets/user-manual/63.png" />

### 接收端配置与监控

- `Kafka集群`下拉列表框：请选择采集任务采集的日志需要传输到的下游接收端集群名。
- `生产端属性`输入框：具有默认值，您也可以根据您的特殊场景需求进行调整（如：序列化方式、压缩方式、安全等）。
- `Topic`选填框：请选择采集任务采集的日志需要传输到的下游接收端 Topic 名。
- 采集延迟监控配置项：默认打开，打开后，Agent-Manager 将根据配置的采集延迟阈值对采集任务进行自动采集延迟检测，采集延迟阈值默认值10分钟，可关闭，关闭后，Agent-Manager 将不会对采集任务进行自动采集延迟检测。
- 任务保障等级配置项：共 3 个配置项`高、中、低`，默认选项为`中`，该配置项用于运行该采集任务的 Agent 触发限流情况时，将根据保障等级高、中、低依次分配流量。
- `高级配置`输入框：高级配置采用默认值，可满足大多数场景，如用户有高阶需求，可通过该配置对原有默认配置进行修改，具体配置方法可咨询（微信：WilliamHu66）。

<img src="./assets/user-manual/64.png" />

​	点击`完成`按钮，采集任务添加成功，如下图：

<img src="./assets/user-manual/65.png" />

<img src="./assets/user-manual/66.png" />

## 修改采集任务

​	在采集任务管理页下方表格中针对需要修改的采集任务记录，在其右侧操作列点击对应`修改`按钮，即可对其信息进行修改，如下图：

<img src="./assets/user-manual/67.png" />

<img src="./assets/user-manual/68.png" />

## 删除采集任务

​	在采集任务管理页下方表格中针对需要删除的采集任务记录，在其右侧操作列点击对应`删除`按钮，即可对其进行删除，如下图：

<img src="./assets/user-manual/69.png" />

## 暂停/继续采集任务

​	在采集任务管理页下方表格中针对需要暂停/继续的采集任务记录，在其右侧操作列点击对应`暂停/继续`按钮，即可对其进行暂停/继续，如下图：

<img src="./assets/user-manual/70.png" />

<img src="./assets/user-manual/71.png" />

## 查询采集任务	

​	在采集任务管理页可根据`采集任务名、采集应用、采集任务状态、健康度、创建时间`对系统已有采集任务信息进行查询，`采集任务名`支持模糊查询，如下图：

<img src="./assets/user-manual/72.png" />

## 查看采集任务健康度信息

​	采集任务健康度分三种类型：

​	红：采集任务故障，对数据完整性存在影响。

​	黄：采集任务存在风险，暂对数据完整性无影响。

​	绿：采集任务健康，无须关注。

​	在采集任务管理页下方表格的健康度列，将鼠标移至对应图标上方，即可看到对应健康度描述信息，如下图：

<img src="./assets/user-manual/73.png" />

​	也可在详情页，查看采集任务健康度描述信息，如下图：

<img src="./assets/user-manual/74.png" />

<img src="./assets/user-manual/75.png" />

## 修复采集任务故障

​	根据出现故障的采集任务健康度描述信息，进行问题修改，如下图：

<img src="./assets/user-manual/76.png" />

## 查看采集任务运行时黄金指标

​	在采集任务管理页下方表格中针对需要查看黄金指标的采集任务记录，在其右侧操作列点击对应`指标`按钮，如下图：

<img src="./assets/user-manual/77.png" />

<img src="./assets/user-manual/78.png" />

​	如须查看更多采集任务指标，可点击左下方`前往采集任务指标看板`按钮。

# 监控中心

## 采集任务指标看板

​	在这里，可以以指标形式透视采集任务运行时的状态，默认展示采集任务的核心指标，指标看板支持拖拽，如下图：

<img src="./assets/user-manual/79.png" />

​	通过`采集任务名`下拉列表框选择某个采集任务后，可通过`采集路径`、`主机名`下拉列表框选择某个采集路径或某个采集路径下的某个主机进行指标钻取查看，如下图：

<img src="./assets/user-manual/80.png" />

​	单个指标，可点击大图，查看该指标在各个时间点的各统计值（如存在），如下图：

<img src="./assets/user-manual/81.png" />

<img src="./assets/user-manual/82.png" />

​	如须查看更多采集任务指标，可点击指标筛选按钮，选择需要查看的指标，如下图：

<img src="./assets/user-manual/83.png" />

<img src="./assets/user-manual/84.png" />

## Agent 指标看板

​	在这里，可以以指标形式透视 Agent 运行时的状态，默认展示 Agent 的核心指标，指标看板支持拖拽，如下图：

<img src="./assets/user-manual/85.png" />

​	单个指标，可点击大图，查看该指标在各个时间点的各统计值（如存在），如下图：

<img src="./assets/user-manual/86.png" />

<img src="./assets/user-manual/87.png" />

<img src="./assets/user-manual/88.png" />

​	如须查看更多 Agent 指标，可点击指标筛选按钮，选择需要查看的指标，如下图：

<img src="./assets/user-manual/89.png" />

<img src="./assets/user-manual/90.png" />

# 我的工作台

## 运营大盘

​	展示运营相关的核心指标数据，如下图：

<img src="./assets/user-manual/91.png" />

## 运维大盘

​	展示运维相关的核心指标数据，如下图：

<img src="./assets/user-manual/92.png" />

​	在`各健康度类型采集任务分布`与`各健康度类型Agent分布`图中，针对故障任务、预警任务、故障 Agent、预警 Agent，可点击进入采集任务详情页与 Agent 详情页，查看采集任务健康度描述与 Agent 健康度描述信息，如下图：

<img src="./assets/user-manual/93.png" />

​	在 `Agent视角 TOP5`与`采集任务视角 TOP5`部分，针对柱状图对应采集任务名与 Agent 主机名与可点击进入对应采集任务指标看板 与 Agent 指标看板，如下图：

<img src="./assets/user-manual/94.png" />

<img src="./assets/user-manual/95.png" />

# 指标探查

​	用于复杂场景下运维排障场景，可针对 Agent 指标与采集任务指标进行关联分析，也可针对不同的 Agent 指标或采集任务指标进行对比分析，指标看板支持拖拽，如下图：

<img src="./assets/user-manual/96.png" />

<img src="./assets/user-manual/97.png" />

<img src="./assets/user-manual/98.png" />
