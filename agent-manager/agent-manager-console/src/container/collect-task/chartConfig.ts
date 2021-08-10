export const COLLECT_CONFIG = [
  {
      "metricPanelGroupName": "Health",
      "metricPanelList": [
          {
              "panelName": "存在心跳主机数占比",
              "title": '存在心跳主机数占比',
              "metricList": [
                  {
                      "metricName": "存在心跳主机数占比",
                      "metricPointList": []
                  }
              ],
              "api": 'Health',
              "isPie": true,
              "isEachHost": false,
              "selfHide": false
          },
          {
              "panelName": "数据最大延迟",
              "title": '数据最大延迟 （s）',
              "metricList": [
                  {
                      "metricName": "数据最大延迟",
                      "metricPointList": []
                  }
              ],
              "api": 'HealthMaxDelay',
              "isPie": false,
              "isEachHost": false,
              "selfHide": false
          },
          {
            "panelName": "最小采集业务时间",
            "title": '最小采集业务时间',
            "metricList": [
                {
                    "metricName": "最小采集业务时间",
                    "metricPointList": []
                }
            ],
            // 'unit': 'min',
            "api": 'HealthMinCollectBusineTime',
            "isPie": false,
            "isEachHost": false,
            "selfHide": false
        },
          {
            "panelName": "限流时长",
            "title": '限流时长（s）',
            "metricList": [
                {
                    "metricName": "限流时长（ms）",
                    "metricPointList": []
                }
            ],
            "api": 'HealthLimitTime',
            "isPie": false,
            "isEachHost": false,
            "selfHide": false
        },
        {
          "panelName": "异常截断条数",
          "title": '异常截断条数（条）',
          "metricList": [
              {
                  "metricName": "异常截断条数（条）",
                  "metricPointList": []
              }
          ],
          'unit': '条',
          "api": 'HealthAbnormTrunca',
          "isPie": false,
          "isEachHost": false,
          "selfHide": false
        },
        {
          "panelName": "采集路径是否存在",
          "title": '采集路径是否存在',
          "metricList": [
              {
                  "metricName": "采集路径是否存在",
                  "metricPointList": []
              }
          ],
          // 'unit': '(s)',
          "api": 'isCollectPath',
          "isPie": false,
          "isEachHost": false,
          "selfHide": false
        },
        {
          "panelName": "采集路径是否存在乱序",
          "title": '采集路径是否存在乱序',
          "metricList": [
              {
                  "metricName": "采集路径是否存在乱序",
                  "metricPointList": []
              }
          ],
          // 'unit': '(s)',
          "api": 'isExistCollectPathChaos',
          "isPie": false,
          "isEachHost": false,
          "selfHide": false
        },
        {
          "panelName": "是否存在日志切片错误",
          "title": '是否存在日志切片错误',
          "metricList": [
              {
                  "metricName": "是否存在日志切片错误",
                  "metricPointList": []
              }
          ],
          // 'unit': '(s)',
          "api": 'islogChopFault',
          "isPie": false,
          "isEachHost": false,
          "selfHide": false
        },
      ],
      "groupHide": false
  },
  {
    "metricPanelGroupName": "Performance",
    "metricPanelList": [
        {
            "panelName": "日志读取字节数",
            "title": '日志读取字节数 （MB/分钟）',
            "metricList": [
                {
                    "metricName": "日志读取字节数 （MB/分钟）",
                    "metricPointList": []
                }
            ],
            "api": 'logReadBytes',
            "unit": '/分钟',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志读取条数",
            "title": '日志读取条数 （条）',
            "metricList": [
                {
                    "metricName": "日志读取条数 （条）",
                    "metricPointList": []
                }
            ],
            'unit': '条',
            "api": 'logReadBar',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "单logevent读取最小耗时",
            "title": '单logevent读取最小耗时 (ns)',
            "metricList": [
                {
                    "metricName": "单logevent读取最小耗时",
                    "metricPointList": []
                }
            ],
            "api": 'logeventMinConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "单logevent读取最大耗时",
            "title": '单logevent读取最大耗时 （ns）',
            "metricList": [
                {
                    "metricName": "单logevent读取最大耗时 （ns）",
                    "metricPointList": []
                }
            ],
            // 'unit': 'ns',
            "api": 'logEventMaxConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "单logevent读取平均耗时",
            "title": '单logevent读取平均耗时 （ns）',
            "metricList": [
                {
                    "metricName": "单logevent读取平均耗时 （ns）",
                    "metricPointList": []
                }
            ],
            // 'unit': 'ns',
            "api": 'logEventMeanConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志发送字节数",
            "title": '日志发送字节数（MB）',
            "metricList": [
                {
                    "metricName": "日志发送字节数（MB）",
                    "metricPointList": []
                }
            ],
            "api": 'logSendBytes',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志发送条数",
            "title": '日志发送条数 （条）',
            "metricList": [
                {
                    "metricName": "日志发送条数 （条）",
                    "metricPointList": []
                }
            ],
            'unit': '条',
            "api": 'logSnedBar',
            "isPie": false,
            "selfHide": false
        },
        // {
        //     "panelName": "日志发送耗时",
        //     "title": '日志发送耗时 （ms）',
        //     "metricList": [
        //         {
        //             "metricName": "日志发送耗时 （ms）",
        //             "metricPointList": []
        //         }
        //     ],
        //     "api": 'logSendConsuming',
        //     "isPie": false,
        //     "selfHide": false
        // },
        {
            "panelName": "日志发送最小耗时",
            "title": '日志发送最小耗时 （μs）',
            "metricList": [
                {
                    "metricName": "日志发送耗时 （ms）",
                    "metricPointList": []
                }
            ],
            "api": 'logSendMinConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志发送平均耗时",
            "title": '日志发送平均耗时 （μs）',
            "metricList": [
                {
                    "metricName": "日志发送耗时 （ms）",
                    "metricPointList": []
                }
            ],
            "api": 'logSendMeanConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志发送最大耗时",
            "title": '日志发送最大耗时 （μs）',
            "metricList": [
                {
                    "metricName": "日志发送耗时 （ms）",
                    "metricPointList": []
                }
            ],
            "api": 'logSendMaxConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志flush次数",
            "title": '日志flush次数（次）',
            "metricList": [
                {
                    "metricName": "日志flush次数（次）",
                    "metricPointList": []
                }
            ],
            'unit': '次',
            "api": 'logFlushTime',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志flush最大耗时",
            "title": '日志flush最大耗时 （ms）',
            "metricList": [
                {
                    "metricName": "日志flush最大耗时 （ms）",
                    "metricPointList": []
                }
            ],
            "api": 'logFlushMaxConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志flush平均耗时",
            "title": '日志flush平均耗时 （ms）',
            "metricList": [
                {
                    "metricName": "日志flush平均耗时 （s）",
                    "metricPointList": []
                }
            ],
            "api": 'logFlushMeanConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志flush最小耗时",
            "title": '日志flush最小耗时 （ms）',
            "metricList": [
                {
                    "metricName": "日志flush平均耗时 （s）",
                    "metricPointList": []
                }
            ],
            "api": 'logflushMinConsuming',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "日志flush失败次数",
            "title": '日志flush失败次数（次）',
            "metricList": [
                {
                    "metricName": "日志flush失败次数（次）",
                    "metricPointList": []
                }
            ],
            'unit': '次',
            "api": 'logFlushFailTimes',
            "isPie": false,
            "selfHide": false
        },
        {
            "panelName": "数据过滤条数",
            "title": '数据过滤条数 （条）',
            "metricList": [
                {
                    "metricName": "数据过滤条数 （条）",
                    "metricPointList": []
                }
            ],
            'unit': '条',
            "api": 'DataFilterTimes',
            "isPie": false,
            "selfHide": false
        },
    ],
    "groupHide": false
  }
]