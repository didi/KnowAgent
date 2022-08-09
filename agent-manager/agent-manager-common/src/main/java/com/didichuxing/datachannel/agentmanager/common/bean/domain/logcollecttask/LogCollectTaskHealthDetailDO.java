package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

@Data
public class LogCollectTaskHealthDetailDO  extends BaseDO {

    private Long id;

    private Long logCollectTaskId;

    private Long pathId;

    private String hostName;

    private Long collectDqualityTime;

    private Long tooLargeTruncateCheckHealthyHeartbeatTime;

    private Long filePathExistsCheckHealthyHeartbeatTime;

    private Long fileDisorderCheckHealthyHeartbeatTime;

    private Long logSliceCheckHealthyHeartbeatTime;

}
