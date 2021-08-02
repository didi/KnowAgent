import * as React from 'react';
import { connect } from "react-redux";
import ModifyHost from '../modal/agent/ModifyHost';
import NewHost from '../modal/agent/NewHost';
import ActionApp from '../modal/dataSource/actionApp';
import ActionCluster from '../modal/receivingTerminal/actionCluster';
import DiagnosisReport from '../modal/agent/DiagnosisReport';
import AssociateHostDetail from '../modal/agent/AssociateHostDetail';
import CollectFileInfoDetail from '../modal/agent/CollectFileInfoDetail';
import MetricDetail from '../modal/agent/MetricDetail';
import InstallHost from '../modal/agent/InstallHost';
import ActionVersion from '../modal/agentVersion/actionVersion';

const mapStateToProps = (state: any) => ({
  isLoading: state.modal.loading,
  modalId: state.modal.modalId,
  drawerId: state.modal.drawerId,
});

const AllModalInOne = (props: any) => {
  const { modalId, drawerId } = props;
  if (!modalId && !drawerId) return null;

  return (
    <>
      {modalMap[modalId] || null}
      {drawerMap[drawerId] || null}
    </>
  )
}

const modalMap = {
  ModifyHost: <ModifyHost />,
  NewHost: <NewHost />,
  ActionApp: <ActionApp />,
  ActionCluster: <ActionCluster />,
  InstallHost: <InstallHost />,
  ActionVersion: <ActionVersion />,
} as {
  [key: string]: JSX.Element;
};

const drawerMap = {
  DiagnosisReport: <DiagnosisReport />,
  AssociateHostDetail: <AssociateHostDetail />,
  CollectFileInfoDetail: <CollectFileInfoDetail />,
  MetricDetail: <MetricDetail />
} as {
  [key: string]: JSX.Element;
};

export default connect(mapStateToProps)(AllModalInOne)
