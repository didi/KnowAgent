package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.service.ServicePaginationRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.host.HostVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.service.ServiceDetailVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.service.ServicePaginationRecordVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.service.ServiceVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.constant.ProjectConstant;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Api(tags = "Normal-Service维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "services")
public class NormalServiceController {

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @ApiOperation(value = "查询服务列表 & 各服务关联的主机数", notes = "")
    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    @ResponseBody
    public Result<PaginationResult<ServicePaginationRecordVO>> listServicesAndRelationHostCount(@RequestBody ServicePaginationRequestDTO dto) {
        ServicePaginationQueryConditionDO servicePaginationQueryConditionDO = servicePaginationRequestDTO2ServicePaginationQueryConditionDO(dto);
        List<ServicePaginationRecordVO> servicePaginationRecordVOList = servicePaginationRecordDOList2ServicePaginationRecordVOList(serviceManageService.paginationQueryByConditon(servicePaginationQueryConditionDO));
        PaginationResult<ServicePaginationRecordVO> paginationResult = new PaginationResult<>(servicePaginationRecordVOList, serviceManageService.queryCountByCondition(servicePaginationQueryConditionDO), dto.getPageNo(), dto.getPageSize());
        return Result.buildSucc(paginationResult);
    }

    private List<ServicePaginationRecordVO> servicePaginationRecordDOList2ServicePaginationRecordVOList(List<ServicePaginationRecordDO> servicePaginationRecordDOList) {
        List<ServicePaginationRecordVO> servicePaginationRecordVOList = new ArrayList<>(servicePaginationRecordDOList.size());
        for (ServicePaginationRecordDO servicePaginationRecordDO : servicePaginationRecordDOList) {
            ServicePaginationRecordVO servicePaginationRecordVO = new ServicePaginationRecordVO();
            servicePaginationRecordVO.setRelationHostCount(null == servicePaginationRecordDO.getRelationHostCount() ? 0 : servicePaginationRecordDO.getRelationHostCount());
            servicePaginationRecordVO.setServiceName(servicePaginationRecordDO.getServicename());
            servicePaginationRecordVO.setId(servicePaginationRecordDO.getId());
            servicePaginationRecordVO.setCreateTime(servicePaginationRecordDO.getCreateTime().getTime());
            servicePaginationRecordVOList.add(servicePaginationRecordVO);
        }
        return servicePaginationRecordVOList;
    }

    private ServicePaginationQueryConditionDO servicePaginationRequestDTO2ServicePaginationQueryConditionDO(ServicePaginationRequestDTO dto) {
        ServicePaginationQueryConditionDO servicePaginationQueryConditionDO = new ServicePaginationQueryConditionDO();
        servicePaginationQueryConditionDO.setLimitSize(dto.getLimitSize());
        servicePaginationQueryConditionDO.setLimitFrom(dto.getLimitFrom());
        if (null != dto.getServiceCreateTimeEnd()) {
            servicePaginationQueryConditionDO.setCreateTimeEnd(new Date(dto.getServiceCreateTimeEnd()));
        }
        if (null != dto.getServiceCreateTimeStart()) {
            servicePaginationQueryConditionDO.setCreateTimeStart(new Date(dto.getServiceCreateTimeStart()));
        }
        if (StringUtils.isNotBlank(dto.getServicename())) {
            servicePaginationQueryConditionDO.setServiceName(dto.getServicename().replace("_", "\\_").replace("%", "\\%"));
        }
        if(StringUtils.isNotBlank(dto.getQueryTerm())) {
            servicePaginationQueryConditionDO.setQueryTerm(dto.getQueryTerm());
        }
        servicePaginationQueryConditionDO.setSortColumn(dto.getSortColumn());
        servicePaginationQueryConditionDO.setAsc(dto.getAsc());
        return servicePaginationQueryConditionDO;
    }

    @ApiOperation(value = "查询\"服务名-服务id\"列表", notes = "")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    // @CheckPermission(permission = AGENT_APP_LIST)
    public Result<List<ServiceVO>> listService(HttpServletRequest httpServletRequest) {
        //TODO：获取 projectId
        String projectIdStr = httpServletRequest.getHeader(ProjectConstant.PROJECT_ID_KEY_IN_HTTP_REQUEST_HEADER);
        Long projectId = null;
        if(StringUtils.isNotBlank(projectIdStr)) {
            projectId = Long.valueOf(projectIdStr);
        }
        List<ServiceDO> serviceDOList = serviceManageService.getServicesByProjectId(projectId);
        return Result.buildSucc(ConvertUtil.list2List(serviceDOList, ServiceVO.class));
    }

    @ApiOperation(value = "根据id查询服务对象", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<ServiceDetailVO> get(@PathVariable Long id) {
        ServiceDO serviceDO = serviceManageService.getServiceById(id);
        if (null == serviceDO) {
            return Result.buildSucc(null);
        } else {
            ServiceDetailVO serviceDetailVO = new ServiceDetailVO();
            serviceDetailVO.setServiceName(serviceDO.getServicename());
            serviceDetailVO.setId(serviceDO.getId());
            List<HostDO> hostDOList = hostManageService.getHostsByServiceId(serviceDO.getId());
            if(CollectionUtils.isNotEmpty(hostDOList)) {
                List<HostVO> hostVOList = new ArrayList<>(hostDOList.size());
                for (HostDO hostDO : hostDOList) {
                    HostVO hostVO = ConvertUtil.obj2Obj(hostDO, HostVO.class);
                    hostVO.setContainer(hostDO.getContainer());
                    hostVOList.add(hostVO);
                }
                serviceDetailVO.setHostList(hostVOList);
            }
            serviceDetailVO.setCreateTime(serviceDO.getCreateTime().getTime());
            return Result.buildSucc(serviceDetailVO);
        }
    }

    @ApiOperation(value = "根据id查询对应Service对象是否关联LogCollectTask true：存在 false：不存在", notes = "")
    @RequestMapping(value = "/rela-logcollecttask-exists/{ids}", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> relaLogCollectTaskExists(@PathVariable String ids) {
        String[] idArray = ids.split(",");
        if(null != idArray && idArray.length != 0) {
            for (String id : idArray) {
                Long serviceId = Long.valueOf(id);
                if(CollectionUtils.isNotEmpty(logCollectTaskManageService.getLogCollectTaskListByServiceId(serviceId))) {
                    return Result.buildSucc(Boolean.TRUE);
                }
            }
        }
        return Result.buildSucc(Boolean.FALSE);
    }

    @ApiOperation(value = "根据id查询对应Service对象是否关联Host true：存在 false：不存在", notes = "")
    @RequestMapping(value = "/rela-host-exists/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> relaHostExists(@PathVariable Long id) {
        if (CollectionUtils.isNotEmpty(hostManageService.getHostsByServiceId(id))) {
            return Result.buildSucc(Boolean.TRUE);
        } else {
            return Result.buildSucc(Boolean.FALSE);
        }
    }

}
