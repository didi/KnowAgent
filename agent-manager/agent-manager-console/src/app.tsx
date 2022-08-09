import React, { useEffect } from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import _ from 'lodash';
import dantdZhCN from '@didi/dcloud-design/lib/locale/zh_CN';
import dantdEnUS from '@didi/dcloud-design/lib/locale/en_US';
import intlZhCN from './locales/zh';
import intlEnUS from './locales/en';
import { systemKey, leftMenus } from './constants/menu';
import { DLayout, AppContainer, RouteGuard, Empty, Image } from '@didi/dcloud-design';
import { pageRoutes } from './pages';
import './index.less';
import img from './image/logo.png';

interface ILocaleMap {
  [index: string]: any;
}

const localeMap: ILocaleMap = {
  'zh-CN': {
    dantd: dantdZhCN,
    intl: 'zh-CN',
    intlMessages: intlZhCN,
  },
  en: {
    dantd: dantdEnUS,
    intl: 'en',
    intlMessages: intlEnUS,
  },
};

export const { Provider, Consumer } = React.createContext('zh');

const defaultLanguage = 'zh-CN';
const App = () => {
  const intlMessages = _.get(localeMap[defaultLanguage], 'intlMessages', intlZhCN);
  const locale = _.get(localeMap[defaultLanguage], 'intl', 'zh-CN');
  const antdLocale = _.get(localeMap[defaultLanguage], 'dantd', dantdZhCN);
  const fn = () => {
    return Promise.resolve('test');
  };

  return (
    <AppContainer
      intlProvider={{ locale, messages: intlMessages }}
      antdProvider={{
        locale: antdLocale,
        renderEmpty: (): JSX.Element => (<Empty description="数据为空~" image={Empty.PRESENTED_IMAGE_CUSTOM} />) as JSX.Element,
      }}
    >
      <Router basename={systemKey}>
        <DLayout.DSkoteLayout
          siderbarNavTitle={
            <>
              <Image width={24} src={img} />
              <i className="iconfont icon-a-agentlogo" />
            </>
          }
          noFooter
          headerLeftContent={'我的工作台'}
          defaultSideCollpsed={false}
          systemKey={systemKey}
          leftMenus={leftMenus}
          sidebarTheme={'dark'}
          getUserInfo={fn}
          needSettingsIcon={false}
        >
          <RouteGuard routeList={pageRoutes} noMatch={() => <></>} />
        </DLayout.DSkoteLayout>
      </Router>
    </AppContainer>
  );
};

export default App;
