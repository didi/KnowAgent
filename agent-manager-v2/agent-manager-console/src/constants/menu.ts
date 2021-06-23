const pkgJson = require('../../package');
export const systemKey = pkgJson.ident;
export const leftMenus = {
  name: `${systemKey}`,
  path: 'main',
  icon: '#icon-jiqun',
  children: [
    {
      name: 'main',
      path: '',
      icon: '#icon-jiqun1',
      children: [
        {
          name: 'list',
          path: 'list',
          icon: '#icon-luoji',
          permissionPoint: 'Agent_agent_management_page',
        }, {
          name: 'agentVersion',
          path: 'agentVersion',
          icon: '#icon-jiqun1',
          permissionPoint: 'Agent_agent_version_page',
        }, {
          name: 'operationTasks',
          path: 'operationTasks',
          icon: '#icon-jiqun1',
          permissionPoint: 'Agent_operational_tasks_page',
        },
        // {
        //   name: 'hola',
        //   path: 'hola',
        //   icon: '#icon-jiqun1',
        // }
      ],
    },
    {
      name: 'dataSource',
      path: 'dataSource',
      icon: '#icon-jiqun1',
      children: [
        {
          name: 'appList',
          path: 'appList',
          icon: '#icon-luoji',
          permissionPoint: 'Agent_dataSource_app_page',
        }],
    },
    {
      name: 'receivingTerminal',
      path: 'receivingTerminal',
      icon: '#icon-jiqun1',
      children: [
        {
          name: 'clusterList',
          path: 'clusterList',
          icon: '#icon-luoji',
          permissionPoint: 'Agent_receivingterminal_cluster_page',
        }],
    },
    {
      name: 'collect',
      path: 'collect',
      icon: '#icon-jiqun1',
      permissionPoint: 'Agent_collect_page',
    },
    {
      name: 'operationRecord',
      path: 'operationRecord',
      icon: '#icon-jiqun1',
      permissionPoint: 'Agent_operationRecord_page',
    },
  ],
};
