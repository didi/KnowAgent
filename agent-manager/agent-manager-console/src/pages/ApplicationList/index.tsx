import React, { useState, useEffect } from 'react';
import './index.less';
import { AppContainer, Container } from '@didi/dcloud-design';
import ProTableProject from '@tpl_packages/module/tpl-protable/index';
import ProFormProject from '@tpl_packages/module/tpl-proform/index';
import Prodescription from '@tpl_packages/module/tpl-prodescription/index';
import * as customHandle from './module';
import * as filterMap from './filtter';
import { EventBusTypes } from '../../constants/event-types';
const pageTreeData = require('./pageTreeData.json');

const AutoPage = (props: any) => {
  const PageContainter = (ele) => {
    return (
      <div className="pageContainter" style={{ padding: '24px', backgroundColor: '#F8F8FB' }}>
        {ele.children}
      </div>
    );
  };
  // const {pageTreeData}=props;
  const [pageData, setPageData] = useState<any>(pageTreeData || props?.pageTreeData);
  const Components = {
    ProTableProject: ProTableProject,
    ProFormProject: ProFormProject,
    Prodescription: Prodescription,
  };
  const [visible, setVisible] = useState(false);
  const returnRender = (item, slot) => {
    if (slot.children.length === 0) {
      return Components[slot.componentName]({ config: slot.dataInfo, customHandle: customHandle, filterMap: filterMap.formatMap });
    } else {
      return RenderComponents(slot);
    }
  };

  const RenderComponents = (props) => {
    const { children } = props;
    if (!children || children.length === 0) {
      return null;
    }
    return children.map((slot, index) => {
      return (
        <div key={index} style={{ ...slot.containerStyle }}>
          {!!slot.nodeTitle && <div style={{ ...slot.nodeStyle }}>{slot.nodeTitle}</div>}
          <Container {...slot.grid}>{returnRender({ children: slot.children }, slot)}</Container>
        </div>
      );
    });
  };
  useEffect(() => {
    if (props.pageTreeData) {
      setPageData(props.pageTreeData);
    }
  }, [props.pageTreeData]);
  const headerLeftContent = <>应用管理</>;
  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, []);

  return (
    <>
      <PageContainter>
        <RenderComponents children={pageData.children} />
      </PageContainter>
    </>
  );
};

export default AutoPage;
