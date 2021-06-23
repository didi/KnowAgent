import React from 'react';
import * as actions from '../../actions';
import { connect } from "react-redux";
import { Dispatch } from 'redux';
import './index.less';

const mapStateToProps = (state: any) => ({
  content: state.fullScreen.content,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  closeContent: () => dispatch(actions.closeContent()),
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;
@connect(mapStateToProps, mapDispatchToProps)
export class FullScreen extends React.Component<Props> {

  public handleClose = (event: React.MouseEvent<HTMLDivElement, MouseEvent>) => {
    if ((event.target as any).nodeName === 'SECTION') this.props.closeContent();
  }

  public render() {
    if (!this.props.content) return null;
    return (
      <section className="full-screen-mark" onClick={this.handleClose}>
        <div className="full-screen-content">
          {this.props.content}
        </div>
      </section>
    );
  }
}
