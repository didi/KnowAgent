import React, { useState, useEffect } from 'react';
import { Form, Collapse } from '@didi/dcloud-design';
import { collectLogTypes, codingFormatTypes, collectSpliceRuleItemLayout } from './config';
import './index.less';
import LogFileRule from './LogFileRule';

const CollectLogConfiguration = (props: any) => {
  const [logListFile, setLogListFile] = useState([]);

  return (
    <div className="set-up collect-log-config">
      <Form form={props.form} {...collectSpliceRuleItemLayout} layout={'vertical'}>
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
