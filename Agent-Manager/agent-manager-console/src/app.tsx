import React, { useState } from 'react';
import { BrowserRouter, Switch, Route, Redirect } from 'react-router-dom';
import _ from 'lodash';
import { ConfigProvider } from 'antd';
import { IntlProvider as DantdIntlProvider } from 'antd-advanced'
import antdZhCN from 'antd/lib/locale/zh_CN';
import antdEnUS from 'antd/lib/locale/en_US';
import { IntlProvider } from 'react-intl';
import intlZhCN from './locales/zh';
import intlEnUS from './locales/en';
import { InjectIntlContext } from '@pkgs/hooks/useFormatMessage';
import { Page403, Page404 } from '@pkgs/Exception';
import LayoutHeaderNav from '@pkgs/Layout';
import LayoutMain from '@pkgs/Layout/LeftMenu';
import { leftMenus, systemKey } from './constants/menu';
import { Provider as ReduxProvider } from 'react-redux';
import store from './store';
import './index.less';
import { Agent } from './pages/agent';
import { DataSource } from './pages/dataSource';
import { ReceivingTerminal } from './pages/receivingTerminal';
import { OperationRecord } from './pages/operationRecord';
import { Collect } from './pages/collect';
import { AgentVersion } from './pages/agentVersion';
import { OperationTasks } from './pages/operationTasks';
interface ILocaleMap {
  [index: string]: any;
}

const localeMap: ILocaleMap = {
  zh: {
    antd: antdZhCN,
    intl: 'zh',
    intlMessages: intlZhCN,
    title: systemKey,
  },
  en: {
    antd: antdEnUS,
    intl: 'en',
    intlMessages: intlEnUS,
    title: systemKey,
  },
};

export const { Provider, Consumer } = React.createContext('zh');

const defaultLanguage = window.localStorage.getItem('language') || navigator.language.substr(0, 2);

const App = () => {
  const [language, setLanguage] = useState(defaultLanguage);
  const antdAdvanceLanguage = language.includes('zh') ? 'zh-CN' : 'en';
  const intlMessages = _.get(localeMap[language], 'intlMessages', intlZhCN);
  const title = _.get(localeMap[language], 'title');

  return (
    <IntlProvider
      locale={_.get(localeMap[language], 'intl', 'zh')}
      messages={intlMessages}
    >
      <ConfigProvider locale={_.get(localeMap[language], 'antd', antdZhCN)}>
        <InjectIntlContext>
          <Provider value={language}>
            <DantdIntlProvider locale={antdAdvanceLanguage}>
              <ReduxProvider store={store}>
                <BrowserRouter basename={systemKey}>
                  <Switch>
                    <Route exact={true} path="/403" component={Page403} />
                    <Route exact={true} path="/404" component={Page404} />
                    <LayoutHeaderNav
                      language={language}
                      onLanguageChange={setLanguage}
                      tenantProjectVisible={false}
                      onMount={() => { }}>
                      <LayoutMain
                        siderMenuVisible={true}
                        systemName={systemKey}
                        systemNameChn={title}
                        menus={leftMenus}
                      >
                        <Switch>
                          <Route path="/" exact={true} component={Agent} />
                          <Route path="/list" exact={true} component={Agent} />
                          <Route path="/detail" exact={true} component={Agent} />
                          <Route path="/dataSource" exact={true} component={DataSource} />
                          <Route path="/dataSource/appList" exact={true} component={DataSource} />
                          <Route path="/receivingTerminal" exact={true} component={ReceivingTerminal} />
                          <Route path="/receivingTerminal/clusterList" exact={true} component={ReceivingTerminal} />
                          <Route path="/operationRecord" exact={true} component={OperationRecord} />
                          <Route path="/collect" exact={true} component={Collect} />
                          <Route path="/collect/detail" exact={true} component={Collect} />
                          <Route path="/agentVersion" exact={true} component={AgentVersion} />
                          <Route path="/operationTasks" exact={true} component={OperationTasks} />
                          <Route path="/operationTasks/taskDetail" exact={true} component={OperationTasks} />
                          <Route path="/collect/add-task" exact={true} component={Collect} />
                          <Route path="/collect/edit-task" exact={true} component={Collect} />
                        </Switch>
                      </LayoutMain>
                    </LayoutHeaderNav>
                    <Route render={() => <Redirect to="/404" />} />
                  </Switch>
                </BrowserRouter>
              </ReduxProvider>
            </DantdIntlProvider>
          </Provider>
        </InjectIntlContext>
      </ConfigProvider>
    </IntlProvider>
  );
};

export default App;
