package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.didichuxing.datachannel.agentmanager.common.annotation.CheckPermission;
import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.version.AgentVersionDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.version.AgentVersionPaginationRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.version.AgentVersionPaginationRecordVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.didichuxing.datachannel.agentmanager.common.constant.PermissionConstant.*;

@Api(tags = "OP-AgentVersion维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "version")
public class OpAgentVersionController {

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @ApiOperation(value = "新增Agent版本", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    @CheckPermission(permission = AGENT_VERSION_ADD)
    public Result<Long> createAgentVersion(AgentVersionDTO dto) {
        return Result.buildSucc(agentVersionManageService.createAgentVersion(dto, SpringTool.getUserName()));
    }

    @ApiOperation(value = "修改Agent版本", notes = "")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    @CheckPermission(permission = AGENT_VERSION_EDIT)
    public Result updateAgentVersion(AgentVersionDTO dto) {
        agentVersionManageService.updateAgentVersion(dto, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "删除Agent版本 0：删除成功 29000：待删除 AgentVersion 在系统中不存在 29002：AgentVersion在系统中存在关联，无法删除", notes = "")
    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    @CheckPermission(permission = AGENT_VERSION_DELETE)
    public Result deleteAgentVersion(@PathVariable String ids) {
        String[] idArray = ids.split(",");
        if(null != idArray && idArray.length != 0) {
            List<Long> agentVersionIdList = new ArrayList<>(idArray.length);
            for (String id : idArray) {
                agentVersionIdList.add(Long.valueOf(id));
            }
            agentVersionManageService.deleteAgentVersion(agentVersionIdList, SpringTool.getUserName());
        }
        return Result.buildSucc();
    }

    @ApiOperation(value = "下载Agent版本", notes = "")
    @RequestMapping(value = "/{agentVersionId}", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> downloadAgentVersion(@PathVariable Long agentVersionId, HttpServletResponse response) {
        Result<String> result = Result.buildSucc();
        result.setData(agentVersionManageService.getAgentInstallFileDownloadUrl(agentVersionId));
        return result;
    }

    @ApiOperation(value = "查询Agent版本列表", notes = "")
    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    @ResponseBody
    public Result<PaginationResult<AgentVersionPaginationRecordVO>> listAgentOperationTask(@RequestBody AgentVersionPaginationRequestDTO dto) {
        AgentVersionPaginationQueryConditionDO agentVersionPaginationQueryConditionDO = agentVersionPaginationRequestDTO2AgentVersionPaginationQueryConditionDO(dto);
        List<AgentVersionPaginationRecordVO> agentVersionPaginationRecordVOList = agentVersionDOList2AgentVersionPaginationRecordVOList(agentVersionManageService.paginationQueryByConditon(agentVersionPaginationQueryConditionDO));
        PaginationResult<AgentVersionPaginationRecordVO> paginationResult = new PaginationResult<>(agentVersionPaginationRecordVOList, agentVersionManageService.queryCountByCondition(agentVersionPaginationQueryConditionDO), dto.getPageNo(), dto.getPageSize());
        return Result.buildSucc(paginationResult);
    }

    /**
     * 将给定 AgentVersionDO 对象集转化为 AgentVersionPaginationRecordVO 对象集
     * @param agentVersionDOList 待转化 AgentVersionDO 对象集
     * @return 返回将给定 AgentVersionDO 对象集转化为的 AgentVersionPaginationRecordVO 对象集
     */
    private List<AgentVersionPaginationRecordVO> agentVersionDOList2AgentVersionPaginationRecordVOList(List<AgentVersionDO> agentVersionDOList) {
        List<AgentVersionPaginationRecordVO> agentVersionPaginationRecordVOList = new ArrayList<>(agentVersionDOList.size());
        for (AgentVersionDO agentVersionDO : agentVersionDOList) {
            AgentVersionPaginationRecordVO agentVersionPaginationRecordVO = new AgentVersionPaginationRecordVO();
            agentVersionPaginationRecordVO.setAgentPackageName(agentVersionDO.getFileName());
            agentVersionPaginationRecordVO.setAgentVersion(agentVersionDO.getVersion());
            agentVersionPaginationRecordVO.setAgentVersionDescription(agentVersionDO.getDescription());
            agentVersionPaginationRecordVO.setAgentVersionId(agentVersionDO.getId());
            agentVersionPaginationRecordVO.setFileMd5(agentVersionDO.getFileMd5());
            agentVersionPaginationRecordVO.setCreateTime(agentVersionDO.getCreateTime().getTime());
            agentVersionPaginationRecordVOList.add(agentVersionPaginationRecordVO);
        }
        return agentVersionPaginationRecordVOList;
    }

    /**
     * 将给定 AgentVersionPaginationRequestDTO 对象转化为 AgentVersionPaginationQueryConditionDO 对象
     * @param dto 待转化 AgentVersionPaginationRequestDTO 对象
     * @return 返回将给定 AgentVersionPaginationRequestDTO 对象转化为的 AgentVersionPaginationQueryConditionDO 对象
     */
    private AgentVersionPaginationQueryConditionDO agentVersionPaginationRequestDTO2AgentVersionPaginationQueryConditionDO(AgentVersionPaginationRequestDTO dto) {
        AgentVersionPaginationQueryConditionDO agentVersionPaginationQueryConditionDO = new AgentVersionPaginationQueryConditionDO();
        if(StringUtils.isNotBlank(dto.getAgentPackageName())) {
            agentVersionPaginationQueryConditionDO.setAgentPackageName(dto.getAgentPackageName().replace("_", "\\_").replace("%", "\\%"));
        }
        if(CollectionUtils.isNotEmpty(dto.getAgentVersionList())) {
            agentVersionPaginationQueryConditionDO.setAgentVersionList(dto.getAgentVersionList());
        }
        if(null != dto.getAgentVersionCreateTimeEnd()) {
            agentVersionPaginationQueryConditionDO.setAgentVersionCreateTimeEnd(new Date(dto.getAgentVersionCreateTimeEnd()));
        }
        if(null != dto.getAgentVersionCreateTimeStart()) {
            agentVersionPaginationQueryConditionDO.setAgentVersionCreateTimeStart(new Date(dto.getAgentVersionCreateTimeStart()));
        }
        if(null != dto.getQueryTerm()) {
            agentVersionPaginationQueryConditionDO.setQueryTerm(dto.getQueryTerm());
        }
        if(StringUtils.isNotBlank(dto.getAgentVersionDescription())) {
            agentVersionPaginationQueryConditionDO.setAgentVersionDescription(dto.getAgentVersionDescription().replace("_", "\\_").replace("%", "\\%"));
        }
        agentVersionPaginationQueryConditionDO.setLimitFrom(dto.getLimitFrom());
        agentVersionPaginationQueryConditionDO.setLimitSize(dto.getLimitSize());
        agentVersionPaginationQueryConditionDO.setSortColumn(dto.getSortColumn());
        agentVersionPaginationQueryConditionDO.setAsc(dto.getAsc());
        return agentVersionPaginationQueryConditionDO;
    }

}
