package com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metadata.MetaDataFilePO;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import lombok.Data;

/**
 * 元数据文件
 */
@Data
public class MetaDataFileDO extends BaseDO {

    private Long id;

    private String fileName;

    private String filePath;

    private String fileMd5;

    private String description;

    public MetaDataFilePO convert2MetaDataFilePO() {
        return ConvertUtil.obj2Obj(this, MetaDataFilePO.class);
    }
}
