import AllModalInOne from '../container/AllModalInOne';
import { IPageRouteItem } from '../interface/common';
import { FullScreen } from '../container/full-screen';
import { connect } from "react-redux";
import * as actions from '../actions';
import * as React from 'react';

import { Route, Switch } from 'react-router-dom';

interface ICommonRoute {
  pageRoute: IPageRouteItem[];
  setUserInfo: Function;
  setResPermissions: Function;
}


const mapStateToProps = (state: any) => ({
  // loading: state.tenantProject.loading,
  // userLoading: state.userInfo.loading,
  rdbPoints: state.resPermission.rdbPoints
});

const mapDispatchToProps = (dispatch: any) => ({
  // setProjectInfo: (project: any) => dispatch(actions.setProjectInfo(project)),
  setUserInfo: (user: any) => dispatch(actions.setUser(user)),
  setResPermissions: (params: any) => dispatch(actions.setResPermissions(params)),
});
class CommonRoutePage extends React.Component<ICommonRoute> {
  public componentDidMount() {
    //const { setUserInfo, setResPermissions } = this.props
    // const userInfo = auth.getSelftProfile() || {} as any;
    // setUserInfo(userInfo);
  }
  public render() {
    const { pageRoute } = this.props;
    return (
      <>
        <div className="core-container">
          {
            <Switch>
              {
                // rdbPoints 
                // && 
                pageRoute.map((item: IPageRouteItem, key: number) =>
                  // <Route key={key} path={item.path} exact={item.exact} component={item.isNoPermission ? () => <Redirect to="/403" /> : item.component} />
                  <Route key={key} path={item.path} exact={item.exact} component={item.component} />
                )
              }
            </Switch>
          }
        </div>
        <AllModalInOne />
        <FullScreen />
      </>
    );
  }
}


export default connect(mapStateToProps, mapDispatchToProps)(CommonRoutePage);