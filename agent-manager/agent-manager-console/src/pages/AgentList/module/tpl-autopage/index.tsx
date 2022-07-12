import React, { useState, useEffect } from 'react';
import './index.less';
import { Container } from '@didi/dcloud-design';
import ProTableProject from '../tpl-protable/index';
import ProFormProject from '../tpl-proform/index';
import Prodescription from '../tpl-prodescription/index';
const pageTreeData = null;

const AutoPage = (props) => {
  const PageContainter = (ele) => {
    return <div className="pageContainter">{ele.children}</div>;
  };
  // const {pageTreeData}=props;
  const [pageData, setPageData] = useState<any>(pageTreeData || props.pageTreeData);
  const Components = {
    ProTableProject: ProTableProject,
    ProFormProject: ProFormProject,
    Prodescription: Prodescription,
  };
  const [visible, setVisible] = useState(false);
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
