import React from 'react';
import { Icon } from 'antd';
import './index.less';

interface ICardProps {
  title: string;
  groupHide: boolean;
  expand?: boolean;
  charts?: JSX.Element[];
  noTitle?: boolean;
}

export class ExpandCard extends React.Component<ICardProps> {
  public state = {
    innerExpand: true,
  };

  public handleClick = () => {
    this.setState({ innerExpand: !this.state.innerExpand });
  }

  public render() {
    let { expand } = this.props;
    const { noTitle } = this.props;
    if (expand === undefined) expand = this.state.innerExpand;
    const { charts, groupHide } = this.props;
    return (
      <>
        {!groupHide && <div className="card-wrapper">
          {!noTitle ?
            <div className="card-head">
              <h3>{this.props.title}：</h3>
              <a className="card-title" onClick={this.handleClick}>
                {expand ? '收起' : '展开'}
                <Icon type={expand ? 'down' : 'up'} />
              </a>
            </div>
            : null}
          {expand ?
            <div className="card-content">
              {(charts || []).map((c, index) => {
                return (
                  <div key={c.key}>{c}</div>
                )
              })}
            </div> : null}
        </div>}
      </>
    );
  }
}
