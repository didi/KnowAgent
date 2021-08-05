export const AGENT_CONFIG = [
  {
      "metricPanelGroupName": "全选",
      "metricPanelList": [
          {
              "panelName": "CPU使用率",
              "title": 'CPU使用率 (%)',
              "metricList": [
                  {
                      "metricName": "CPU使用率 (%)",
                      "metricPointList": []
                  }
              ],
              'unit': '%',
              "api": 'cpuUsage',
              "selfHide": false
          },
          {
              "panelName": "内存使用量",
              "title": '内存使用量 (MB)',
              "metricList": [
                  {
                      "metricName": "内存使用量 (MB)",
                      "metricPointList": []
                  }
              ],
              "api": 'memoryUsage',
              "selfHide": false
          },
          {
              "panelName": "fd使用量",
              "title": 'fd使用量 (条)',
              "metricList": [
                  {
                      "metricName": "fd使用量 (条)",
                      "metricPointList": []
                  }
              ],
              'unit': '条',
              "api": 'fdUsage',
              "selfHide": false
          },
          {
              "panelName": "full gc次数",
              "title": 'full gc次数（次/min）',
              "metricList": [
                  {
                      "metricName": "full gc次数（次/min）",
                      "metricPointList": []
                  }
              ],
              'unit': '次/min',
              "api": 'fullGcTimes',
              "selfHide": false
          },
          {
              "panelName": "出口发送流量",
              "title": '出口发送流量 （MB/min）',
              "metricList": [
                  {
                      "metricName": "出口发送流量 （MB/min）",
                      "metricPointList": []
                  }
              ],
              "api": 'exitSendTraffic',
              "selfHide": false
          },
          {
              "panelName": "出口发送条数",
              "title": '出口发送条数 （条/min）',
              "metricList": [
                  {
                      "metricName": "出口发送条数 （条/min）",
                      "metricPointList": []
                  }
              ],
              'unit': '条/min',
              "api": 'exitSendBar',
              "selfHide": false
          },
          {
              "panelName": "入口采集流量",
              "title": '入口采集流量 （MB/min）',
              "metricList": [
                  {
                      "metricName": "入口采集流量 （MB/min）",
                      "metricPointList": []
                  }
              ],
              "api": 'inletCollectTraffic',
              "selfHide": false
          },
          {
              "panelName": "入口采集条数",
              "title": '入口采集条数（条/min）',
              "metricList": [
                  {
                      "metricName": "入口采集条数（条/min）",
                      "metricPointList": []
                  }
              ],
              'unit': '条/min',
              "api": 'inletCoLLectBar',
              "selfHide": false
          },
          {
              "panelName": "错误日志输出条数",
              "title": '错误日志输出条数（条/min）',
              "metricList": [
                  {
                      "metricName": "错误日志输出条数（条/min）",
                      "metricPointList": []
                  }
              ],
              'unit': '条/min',
              "api": 'logFaultOutPutBar',
              "selfHide": false
          },
          {
              "panelName": "日志采集任务数占比",
              "title": '日志采集任务数占比',
              "metricList": [
                  {
                      "metricName": "日志采集任务数占比",
                      "metricPointList": []
                  }
              ],
              "api": 'logCollectTask',
              "isPie": true,
              "selfHide": false
          },
          {
              "panelName": "日志采集路径数占比",
              "title": '日志采集路径数占比',
              "metricList": [
                  {
                      "metricName": "日志采集路径数占比",
                      "metricPointList": []
                  }
              ],
              "api": 'logCollectPath',
              "isPie": true,
              "selfHide": false
          },
      ],
      "groupHide": false
  }
]