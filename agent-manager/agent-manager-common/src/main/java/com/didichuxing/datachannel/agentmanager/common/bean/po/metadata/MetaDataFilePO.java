package com.didichuxing.datachannel.agentmanager.common.bean.po.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

@Data
public class MetaDataFilePO extends BasePO {

    private Long id;

    private String fileName;

    private String filePath;

    private String fileMd5;

    private String description;

}
