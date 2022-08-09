package com.didichuxing.datachannel.agentmanager.core.agent.version.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.version.AgentVersionDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.version.AgentVersionPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import com.didichuxing.datachannel.agentmanager.core.common.OperateRecordService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.AgentVersionMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.version.AgentVersionManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Service
public class AgentVersionManageServiceImpl implements AgentVersionManageService {

    @Autowired
    private AgentVersionManageServiceExtension agentVersionManageServiceExtension;

    @Autowired
    private AgentVersionMapper agentVersionDAO;

    @Autowired
    private AgentManageService agentManageService;

    @Override
    @Transactional
    public Long createAgentVersion(AgentVersionDTO agentVersionDTO, String operator) {
        /*
         * 参数校验
         */
        CheckResult checkResult = agentVersionManageServiceExtension.checkCreateParameterAgentVersion(agentVersionDTO);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 校验agent安装包文件在系统是否存在
         */
        AgentVersionPO agentVersionPO = agentVersionDAO.selectByfileMd5(agentVersionDTO.getFileMd5());
        if(null != agentVersionPO) {
            throw new ServiceException(
                    String.format("Agent安装文件={fileMd5=%s}在系统中已存在", agentVersionDTO.getFileMd5()),
                    ErrorCodeEnum.AGENT_PACKAGE_FILE_EXISTS.getCode()
            );
        }
        /*
         * 上传文件
         */
        uploadAgentFile(agentVersionDTO);
        /*
         * 添加 AgentVersion 对象
         */
        return this.handleCreateAgentVersion(agentVersionDTO, operator);
    }

    @Override
    @Transactional
    public void updateAgentVersion(AgentVersionDTO agentVersionDTO, String operator) {
        /*
         * 参数校验
         */
        CheckResult checkResult = agentVersionManageServiceExtension.checkUpdateParameterAgentVersion(agentVersionDTO);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 修改 AgentVersion 对象
         */
        this.handleUpdateAgentVersion(agentVersionDTO, operator);//创建 agent 对象
    }

    @Override
    @Transactional
    public void deleteAgentVersion(List<Long> agentVersionIdList, String operator) {
        for (Long agentVersionId : agentVersionIdList) {
            handleDeleteAgentVersion(agentVersionId, operator);
        }
    }

    @Override
    public List<AgentVersionDO> paginationQueryByConditon(AgentVersionPaginationQueryConditionDO query) {
        String column = query.getSortColumn();
        if (column != null) {
            for (char c : column.toCharArray()) {
                if (!Character.isLetter(c) && c != '_') {
                    return Collections.emptyList();
                }
            }
        }
        List<AgentVersionPO> agentVersionPOList = agentVersionDAO.paginationQueryByConditon(query);
        if(CollectionUtils.isEmpty(agentVersionPOList)) {
            return Collections.emptyList();
        }
        return agentVersionManageServiceExtension.agentVersionPOList2AgentVersionDOList(agentVersionPOList);
    }

    @Override
    public Integer queryCountByCondition(AgentVersionPaginationQueryConditionDO agentVersionPaginationQueryConditionDO) {
        return agentVersionDAO.queryCountByConditon(agentVersionPaginationQueryConditionDO);
    }

    @Override
    public String getAgentInstallFileDownloadUrl(Long agentVersionId) {
        AgentVersionDO agentVersionDO = getById(agentVersionId);
        if(null == agentVersionDO) {
            throw new ServiceException(
                    String.format("AgentVersion={id=%d}在系统中不存在", agentVersionId),
                    ErrorCodeEnum.AGENT_VERSION_NOT_EXISTS.getCode()
            );
        }
        String fileName = agentVersionDO.getFileName();
        String fileMd5 = agentVersionDO.getFileMd5();
        String downloadUrl = getDownloadUrl(fileName, fileMd5);
        return downloadUrl;
    }

    private String getDownloadUrl(String fileName, String fileMd5) {
        /*
         * TODO：
         */
        return null;
    }

    /**
     * 根据 id 删除给定 agentVersion 对象
     * @param agentVersionId 待删除 agentVersion 对象 id 值
     * @param operator 操作人
     * @return 删除是否成功
     * @throws ServiceException 执行该函数操作过程中出现的异常
     */
    private void handleDeleteAgentVersion(Long agentVersionId, String operator) throws ServiceException {
        /*
         * 校验待删除 agentVersion 在系统中是否存在
         */
        AgentVersionDO agentVersionDO = getById(agentVersionId);
        if(null == agentVersionDO) {
            throw new ServiceException(
                    "删除失败：待删除 Agent 版本在系统中不存在",
                    ErrorCodeEnum.AGENT_VERSION_NOT_EXISTS.getCode()
            );
        }
        /*
         * 校验待删除 agentVersion 是否被 agent 所引用
         */
        List<AgentDO> relationAgentDOList = agentManageService.getAgentsByAgentVersionId(agentVersionId);
        if(CollectionUtils.isNotEmpty(relationAgentDOList)) {
            throw new ServiceException(
                    String.format("删除失败：待删除 Agent 版本在系统中存在%d个关联的 Agent", relationAgentDOList.size()),
                    ErrorCodeEnum.AGENT_VERSION_RELATION_EXISTS.getCode()
            );
        }
        /*
         * 删除 agentVersion
         */
        agentVersionDAO.deleteByPrimaryKey(agentVersionId);
        /*
         * 添加对应操作记录
         */
        /*operateRecordService.save(
                ModuleEnum.AGENT_VERSION,
                OperationEnum.DELETE,
                agentVersionId,
                String.format("删除KafkaCluster对象={id={%d}}", agentVersionId),
                operator
        );*/
    }

