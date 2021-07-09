import React, { useEffect, useState } from 'react';
import _ from 'lodash';
import { FormattedMessage } from 'react-intl';
import {
  Layout,
  Dropdown,
  Menu,
  Divider,
  TreeSelect,
  Popover,
} from 'antd';
import { normalizeTreeData } from './utils';
import { prefixCls } from './config';
import './style.less';
// import './assets/iconfont/iconfont.css';
// import './assets/iconfont/iconfont.js';
import './assets/iconfont-es/iconfont.js';

import userIcon from './assets/avatars.png';

const feConfig = require('../../../config/feConfig.json');

interface Props {
  tenantProjectVisible: boolean;
  children: React.ReactNode;
  language: string;
  onLanguageChange: (language: string) => void;
  selectedTenantProject?: any;
  setSelectedTenantProject?: (newSelectedTenantProject: any) => void;
  belongProjects?: any;
  onMount: () => void;
}

const { Header } = Layout;
const getSymbolByLanguage = (language: string) => {
  if (language === 'zh') return '#iconzhongwenicon';
  if (language === 'en') return '#iconyingwenicon';
  return '';
};
const normalizeTenantProjectData = (
  data: any[],
  tenantIdent?: string,
  tenantId?: number,
  tenantName?: string,
): any => {
  return _.map(data, (item) => {
    if (item.children) {
      return {
        ...item,
        tenantIdent: tenantIdent || item.ident,
        tenantId: tenantId || item.id,
        tenantName: tenantName || item.name,
        children: normalizeTenantProjectData(
          item.children,
          tenantIdent || item.ident,
          tenantId || item.id,
          tenantName || item.name,
        ),
      };
    }
    return {
      ...item,
      tenantIdent,
      tenantId,
      tenantName,
    };
  });
};
const treeIcon: (node: any) => JSX.Element = (node) => (
  <span
    style={{
      display: 'inline-block',
      backgroundColor: node.icon_color,
      width: 16,
      height: 16,
      lineHeight: '16px',
      borderRadius: 16,
      color: '#fff',
    }}
  >
    {node.icon_char}
  </span>
);
const renderTreeNodes = (nodes: any[]) => {
  return _.map(nodes, (node) => {
    if (_.isArray(node.children)) {
      return (
        <TreeSelect.TreeNode
          icon={treeIcon(node)}
          title={node.name}
          fullTitle={`${node.tenantName}-${node.name}`}
          key={String(node.id)}
          value={node.id}
          path={node.path}
          node={node}
          selectable={false}
        >
          {renderTreeNodes(node.children)}
        </TreeSelect.TreeNode>
      );
    }
    return (
      <TreeSelect.TreeNode
        icon={treeIcon(node)}
        title={node.name}
        fullTitle={`${node.tenantName}-${node.name}`}
        key={String(node.id)}
        value={node.id}
        path={node.path}
        isLeaf={true}
        node={node}
      />
    );
  });
};

export default function index(props: Props) {
  const cPrefixCls = `${prefixCls}-layout`;
  const [dispname, setDispname] = useState('admin');
  const [feConf, setFeConf] = useState(feConfig as any);
  const treeData = normalizeTreeData(props.belongProjects);
  const cacheProject = _.attempt(
    JSON.parse.bind(
      null,
      localStorage.getItem('icee-global-project') as string,
    ),
  );

  return (
    <Layout className={cPrefixCls}>
      <Header className={`${cPrefixCls}-header ${_.get(feConf, 'header.theme')}`}>
        <div className={`${cPrefixCls}-header-left`}>
          <a href='/' className={`${cPrefixCls}-logo`}>
            <img
              src={userIcon}
              alt="logo"
              style={{
                height: 24,
              }}
            />
            {_.get(feConf, 'header.subTitle')}
          </a>
        </div>
        <div className={`${cPrefixCls}-header-right`}>
          {_.get(feConf, 'header.mode') === 'complicated' ? (
            <>
              {props.tenantProjectVisible ? (
                <TreeSelect
                  size="small"
                  showSearch
                  treeIcon
                  className="global-tenantProject-select"
                  dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                  placeholder="请选择租户和项目"
                  treeNodeLabelProp="fullTitle"
                  allowClear
                  treeDefaultExpandAll
                  treeNodeFilterProp="fullTitle"
                  filterTreeNode={(inputValue: string, treeNode: any) => {
                    const { fullTitle = '', path = '' } = treeNode.props;
                    return fullTitle.indexOf(inputValue) > -1 || path.indexOf(inputValue) > -1;
                  }}
                  value={_.get(cacheProject, 'id')}
                  onChange={(_value, _label, extra) => {
                    const newSelectedTenantProject = {
                      tenant: {
                        id: _.get(extra, 'triggerNode.props.node.tenantId'),
                        ident: _.get(extra, 'triggerNode.props.node.tenantIdent'),
                      },
                      project: {
                        id: _.get(extra, 'triggerNode.props.node.id'),
                        ident: _.get(extra, 'triggerNode.props.node.ident'),
                        path: _.get(extra, 'triggerNode.props.node.path'),
                      },
                    };
                    props.setSelectedTenantProject && props.setSelectedTenantProject(newSelectedTenantProject);
                    localStorage.setItem(
                      'icee-global-tenant',
                      JSON.stringify(newSelectedTenantProject.tenant),
                    );
                    localStorage.setItem(
                      'icee-global-project',
                      JSON.stringify(newSelectedTenantProject.project),
                    );
                  }}
                >
                  {renderTreeNodes(normalizeTenantProjectData(treeData))}
                </TreeSelect>
              ) : null}
              <div className={`${cPrefixCls}-header-right-links`}>
                {
                  Array.isArray(_.get(feConf, 'header.right_links')) &&
                  _.get(feConf, 'header.right_links').map((item: { icon: string | undefined; href: string; text: string; }, index: number) => {
                    return item.icon ?
                      <a href={item.href} key={index}>
                        <Popover content={item.text}>
                          <svg className={`${cPrefixCls}-header-menus-icon`} aria-hidden="true">
                            <use xlinkHref={item.icon}></use>
                          </svg>
                        </Popover>
                      </a> : <a href={item.href} key={index}>{item.text}</a>;
                  })
                }
              </div>
              <Divider
                className={`${cPrefixCls}-header-right-divider`}
                type="vertical"
              />
            </>
          ) : null}
          <span className={`${cPrefixCls}-username`}>
            <img src={userIcon} alt="" />
            <span style={{ paddingRight: 5 }}>{dispname}</span>
            {/* <svg className={`${prefixCls}-layout-menus-icon`} aria-hidden="true">
              <use xlinkHref="#iconxuanzekuangzhankai"></use>
            </svg> */}
          </span>
        </div>
      </Header>
      <div
        style={{
          overflow: 'hidden',
          position: 'relative',
        }}
      >
        <div className={`${cPrefixCls}-main`}>{props.children}</div>
      </div>
    </Layout>
  );
}
