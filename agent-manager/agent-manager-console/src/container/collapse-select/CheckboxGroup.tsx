import React from 'react';
import { Checkbox, Tooltip, Icon, Row } from 'antd';

interface ICheckboxGroups {
  title: string;
  checkData: any;
  parent: any;
}

export class DataCheckboxGroup extends React.Component<ICheckboxGroups> {

  constructor(props: any) {
    super(props);
  }

  public state = {
    checkedList: this.props.checkData,
    indeterminate: true,
    checkAll: true,
    expand: true,
  };

  public getCheckedList(e: any) {
    return this.props.parent(e);
  }

  public onCheckAllChange = (e: any) => {
    const checkedList = e.target.checked ? this.props.checkData : [];
    this.getCheckedList(checkedList);
    this.setState({
      checkedList,
      indeterminate: false,
      checkAll: e.target.checked,
    });
  };

  public onChange = (checkedValues: any) => {
    this.getCheckedList(checkedValues);
    this.setState({
      checkedList: checkedValues,
      indeterminate: !!checkedValues.length && checkedValues.length < this.props.checkData.length,
      checkAll: checkedValues.length === this.props.checkData.length,
    });
  };

  public handleExpandClick = () => {
    this.setState({ expand: !this.state.expand });
  }

  public renderCheckBox = (plains: any, expand: boolean) => {
    return (
      <Row>
        {plains.map((val: any, index: number) => {
          return (
            <span key={index} className={(index === 1 && expand) ? 'hide' : ''}>
              {val?.map((ele: any, i: number) => {
                return (
                  ele.length > 8 ?
                    <Tooltip key={i} placement="bottomLeft" title={ele}>
                      <Checkbox value={ele}>{ele}</Checkbox>
                    </Tooltip>
                    : <Checkbox key={i} value={ele}>{ele}</Checkbox>
                );
              })}
            </span>
          )
        })}
        {plains[1]?.length && expand ? <span>...</span> : null}
      </Row>
    )
  }

  public render() {
    const { expand } = this.state;
    const { title, checkData } = this.props;
    const plains = [checkData?.slice(0, 6), checkData?.slice(6)];

    return (
      <>
        <div className="collapse-head">
          <div className="head-box">
            <Checkbox
              key='all'
              indeterminate={this.state.indeterminate}
              onChange={this.onCheckAllChange}
              checked={this.state.checkAll}
            />
            <h3>{title}：</h3>
            <Checkbox.Group
              value={this.state.checkedList}
              onChange={this.onChange}
            >
              {this.renderCheckBox(plains, expand)}
            </Checkbox.Group>
          </div>
          {plains[1]?.length ?
            <a className="collapse-title" onClick={this.handleExpandClick}>
              {expand ? '展开' : '收起'}
              <Icon type={expand ? 'down' : 'up'} />
            </a> : null
          }
        </div>
      </>
    )
  }
}