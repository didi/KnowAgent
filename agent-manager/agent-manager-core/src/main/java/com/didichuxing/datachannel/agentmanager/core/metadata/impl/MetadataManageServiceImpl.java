package com.didichuxing.datachannel.agentmanager.core.metadata.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileContent;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFilePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata.MetadataFileDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metadata.MetaDataFilePO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.FileUtils;
import com.didichuxing.datachannel.agentmanager.core.metadata.MetadataManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.MetaDataFileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Service
public class MetadataManageServiceImpl implements MetadataManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataManageServiceImpl.class);

    @Autowired
    private MetaDataFileMapper metaDataFileDAO;

    @Override
    @Transactional
    public Long addMetadataFile(MetadataFileDTO dto, String operator) {
        /*
         * 参数校验
         */
        CheckResult checkResult = dto.checkParameter();
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(checkResult.getMessage(), checkResult.getCode());
        }
        /*
         * 上传文件
         */
        String filePath = FileUtils.upload(dto.getUploadFile(), dto.getFileMd5());
        /*
         * 构建 MetaDataFileDO 对象
         */
        MetaDataFileDO metaDataFileDO = new MetaDataFileDO();
        metaDataFileDO.setFileMd5(dto.getFileMd5());
        metaDataFileDO.setFileName(dto.getUploadFile().getOriginalFilename());
        metaDataFileDO.setFilePath(filePath);
        metaDataFileDO.setDescription(dto.getDescription());
        /*
         * 添加 MetadataFile 对象
         */
        return this.handleCreateMetaDataFile(metaDataFileDO, operator);
    }

    @Override
    @Transactional
    public void deleteMetaDataFile(Long id) {
        MetaDataFilePO metaDataFilePO = metaDataFileDAO.selectByPrimaryKey(id);
        if(null == metaDataFilePO) {
            throw new ServiceException(
                    String.format("删除失败：待删除元数据文件上传记录在系统中不存在"),
                    ErrorCodeEnum.META_DATA_FILE_NOT_EXISTS.getCode()
            );
        }
        /*
         * 删除文件
         */
        String filePath = metaDataFilePO.getFilePath();
        File file = new File(filePath);
        if(!file.exists()) {
            throw new ServiceException(
                    String.format("删除失败：待删除元数据文件在系统中不存在"),
                    ErrorCodeEnum.FILE_NOT_EXISTS.getCode()
            );
        }
        boolean deleteSuccessful = file.delete();
        if(!deleteSuccessful) {
            throw new ServiceException(
                    String.format("删除失败：待删除元数据文件删除失败"),
                    ErrorCodeEnum.FILE_DELETE_FAILED.getCode()
            );
        }
        /*
         * 删除元数据文件上传记录
         */
        metaDataFileDAO.deleteByPrimaryKey(id);
    }

    @Override
    public MetaDataFileContent getMetaDataFileContent(Long id) {
        MetaDataFilePO metaDataFilePO = metaDataFileDAO.selectByPrimaryKey(id);
        if(null == metaDataFilePO) {
            throw new ServiceException(
                    String.format("元数据上传文件内容获取失败：元数据文件上传记录在系统中不存在"),
                    ErrorCodeEnum.META_DATA_FILE_NOT_EXISTS.getCode()
            );
        }
        /*
         * 获取元数据文件
         */
        String filePath = metaDataFilePO.getFilePath();
        File file = new File(filePath);
        if(!file.exists()) {
            throw new ServiceException(
                    String.format("元数据上传文件内容获取失败：元数据文件在系统中不存在"),
                    ErrorCodeEnum.FILE_NOT_EXISTS.getCode()
            );
        }
        /*
         * 元数据文件读取内容
         *
         * TODO：
         *
         */
//        ExcelUtils.getListByExcel()
        return null;

    }

    @Override
    public List<MetaDataFileDO> paginationQueryByCondition(MetaDataFilePaginationQueryConditionDO metaDataFilePaginationQueryConditionDO) {
        String column = metaDataFilePaginationQueryConditionDO.getSortColumn();
        if (column != null) {
            for (char c : column.toCharArray()) {
                if (!Character.isLetter(c) && c != '_') {
                    return Collections.emptyList();
                }
            }
        }
        List<MetaDataFilePO> metaDataFilePOList = metaDataFileDAO.paginationQueryByCondition(metaDataFilePaginationQueryConditionDO);
        return ConvertUtil.list2List(metaDataFilePOList, MetaDataFileDO.class);
    }

    @Override
    public Integer queryCountByCondition(MetaDataFilePaginationQueryConditionDO metaDataFilePaginationQueryConditionDO) {
        return metaDataFileDAO.queryCountByCondition(metaDataFilePaginationQueryConditionDO);
    }

    private Long handleCreateMetaDataFile(MetaDataFileDO metaDataFileDO, String operator) {
        /*
         * 创建AgentVersion
         */
        MetaDataFilePO metaDataFilePO = metaDataFileDO.convert2MetaDataFilePO();
        metaDataFilePO.setOperator(CommonConstant.getOperator(operator));
        metaDataFileDAO.insert(metaDataFilePO);
        return metaDataFilePO.getId();
    }

}