    @Override
    public AgentVersionDO getByVersion(String version) {
        AgentVersionPO agentVersionPO = agentVersionDAO.selectByVersion(version);
        if(agentVersionPO == null) {
            return null;
        }
        return agentVersionManageServiceExtension.agentVersionPO2AgentVersionDO(agentVersionPO);
    }

    @Override
    public AgentVersionDO getById(Long agentVersionId) {
        AgentVersionPO agentVersionPO = agentVersionDAO.selectByPrimaryKey(agentVersionId);
        if(agentVersionPO == null) {
            return null;
        }
        return agentVersionManageServiceExtension.agentVersionPO2AgentVersionDO(agentVersionPO);
    }

    @Override
    public List<AgentVersionDO> list() {
        List<AgentVersionPO> agentVersionPOList = agentVersionDAO.selectAll();
        if(CollectionUtils.isEmpty(agentVersionPOList)) {
            return new ArrayList<>();
        }
        return agentVersionManageServiceExtension.agentVersionPOList2AgentVersionDOList(agentVersionPOList);
    }

    /**
     * 创建 AgentVersion
     * @param agentVersionDTO 待创建 AgentVersion 对象
     * @param operator 操作人
     * @return 创建的AgentVersion对象 id
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateAgentVersion(AgentVersionDTO agentVersionDTO, String operator) throws ServiceException {
        /*
         * 校验待创建AgentVersion对应version在系统中是否已存在
         */
        String agentVersion = agentVersionDTO.getAgentVersion();
        AgentVersionDO agentVersionDO = getByVersion(agentVersion);
        if(null != agentVersionDO) {
            throw new ServiceException(
                    String.format("待创建AgentVersion对应version={%s}在系统中已存在", agentVersion),
                    ErrorCodeEnum.AGENT_VERSION_DUPLICATE.getCode()
            );
        }
        /*
         * 创建AgentVersion
         */
        AgentVersionPO agentVersionPO = agentVersionManageServiceExtension.AgentVersionDTO2AgentVersionPO(agentVersionDTO);
        agentVersionPO.setOperator(CommonConstant.getOperator(operator));
        agentVersionDAO.insert(agentVersionPO);
        /*
         * 添加对应操作记录
         */
        /*operateRecordService.save(
                ModuleEnum.AGENT_VERSION,
                OperationEnum.ADD,
                agentVersionPO.getId(),
                String.format("创建AgentVersion={%s}，创建成功的AgentVersion对象id={%d}", JSON.toJSONString(agentVersionPO), agentVersionPO.getId()),
                operator
        );*/
        return agentVersionPO.getId();
    }

    /**
     * 上传agent安装文件
     * @param agentVersionDTO 封装agent安装文件的AgentVersionDTO对象
     * @throws ServiceException 执行上传操作过程中出现的异常
     */
    private void uploadAgentFile(AgentVersionDTO agentVersionDTO) throws ServiceException {
        try {
            if (upload(
                    agentVersionDTO.getAgentPackageName(),
                    agentVersionDTO.getFileMd5(),
                    agentVersionDTO.getUploadFile())
            ) {
                throw new ServiceException(
                        String.format("文件={%s}上传出错", agentVersionDTO.getAgentPackageName()),
                        ErrorCodeEnum.FILE_UPLOAD_FAILED.getCode()
                );
            }
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format("文件={%s}上传出错,原因为：%s", agentVersionDTO.getAgentPackageName(), ex.getMessage()),
                    ErrorCodeEnum.FILE_UPLOAD_FAILED.getCode()
            );
        }
    }

    private boolean upload(String agentPackageName, String fileMd5, MultipartFile uploadFile) {
        /*
         * TODO：
         */
        return false;
    }

    /**
     * 更新给定AgentVersionDTO对象
     * @param agentVersionDTO 待更新AgentVersionDTO对象
     * @param operator 操作人
     * @throws ServiceException 执行该操作过程中出现的异常
     */
    private void handleUpdateAgentVersion(AgentVersionDTO agentVersionDTO, String operator) throws ServiceException {
        Long agentVersionId = agentVersionDTO.getId();
        AgentVersionPO agentVersionPO = agentVersionDAO.selectByPrimaryKey(agentVersionId);
        if(null == agentVersionPO) {
            throw new ServiceException(
                    String.format("待更新AgentVersion对象={id=%d}在系统中不存在", agentVersionId),
                    ErrorCodeEnum.AGENT_VERSION_NOT_EXISTS.getCode()
            );
        }
        agentVersionPO.setDescription(agentVersionDTO.getAgentVersionDescription());
        agentVersionPO.setOperator(CommonConstant.getOperator(operator));
        agentVersionDAO.updateByPrimaryKey(agentVersionPO);
        /*
         * 添加对应操作记录
         */
        /*operateRecordService.save(
                ModuleEnum.AGENT_VERSION,
                OperationEnum.EDIT,
                agentVersionDTO.getId(),
                String.format("修改AgentVersion={%s}，修改成功的AgentVersion对象id={%d}", JSON.toJSONString(agentVersionPO), agentVersionDTO.getId()),
                operator
        );*/
    }

}
