/* eslint-disable react/prop-types */
import React, { useState, useEffect } from 'react';
import { Input, Select } from '@didi/dcloud-design';
import './index.less';
import { AppContainer, Container } from '@didi/dcloud-design';
import ProTableProject from './module/tpl-protable/index';
import ProFormProject from './module/tpl-proform/index';
import Prodescription from './module/tpl-prodescription/index';
import { EventBusTypes } from '../../constants/event-types';
// import ProChartContainer from './module/tpl-chartcontainer/index';

const pageTreeData = require('./pageTreeData.json');
const { TextArea } = Input;
const { Option } = Select;
const AutoPage = (props) => {
  const [pageData, setPageData] = useState<any>(pageTreeData);
  const Components = {
    ProTableProject: ProTableProject,
    ProFormProject: ProFormProject,
    Prodescription: Prodescription,
    // ProChartContainer: ProChartContainer,
  };
  const [visible, setVisible] = useState(false);
  const PageContainter = (props) => {
    return (
      <div className="pageContainter" style={{ padding: '24px', backgroundColor: '#F8F8FB' }}>
        {props.children}
      </div>
    );
  };
  const returnRender = (item, slot) => {
    if (slot.children.length === 0) {
      return Components[slot.componentName]({ config: slot.dataInfo });
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
  const headerLeftContent = <>操作记录</>;
  useEffect(() => {
    AppContainer.eventBus.emit(EventBusTypes.renderheaderLeft, [headerLeftContent]);
  }, []);

  useEffect(() => {
    if (props.pageTreeData) {
      setPageData(props.pageTreeData);
    }
  }, [props.pageTreeData]);

  return (
    <>
      <PageContainter>
        <RenderComponents children={pageData.children} />
      </PageContainter>
    </>
  );
};

export default AutoPage;
