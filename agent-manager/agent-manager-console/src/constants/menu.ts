const pkgJson = require('../../package');
export const systemKey = pkgJson.ident;

export const leftMenus = {
  name: `${systemKey}`,
  icon: 'icon-jiqun',
  path: '/',
  children: [
    {
      name: 'home',
      path: '/version',
      icon: 'icon-wodegongzuotai',
      children: [
        {
          name: 'operation',
          path: 'operation',
        },
        {
          name: 'devops',
          path: 'devops',
        },
      ],
    },
    //元数据中心
    {
      name: 'meta',
      path: '/meta',
      icon: 'icon-cebiandaohang-shujuyuan',
      children: [
        {
          name: 'dataSource',
          path: 'dataSource',
        },
        {
          name: 'receivingTerminal',
          path: 'receivingTerminal',
        },
        {
          name: 'metaVersion',
          path: 'metaVersion',
        },
      ],
    },
    // Agent中心
    {
      name: 'main',
      path: '/main',
      icon: 'icon-Agentzhongxin',
      children: [
        {
          name: 'list',
          path: '',
          icon: '#icon-luoji',
        },
        {
          name: 'agentVersion',
          path: 'agentVersion',
          icon: '#icon-jiqun1',
        },
      ],
    },
    //采集任务管理
    {
      name: 'collect',
      path: '/collect',
      icon: 'icon-cebiandaohang-caijirenwu',
    },
    //监控中心
    {
      name: 'monitor',
      path: '/monitor',
      icon: 'icon-cebiandaohang-jiankongzhongxin',
      children: [
        {
          name: 'metric',
          path: 'metric',
        },
        {
          name: 'agent-kanban',
          path: 'agent-kanban',
        },
      ],
    },
    //运维中心
    {
      name: 'tool',
      path: '/tool',
      icon: 'icon-a-cebiandaohang-zhibiaotancha',
      children: [
        {
          name: 'indicator-probe',
          path: 'indicator-probe',
        },
      ],
    },
    //操作记录
    // {
    //   name: 'operationRecord',
    //   path: 'operationRecord',
    //   icon: 'icon-caozuojilu',
    // },
  ],
};

// key值需要与locale zh 中key值一致
export const permissionPoints = {
  [`menu.${systemKey}.home`]: true,
};

export const ROUTER_CACHE_KEYS = {
  home: `menu.${systemKey}.home`,
};
