import React, { useState, useEffect } from 'react';
import { Form, Collapse } from '@didi/dcloud-design';
import { collectLogTypes, codingFormatTypes, collectLogFormItemLayout } from './config';
import './index.less';
import LogFileRule from './LogFileRule';

const CollectLogConfiguration = (props: any) => {
  const [logListFile, setLogListFile] = useState([]);

  useEffect(() => {
    console.log(props.collectLogType, props.logFilter);
  }, [props.collectLogType, props.logFilter]);

  return (
    <div className="set-up collect-log-config">
      <Form form={props.form} {...collectLogFormItemLayout} layout={'vertical'}>
        <LogFileRule
          form={props.form}
          setLogListFile={setLogListFile}
          filePathList={props.filePathList}
          logListFile={logListFile}
          isNotLogPath={props.isNotLogPath}
          setisNotLogPath={props.setisNotLogPath}
          hostList={props.hostList}
          slicingRuleLogList={props.slicingRuleLogList}
          sliceRule={props.sliceRule}
          edit={props.editUrl}
          logType="file"
        />
      </Form>
    </div>
  );
};

export default CollectLogConfiguration;
