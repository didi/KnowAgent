import React from 'react';
import Line, { hasData } from './../../../component/echarts';
import { createOptions } from './constants';
import './index.less';
// import { data } from './mock';

interface IProps {
  configList: any[];
  title: string;
  dataSouce: any;
}

const lineClassName = 'dashboard-lineCard';

export class LineChart extends React.Component<IProps> {

  public state = {
    width: document.querySelector(`.dashboard-lineCard-content-linebox-line`)?.clientWidth,
    height: 263,
  }

  public renderLine = (config: any) => {
    const options = createOptions(config, this.props.dataSouce[config.api]);
    return (
      <div className={`${lineClassName}-content-linebox`} key={config.title}>
        {/* <div className={`${lineClassName}-content-linebox-title`}>
          {config.title}
        </div> */}
        <div className={`${lineClassName}-content-linebox-line`}>
          <Line width={this.state.width} height={this.state.height} options={options} key={config.title} />
        </div>
      </div>
    );
  }

  public render () {
    return (
      <div className={lineClassName}>
        <div className={`${lineClassName}-title`}>{this.props.title}</div>
        <div className={`${lineClassName}-content`}>
          {this.props.configList.map(config => (
            this.renderLine(config)
          ))}
        </div>
      </div>
    )
  }
}