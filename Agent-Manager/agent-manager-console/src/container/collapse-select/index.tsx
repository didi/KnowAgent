import React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import { DataCheckboxGroup } from './CheckboxGroup';
import { IRdAgentMetrics } from '../../interface/agent';
import './index.less';

interface ICollapseSelectProps {
  metrics: IRdAgentMetrics[];
}

interface IMetricesTypes {
  title: string;
  checkData: string[];
}

const mapDispatchToProps = (dispatch: Dispatch) => ({
  setChartMetrics: (chartMetrics: IRdAgentMetrics[]) => dispatch(actions.setChartMetrics(chartMetrics)),
});

type Props = ReturnType<typeof mapDispatchToProps>;
@connect(null, mapDispatchToProps)
export class DataCollapseSelect extends React.Component<ICollapseSelectProps & Props> {

  public state = {
    tansterMetrics: JSON.parse(JSON.stringify(this.props.metrics)) as IRdAgentMetrics[],
  }

  public onBindClick = async (msg: string[], index: number) => {
    const datas = JSON.parse(JSON.stringify(this.state.tansterMetrics)) as IRdAgentMetrics[];
    datas.map((ele, i) => {
      if (index === i) {
        if (msg?.length) {
          ele.groupHide = false;
          ele.metricPanelList.forEach(v => {
            if (msg.indexOf(v.panelName) === -1) {
              v.selfHide = true;
            } else {
              v.selfHide = false;
            }
          })
        } else {
          ele.groupHide = true;
        }
      }
      return ele;
    })
    this.setState({ tansterMetrics: datas });
    this.props.setChartMetrics(datas);
  }

  public setMetriceTypes = () => {
    const types = this.props.metrics.map(ele => {
      return {
        title: ele.metricPanelGroupName,
        checkData: ele.metricPanelList.map(v => v.panelName),
      }
    });
    return types;
  }

  public render() {
    const metriceTypes = this.setMetriceTypes();
    return (
      <div className="collapse-select">
        {metriceTypes.map((ele: IMetricesTypes, index: number) => {
          return (
            <DataCheckboxGroup
              key={index}
              title={ele.title}
              checkData={ele.checkData}
              parent={(msg: any) => this.onBindClick(msg, index)}
            />
          )
        })}
      </div>
    );
  }
}