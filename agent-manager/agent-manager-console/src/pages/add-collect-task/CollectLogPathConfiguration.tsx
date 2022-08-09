import React, { useState, useEffect } from 'react';
import { Form, Collapse } from '@didi/dcloud-design';
import { collectLogFormItemLayout } from './config';
import LoopAddLogPath from './LoopAddLogPath';

import './index.less';

const CollectLogConfiguration = (props: any) => {
  const [logListFile, setLogListFile] = useState([]);

  return (
    <div className="set-up collect-log-config">
      <Form form={props.form} {...collectLogFormItemLayout} layout={'vertical'}>
        <LoopAddLogPath
          form={props.form}
          editUrl={props.editUrl}
          hostNames={props.hostNames}
          suffixfilesList={props.suffixfilesList}
          originFilePathList={props.originFilePathList}
          setHookPathList={props.setHookPathList}
          setFilePathList={props.setFilePathList}
          slicingRuleLogList={props.slicingRuleLogList}
          setLogListFile={setLogListFile}
          logListFile={logListFile}
          isNotLogPath={props.isNotLogPath}
          setisNotLogPath={props.setisNotLogPath}
          hostList={props.hostList}
        />
      </Form>
    </div>
  );
};

export default CollectLogConfiguration;
