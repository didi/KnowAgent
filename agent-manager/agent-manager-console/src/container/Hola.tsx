import React from "react";
import { connect } from "react-redux";
import * as actions from '../actions';

const mapStateToProps = (state) => ({
  isLoading: state.hola.loading,
  user: state.hola.user,
});

const mapDispatchToProps = dispatch => ({
  getUserAsync: () => dispatch(actions.getUserAsync())
});

type Props = ReturnType<typeof mapStateToProps> & ReturnType<typeof mapDispatchToProps>;

type State = {};

@connect(mapStateToProps, mapDispatchToProps)
export default class Hello extends React.Component<Props, State> {
  render() {
    const { isLoading, user, getUserAsync } = this.props;
    return (
      <>
        <button type="button" onClick={() => getUserAsync()} disabled={isLoading}>
          Load User
        </button>
        <div className="greeting">
          <h1>Hello, {user?.name}!</h1>
          <h2>COOL!</h2>
          <h3>Good to see you here.</h3>
        </div>
      </>
    );
  }
}



