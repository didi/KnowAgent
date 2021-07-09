import React from 'react';
import { withRouter, RouteComponentProps } from 'react-router-dom';
import { Layout } from 'antd';
import classNames from 'classnames';
import _ from 'lodash';
import { prefixCls } from './config';
import MeunContent from './MenuContent';
import './assets/iconfont/iconfont.css';
import './assets/iconfont/iconfont.js';

interface Props {
  noBackground?: boolean,
  menus: any,
  treeVisible?: boolean,
  systemName: string,
  systemNameChn: string,
  children: React.ReactNode,
  siderMenuVisible?: boolean; // 是否需要菜单
  intlInfo?: any;
}

const defaultCollapsed = window.localStorage.getItem('siderMenuCollapsed') === 'true';
const { Content, Sider } = Layout;

const MenuLayout = (props: Props & RouteComponentProps) => {
  const [menuCollapsed, setMenuCollapsed] = React.useState(defaultCollapsed);
  const [permissionPoints, setPermissionPoints] = React.useState(null);

  const {
    systemName, menus, systemNameChn, siderMenuVisible,
  } = props;
  const currentSystemMenuConf = _.get(menus, 'children');
  const cPrefixCls = `${prefixCls}-layout`;


  const renderContent = () => {
    const { noBackground = false } = props;
    const cPrefixCls = `${prefixCls}-layout`;

    return (
      <Layout
        className={classNames({
          [`${cPrefixCls}-container`]: true,
        })}
        style={{ height: '100%' }}
      >
        <Content className={`${cPrefixCls}-content`} style={{ position: 'relative' }}>
          <div className={classNames({
            [`${cPrefixCls}-main`]: true,
            [`${cPrefixCls}-main-noBg`]: noBackground,
          })} id={`${cPrefixCls}-main`}>
            {props.children}
          </div>
        </Content>
      </Layout>
    );
  }
  return (<>
    <Layout className={cPrefixCls}>
      {siderMenuVisible
        && <Sider
          theme="light"
          width={190}
          collapsedWidth={56}
          className={
            classNames({
              [`${cPrefixCls}-sider-nav`]: true,
            })
          }
          trigger={null}
          collapsible
          collapsed={menuCollapsed}
        >
          <div
            className={`${prefixCls}-layout-sider-nav-systemName`}
            style={{
              paddingLeft: menuCollapsed ? 20 : 24,
            }}
          >
            {
              menuCollapsed
                ? <svg className={`${prefixCls}-layout-menus-icon`} aria-hidden="true" style={{ marginRight: 0 }}>
                  <use xlinkHref={_.get(menus, 'icon')} />
                </svg>
                : <span>
                  <svg className={`${prefixCls}-layout-menus-icon`} aria-hidden="true">
                    <use xlinkHref={_.get(menus, 'icon')} />
                  </svg>
                  {systemNameChn}
                </span>
            }
          </div>
          <MeunContent
            systemName={systemName}
            systemNameChn={systemNameChn}
            menuConf={currentSystemMenuConf}
            className={`${cPrefixCls}-menu`}
            collapsed={menuCollapsed}
            permissionPoints={permissionPoints}
          />
          <div
            className={`${prefixCls}-layout-sider-nav-bottom`}
            onClick={() => {
              setMenuCollapsed(!menuCollapsed);
              window.localStorage.setItem('siderMenuCollapsed', String(!menuCollapsed));
            }}
          >
            <svg className={`${prefixCls}-layout-menus-icon`} aria-hidden="true">
              <use xlinkHref={menuCollapsed ? '#iconzhankaiicon' : '#iconshouqiicon'} />
            </svg>
          </div>
        </Sider>
      }
      <Content
        style={{
          marginLeft: siderMenuVisible ? (menuCollapsed ? 56 : 190) : 0,
          overflow: 'hidden',
        }}
      >
        {renderContent()}
      </Content>
    </Layout>
  </>);
};

export default withRouter(MenuLayout);
