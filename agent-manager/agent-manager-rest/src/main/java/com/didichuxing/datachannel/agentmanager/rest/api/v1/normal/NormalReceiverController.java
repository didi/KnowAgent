package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.receiver.ReceiverPaginationRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.receiver.ReceiverVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.RemoteKafkaClusterService;
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
    private RemoteKafkaClusterService remoteKafkaClusterService;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private AgentManageService agentManageService;

    @ApiOperation(value = "查询接收端", notes = "")
    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    @ResponseBody
    // @CheckPermission(permission = AGENT_KAFKA_CLUSTER_LIST)
    public Result<PaginationResult<ReceiverVO>> listReceivers(@RequestBody ReceiverPaginationRequestDTO dto) {
        ReceiverPaginationQueryConditionDO receiverPaginationQueryConditionDO = receiverPaginationRequestDTO2ReceiverPaginationQueryConditionDO(dto);
        List<ReceiverVO> receiverVOList = receiverDOList2ReceiverVOList(kafkaClusterManageService.paginationQueryByConditon(receiverPaginationQueryConditionDO));
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
    @RequestMapping(value = "/{receiverId}/topics", method = RequestMethod.GET)
    @ResponseBody
    public Result<Set<String>> listTopics(@PathVariable Long receiverId) {
        ReceiverDO receiverDO = kafkaClusterManageService.getById(receiverId);
        if(null == receiverDO) {
            return Result.build(
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_EXISTS.getCode(),
                    String.format("接收端对应Kafka集群{id=%d}在系统中不存在", receiverId)
            );
        }
        if(null != receiverDO.getKafkaClusterId()) {
            return Result.buildSucc(remoteKafkaClusterService.getTopicsByKafkaClusterId(receiverDO.getKafkaClusterId()));
        } else {//case：kafka集群由用户维护而非通过kafka-manager获取
            return Result.build(
                    ErrorCodeEnum.SYSTEM_NOT_SUPPORT.getCode(),
                    String.format("系统不支持！待获取topic列表信息的Kafka集群={id=%d}由用户维护，而非通过Kafka-Manager系统同步获得，系统仅对通过Kafka-Manager系统同步过来的Kafka集群提供该接口支持！", receiverId)
            );
        }
    }

    @ApiOperation(value = "根据id查询对应KafkaCluster对象是否关联LogCollectTask true：存在 false：不存在", notes = "")
    @RequestMapping(value = "/rela-logcollecttask-exists/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> relaLogCollectTaskExists(@PathVariable Long id) {
        if(CollectionUtils.isNotEmpty(logCollectTaskManageService.getLogCollectTaskListByKafkaClusterId(id))) {
            return Result.buildSucc(Boolean.TRUE);
        } else {
            return Result.buildSucc(Boolean.FALSE);
        }
    }

    @ApiOperation(value = "根据id查询对应KafkaCluster对象是否关联Agent true：存在 false：不存在", notes = "")
    @RequestMapping(value = "/rela-agent-exists/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> relaAgentExists(@PathVariable Long id) {
        if(CollectionUtils.isNotEmpty(agentManageService.getAgentListByKafkaClusterId(id))) {
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
        receiverPaginationQueryConditionDO.setLimitFrom(dto.getLimitFrom());
        receiverPaginationQueryConditionDO.setLimitSize(dto.getLimitSize());
        receiverPaginationQueryConditionDO.setSortColumn(dto.getSortColumn());
        receiverPaginationQueryConditionDO.setAsc(dto.getAsc());
        return receiverPaginationQueryConditionDO;
    }

}
