const pkgJson = require('../../package');
export const systemKey = pkgJson.ident;

export const leftMenus = {
  name: `${systemKey}`,
  icon: 'icon-jiqun',
  path: '/',
  children: [
    {
      name: 'home',
      path: 'version',
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
    // Agent中心
    {
      name: 'main',
      path: 'main',
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
        {
          name: 'agent-kanban',
          path: 'agent-kanban',
          icon: '#icon-jiqun1',
        },
      ],
    },
    // {
    //   name: 'collect',
    //   path: 'collect',
    //   icon: 'icon-cebiandaohang-caijirenwu',
    //   children: [
    //     {
    //       name: 'metric',
    //       path: 'metric',
    //       icon: '#icon-luoji',
    //     },
    //   ],
    // },
    {
      name: 'dataSource',
      path: 'dataSource',
      icon: 'icon-cebiandaohang-shujuyuan',
    },
    {
      name: 'receivingTerminal',
      path: 'receivingTerminal',
      icon: 'icon-jieshouduanguanli',
    },
    // {
    //   name: 'collect',
    //   path: 'collect',
    //   icon: '#icon-jiqun1',
    // },
    {
      name: 'collect',
      path: 'collect',
      icon: 'icon-cebiandaohang-caijirenwu',
      children: [
        {
          name: 'list',
          path: '',
          icon: '#icon-luoji',
        },
        {
          name: 'metric',
          path: 'metric',
          icon: '#icon-luoji',
        },
      ],
    },
    {
      name: 'tool',
      path: 'tool',
      icon: 'icon-a-cebiandaohang-zhibiaotancha',
      children: [
        {
          name: 'indicator-probe',
          path: 'indicator-probe',
          icon: 'icon-a-cebiandaohang-zhibiaotancha',
        },
      ],
    },
    {
      name: 'operationRecord',
      path: 'operationRecord',
      icon: 'icon-caozuojilu',
    },
    // {
    //   name: "kafka",
    //   path: "kafka",
    //   icon: "icon-cebiandaohang-caijirenwu",
    //   children: [
    //     {
    //       name: "physics",
    //       path: "physics",
    //       icon: "#icon-luoji",
    //     },
    //   ],
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
