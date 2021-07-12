package com.didichuxing.datachannel.agentmanager.thirdpart.agent.version.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.version.AgentVersionDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.version.AgentVersionPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentFileTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.version.AgentVersionManageServiceExtension;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@org.springframework.stereotype.Service
public class AgentVersionManageServiceExtensionImpl implements AgentVersionManageServiceExtension {

    @Override
    public CheckResult checkCreateParameterAgentVersion(AgentVersionDTO agentVersionDTO) {
        if(StringUtils.isBlank(agentVersionDTO.getAgentPackageName())) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参agentPackageName不可为空"
            );
        }
        if(StringUtils.isBlank(agentVersionDTO.getAgentVersion())) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参agentVersion不可为空"
            );
        }
        if(StringUtils.isBlank(agentVersionDTO.getFileMd5())) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参fileMd5不可为空"
            );
        }
        if(null == agentVersionDTO.getUploadFile()) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参uploadFile不可为空"
            );
        }
        return new CheckResult(true);
    }

    @Override
    public AgentVersionPO AgentVersionDTO2AgentVersionPO(AgentVersionDTO agentVersionDTO) {
        AgentVersionPO agentVersionPO = new AgentVersionPO();
        agentVersionPO.setVersion(agentVersionDTO.getAgentVersion());
        agentVersionPO.setFileType(AgentFileTypeEnum.AGENT_INSTALL_FILE.getCode());
        agentVersionPO.setFileName(agentVersionDTO.getAgentPackageName());
        agentVersionPO.setFileMd5(agentVersionDTO.getFileMd5());
        agentVersionPO.setDescription(agentVersionDTO.getAgentVersionDescription());
        return agentVersionPO;
    }

    @Override
    public AgentVersionDO agentVersionPO2AgentVersionDO(AgentVersionPO agentVersionPO) {
        try {
            AgentVersionDO agentVersionDO = ConvertUtil.obj2Obj(agentVersionPO, AgentVersionDO.class);
            return agentVersionDO;
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format("AgentVersionPO对象={%s}转化为AgentVersionDO对象失败，原因为：%s", JSON.toJSONString(agentVersionPO), ex.getMessage()),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    public List<AgentVersionDO> agentVersionPOList2AgentVersionDOList(List<AgentVersionPO> agentVersionPOList) {
        return ConvertUtil.list2List(agentVersionPOList, AgentVersionDO.class);
    }

    @Override
    public CheckResult checkUpdateParameterAgentVersion(AgentVersionDTO agentVersionDTO) {
        if(null == agentVersionDTO.getId()) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    "入参id不可为空"
            );
        }
        return new CheckResult(true);
    }

}
