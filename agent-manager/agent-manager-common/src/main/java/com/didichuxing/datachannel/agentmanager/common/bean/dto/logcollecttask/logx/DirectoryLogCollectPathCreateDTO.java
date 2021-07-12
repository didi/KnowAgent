package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.logx;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "目录类型路径采集配置", description = "")
public class DirectoryLogCollectPathCreateDTO extends LogCollectPathCreateDTO {

    @ApiModelProperty(value = "目录采集深度")
    private Integer directoryCollectDepth;

    @ApiModelProperty(value = "存储有序的文件筛选规则集。pair.key：表示黑/白名单类型，0：白名单，1：黑名单；pair.value：表示过滤规则对应正则表达式")
    private List<Pair<Integer, String>> filterRuleChain;

    public void setFilterRuleChain(List<Pair<Integer, String>> filterRuleChain) {
        this.filterRuleChain = filterRuleChain;
    }

    public List<Pair<Integer, String>> getFilterRuleChain() {
        return filterRuleChain;
    }

    public void setDirectoryCollectDepth(Integer directoryCollectDepth) {
        this.directoryCollectDepth = directoryCollectDepth;
    }

    public Integer getDirectoryCollectDepth() {
        return directoryCollectDepth;
    }
}
