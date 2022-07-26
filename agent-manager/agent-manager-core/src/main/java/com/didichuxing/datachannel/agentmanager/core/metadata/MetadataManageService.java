package com.didichuxing.datachannel.agentmanager.core.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileContent;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFilePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata.MetadataFileDTO;
import java.util.List;

public interface MetadataManageService {

    /**
     * 添加 metadata excel 文件 & 描述信息，返回元数据上传记录 id
     * @param dto MetadataFileDTO 对象
     * @param operator 操作人
     * @return 创建成功的元数据上传记录 id
     */
    Long addMetadataFile(MetadataFileDTO dto, String operator);

    /**
     * 根据元数据上传记录 id 删除对应 metadata excel 文件上传记录
     * @param id 元数据上传记录 id
     */
    void deleteMetaDataFile(Long id);

    /**
     * 根据元数据上传记录 id 获取对应元数据上传文件内容
     * @param id 元数据上传记录 id
     * @return 返回根据元数据上传记录 id 获取到的对应元数据上传文件内容
     */
    MetaDataFileContent getMetaDataFileContent(Long id);

    /**
     * 分页查询
     * @param metaDataFilePaginationQueryConditionDO 查询条件 MetaDataFilePaginationQueryConditionDO 对象
     * @return 分页查询结果
     */
    List<MetaDataFileDO> paginationQueryByCondition(MetaDataFilePaginationQueryConditionDO metaDataFilePaginationQueryConditionDO);

    /**
     * 查询结果集数
     * @param metaDataFilePaginationQueryConditionDO 查询条件 MetaDataFilePaginationQueryConditionDO 对象
     * @return 查询结果集数
     */
    Integer queryCountByCondition(MetaDataFilePaginationQueryConditionDO metaDataFilePaginationQueryConditionDO);

    /**
     * 导入元数据信息
     * @param id 元数据上传记录 id
     * @param operator 操作人
     */
    void importMetaData(Long id, String operator);

}
