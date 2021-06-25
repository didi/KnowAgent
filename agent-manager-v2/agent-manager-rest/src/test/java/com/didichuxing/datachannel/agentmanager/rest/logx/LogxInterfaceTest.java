package com.didichuxing.datachannel.agentmanager.rest.logx;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.logx.*;
import com.didichuxing.datachannel.agentmanager.common.util.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogxInterfaceTest {

    public static void main(String[] args) throws InterruptedException {

//        String createRequestUrl = "http://localhost:8006/api/v1/logx/collect-task";
//        LogCollectTaskCreateDTO logCollectTaskCreateDTO = new LogCollectTaskCreateDTO();
//        logCollectTaskCreateDTO.setServiceId(96L);
//        logCollectTaskCreateDTO.setKafkaClusterId(1L);
//        logCollectTaskCreateDTO.setLogCollectTaskName("logx采集任务");
//        logCollectTaskCreateDTO.setSendTopic("topic01");
//        List<FileLogCollectPathCreateDTO> fileLogCollectPathList = new ArrayList<>();
//        FileLogCollectPathCreateDTO fileLogCollectPathUpdateDTO = new FileLogCollectPathCreateDTO();
//        fileLogCollectPathUpdateDTO.setPath("/var/log/messages");//TODO：
//        FileNameSuffixMatchRuleDTO fileNameSuffixMatchRuleDTO = new FileNameSuffixMatchRuleDTO();
//        fileNameSuffixMatchRuleDTO.setSuffixMatchRegular(".\\d+");
//        fileLogCollectPathUpdateDTO.setFileNameSuffixMatchRuleDTO(fileNameSuffixMatchRuleDTO);
//        LogSliceRuleDTO logSliceRuleDTO = new LogSliceRuleDTO();
//        logSliceRuleDTO.setSliceTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        logSliceRuleDTO.setSliceTimestampPrefixString("");
//        logSliceRuleDTO.setSliceTimestampPrefixStringIndex(0);
//        fileLogCollectPathUpdateDTO.setLogSliceRuleDTO(logSliceRuleDTO);
//        fileLogCollectPathList.add(fileLogCollectPathUpdateDTO);
//        logCollectTaskCreateDTO.setFileLogCollectPathList(fileLogCollectPathList);
//        String content = JSON.toJSONString(logCollectTaskCreateDTO);
//        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type", "application/json");
//        System.err.println(content);
//        String response = HttpUtils.postForString(createRequestUrl, content, header);
//        System.err.println(response);
//        Thread.sleep(99999L);


        LogCollectTaskUpdateDTO logCollectTaskUpdateDTO = new LogCollectTaskUpdateDTO();
        logCollectTaskUpdateDTO.setServiceId(95L);
        logCollectTaskUpdateDTO.setKafkaClusterId(1L);
        logCollectTaskUpdateDTO.setLogCollectTaskName("logx采集任务_update");
        logCollectTaskUpdateDTO.setSendTopic("topic02");
        logCollectTaskUpdateDTO.setId(1030L);
        List<FileLogCollectPathUpdateDTO> fileLogCollectPathList = new ArrayList<>();
        FileLogCollectPathUpdateDTO fileLogCollectPathUpdateDTO = new FileLogCollectPathUpdateDTO();
        fileLogCollectPathUpdateDTO.setPath("/var/log/messages1");//TODO：
        FileNameSuffixMatchRuleDTO fileNameSuffixMatchRuleDTO = new FileNameSuffixMatchRuleDTO();
        fileNameSuffixMatchRuleDTO.setSuffixMatchRegular(".\\d+0");
        fileLogCollectPathUpdateDTO.setFileNameSuffixMatchRuleDTO(fileNameSuffixMatchRuleDTO);
        LogSliceRuleDTO logSliceRuleDTO = new LogSliceRuleDTO();
        logSliceRuleDTO.setSliceTimestampFormat("yyyy-MM-dd HH:mm:ss.SSS");
        logSliceRuleDTO.setSliceTimestampPrefixString("abc]");
        logSliceRuleDTO.setSliceTimestampPrefixStringIndex(9);
        fileLogCollectPathUpdateDTO.setLogSliceRuleDTO(logSliceRuleDTO);
        fileLogCollectPathList.add(fileLogCollectPathUpdateDTO);
        logCollectTaskUpdateDTO.setFileLogCollectPathList(fileLogCollectPathList);
        String content = JSON.toJSONString(logCollectTaskUpdateDTO);
        System.err.println(content);

    }

}
