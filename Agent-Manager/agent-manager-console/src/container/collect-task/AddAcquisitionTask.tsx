
import * as React from 'react';
import { CustomBreadcrumb } from '../../component/CustomComponent';
import { collectAddTaskBreadcrumb, collectEditTaskBreadcrumb } from './config';
import { RouteComponentProps } from 'react-router-dom';
import StepsForm from '../add-collect-task';
import Url from '../../lib/url-parser';
import './index.less';

export class AddAcquisitionTask extends React.Component<RouteComponentProps> {
  public taskId: number;
  public addUrl = window.location.pathname.includes('/add-task');

  constructor(props: any) {
    super(props);
    const url = Url();
    this.taskId = Number(props.location.state?.taskId);
  }

  public render() {
    return (
      <>
        <CustomBreadcrumb btns={this.addUrl ? collectAddTaskBreadcrumb : collectEditTaskBreadcrumb} />
        <div className="list page-wrapper">
          <StepsForm taskId={this.taskId || ''} history={this.props.history} />
        </div>
      </>
    );
  }
};
