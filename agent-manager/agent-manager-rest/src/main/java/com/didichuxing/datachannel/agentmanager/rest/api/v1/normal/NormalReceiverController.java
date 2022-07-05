package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.receiver.ReceiverPaginationRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.receiver.ReceiverVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.receiver.ReceiverTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.RemoteKafkaClusterService;
import com.didichuxing.datachannel.agentmanager.thirdpart.kafkacluster.extension.KafkaClusterManageServiceExtension;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Api(tags = "Normal-Receiver维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "receivers")
public class NormalReceiverController {

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private KafkaClusterManageServiceExtension kafkaClusterManageServiceExtension;

    @Autowired
    private RemoteKafkaClusterService remoteKafkaClusterService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private AgentManageService agentManageService;

    @ApiOperation(value = "查询接收端", notes = "")
    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    @ResponseBody
    public Result<PaginationResult<ReceiverVO>> listReceivers(@RequestBody ReceiverPaginationRequestDTO dto) {
        ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDO = receiverPaginationRequestDTO2ReceiverPaginationQueryConditionDO(dto);
        List<ReceiverVO> receiverVOList = receiverDOList2ReceiverVOList(kafkaClusterManageService.paginationQueryByCondition(receiverPaginationQueryConditionDO));
        PaginationResult<ReceiverVO> paginationResult = new PaginationResult<>(receiverVOList, kafkaClusterManageService.queryCountByCondition(receiverPaginationQueryConditionDO), dto.getPageNo(), dto.getPageSize());
        return Result.buildSucc(paginationResult);
    }

    @ApiOperation(value = "查询系统全量接收端信息", notes = "")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<ReceiverVO>> list() {
        List<ReceiverDO> receiverDOList = kafkaClusterManageService.list();
        return Result.buildSucc(receiverDOList2ReceiverVOList(receiverDOList));
    }

    @ApiOperation(value = "根据接收端id获取该接收端对应kafka集群的所有topic列表", notes = "")
    @RequestMapping(value = "/topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<Set<String>> listTopics(
            @RequestParam(value = "brokerServers") String brokerServers
    ) {
        Set<String> topics = kafkaClusterManageServiceExtension.listTopics(brokerServers);
        return Result.buildSucc(topics);
    }

    @ApiOperation(value = "根据id查询对应KafkaCluster对象是否关联LogCollectTask true：存在 false：不存在", notes = "")
    @RequestMapping(value = "/rela-logcollecttask-exists/{ids}", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> relaLogCollectTaskExists(@PathVariable String ids) {
        String[] idArray = ids.split(",");
        if(null != idArray && idArray.length != 0) {
            for (String id : idArray) {
                Long kafkaClusterId = Long.valueOf(id);
                if(CollectionUtils.isNotEmpty(logCollectTaskManageService.getLogCollectTaskListByKafkaClusterId(kafkaClusterId))) {
                    return Result.buildSucc(Boolean.TRUE);
                }
            }
        }
        return Result.buildSucc(Boolean.FALSE);
    }

    @ApiOperation(value = "根据id查询对应KafkaCluster对象是否关联Agent true：存在 false：不存在", notes = "")
    @RequestMapping(value = "/rela-agent-exists/{ids}", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> relaAgentExists(@PathVariable String ids) {
        String[] idArray = ids.split(",");
        if(null != idArray && idArray.length != 0) {
            for (String id : idArray) {
                Long kafkaClusterId = Long.valueOf(id);
                if(CollectionUtils.isNotEmpty(agentManageService.getAgentListByKafkaClusterId(kafkaClusterId))) {
                    return Result.buildSucc(Boolean.TRUE);
                }
            }
        }
        return Result.buildSucc(Boolean.FALSE);
    }

    @ApiOperation(value = "校验是否已配置系统全局 agent errorlogs & metrics 流对应配置 true：是 false：否", notes = "")
    @RequestMapping(value = "/global-agent-errorlogs-metrics-receiver-exists", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> globalAgentErrorlogsAndMetricsReceiverExists() {
        ReceiverDO agentErrorLogsTopicExistsReceiver = kafkaClusterManageService.getAgentErrorLogsTopicExistsReceiver();
        ReceiverDO agentMetricsTopicExistsReceiver = kafkaClusterManageService.getAgentMetricsTopicExistsReceiver();
        if(null != agentErrorLogsTopicExistsReceiver && null != agentMetricsTopicExistsReceiver) {
            return Result.buildSucc(Boolean.TRUE);
        } else {
            return Result.buildSucc(Boolean.FALSE);
        }
    }

    /**
     * 将ReceiverDO对象集转化为ReceiverVO对象集
     * @param receiverDOList 待转化ReceiverDO对象集
     * @return 返回将ReceiverDO对象集转化为ReceiverVO的对象集
     */
    private List<ReceiverVO> receiverDOList2ReceiverVOList(List<ReceiverDO> receiverDOList) {
        List<ReceiverVO> receiverVOList = new ArrayList<>(receiverDOList.size());
        for (ReceiverDO receiverDO : receiverDOList) {
            ReceiverVO receiverVO = new ReceiverVO();
            receiverVO.setCreateTime(receiverDO.getCreateTime().getTime());
            receiverVO.setId(receiverDO.getId());
            receiverVO.setKafkaClusterBrokerConfiguration(receiverDO.getKafkaClusterBrokerConfiguration());
            receiverVO.setKafkaClusterName(receiverDO.getKafkaClusterName());
            receiverVO.setKafkaClusterProducerInitConfiguration(receiverDO.getKafkaClusterProducerInitConfiguration());
            receiverVO.setAgentErrorLogsTopic(receiverDO.getAgentErrorLogsTopic());
            receiverVO.setAgentMetricsTopic(receiverDO.getAgentMetricsTopic());
            receiverVO.setReceiverType(ReceiverTypeEnum.fromCode(receiverDO.getReceiverType()).getDescription());
            receiverVOList.add(receiverVO);
        }
        return receiverVOList;
    }

    /**
     * 将分页查询对象ReceiverPaginationRequestDTO转化为服务层分页查询对象ReceiverPaginationQueryConditionDO
     * @param dto 待转化分页查询对象ReceiverPaginationRequestDTO
     * @return 返回将分页查询对象ReceiverPaginationRequestDTO转化的服务层分页查询对象ReceiverPaginationQueryConditionDO
     */
    private ReceiverPaginationQueryConditionDO receiverPaginationRequestDTO2ReceiverPaginationQueryConditionDO(ReceiverPaginationRequestDTO dto) {
        ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDO = new ReceiverPaginationQueryConditionDO();
        if(StringUtils.isNotBlank(dto.getKafkaClusterName())) {
            receiverPaginationQueryConditionDO.setKafkaClusterName(dto.getKafkaClusterName().replace("_", "\\_").replace("%", "\\%"));
        }
        if(null != dto.getReceiverCreateTimeEnd()) {
            receiverPaginationQueryConditionDO.setCreateTimeEnd(new Date(dto.getReceiverCreateTimeEnd()));
        }
        if(null != dto.getReceiverCreateTimeStart()) {
            receiverPaginationQueryConditionDO.setCreateTimeStart(new Date(dto.getReceiverCreateTimeStart()));
        }
        if(StringUtils.isNotBlank(dto.getKafkaClusterBrokerConfiguration())) {
            receiverPaginationQueryConditionDO.setKafkaClusterBrokerConfiguration(dto.getKafkaClusterBrokerConfiguration().replace("_", "\\_").replace("%", "\\%"));
        }
        receiverPaginationQueryConditionDO.setLimitFrom(dto.getLimitFrom());
        receiverPaginationQueryConditionDO.setLimitSize(dto.getLimitSize());
        receiverPaginationQueryConditionDO.setSortColumn(dto.getSortColumn());
        receiverPaginationQueryConditionDO.setAsc(dto.getAsc());
        return receiverPaginationQueryConditionDO;
    }

}
