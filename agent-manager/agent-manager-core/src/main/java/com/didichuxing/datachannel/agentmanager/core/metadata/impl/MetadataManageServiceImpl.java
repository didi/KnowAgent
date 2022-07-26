package com.didichuxing.datachannel.agentmanager.core.metadata.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileContent;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFilePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata.MetadataFileDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metadata.MetaDataFilePO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.ExcelUtils;
import com.didichuxing.datachannel.agentmanager.common.util.FileUtils;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.metadata.MetadataManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.MetaDataFileMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@org.springframework.stereotype.Service
public class MetadataManageServiceImpl implements MetadataManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataManageServiceImpl.class);

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Autowired
    private MetaDataFileMapper metaDataFileDAO;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private ServiceManageService serviceManageService;

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
        String filePath = FileUtils.upload(dto.getUploadFile(), dto.getFileMd5(), uploadDir);
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
         */
        MetaDataFileContent metaDataFileContent = null;
        try {
            List<List<Object>> hostList = ExcelUtils.getListByExcel(
                    new FileInputStream(file),
                    metaDataFilePO.getFileName(),
                    0,
                    1
            );
            List<List<Object>> applicationList = ExcelUtils.getListByExcel(
                    new FileInputStream(file),
                    metaDataFilePO.getFileName(),
                    1,
                    1
            );
            metaDataFileContent = new MetaDataFileContent();
            metaDataFileContent.setHostTable(hostList);
            metaDataFileContent.setApplicationTable(applicationList);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format("Excel 文件读取错误：%s", ex.getMessage()),
                    ex,
                    ErrorCodeEnum.EXCEL_FILE_READ_FAILED.getCode()
            );
        }
        return metaDataFileContent;
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

    @Override
    @Transactional
    public void importMetaData(Long id, String operator) {
        /*
         * 读取对应元数据 excel 文件内容
         */
        MetaDataFileContent metaDataFileContent = getMetaDataFileContent(id);
        /*
         * 元数据 excel 文件内容校验
         */
        if(null == metaDataFileContent) {
            throw new ServiceException(
                    "导入失败：元数据Excel文件内容为空",
                    ErrorCodeEnum.META_DATA_IN_EXCEL_IS_NULL.getCode()
            );
        }
        List<List<Object>> hostList = metaDataFileContent.getHostTable();
        List<List<Object>> applicationList = metaDataFileContent.getApplicationTable();
        checkMetaData(hostList, applicationList);
        /*
         * 元数据持久化
         */
        persistMetaData(operator, hostList, applicationList);
    }

    private void persistMetaData(String operator, List<List<Object>> hostList, List<List<Object>> applicationList) {
        for (int i = 0; i < hostList.size(); i++) {
            List<Object> host = hostList.get(i);
            String hostName = host.get(0).toString();
            String ip = host.get(1).toString();
            HostDO hostDO = new HostDO();
            hostDO.setHostName(hostName);
            hostDO.setIp(ip);
            hostDO.setContainer(HostTypeEnum.HOST.getCode());
            hostManageService.createHost(hostDO, operator);
        }
        for (int i = 0; i < applicationList.size(); i++) {
            List<Object> application = applicationList.get(i);
            String applicationName = application.get(0).toString();
            String relationHost = application.get(1).toString();
            String[] relationHostNameArray = relationHost.split(CommonConstant.COMMA);
            List<Long> hostIdList = new ArrayList<>();
            for (String relationHostName : relationHostNameArray) {
                hostIdList.add(hostManageService.getHostByHostName(relationHostName).getId());
            }
            ServiceDO serviceDO = new ServiceDO();
            serviceDO.setHostIdList(hostIdList);
            serviceDO.setServicename(applicationName);
            serviceManageService.createService(serviceDO, operator);
        }
    }

    private void checkMetaData(List<List<Object>> hostList, List<List<Object>> applicationList) {
        if(CollectionUtils.isEmpty(hostList)) {
            throw new ServiceException(
                    "导入失败：元数据Excel文件的host sheet为空",
                    ErrorCodeEnum.META_DATA_HOST_IN_EXCEL_IS_NULL.getCode()
            );
        }
        if(CollectionUtils.isEmpty(applicationList)) {
            throw new ServiceException(
                    "导入失败：元数据Excel文件的application sheet为空",
                    ErrorCodeEnum.META_DATA_APPLICATION_IN_EXCEL_IS_NULL.getCode()
            );
        }
        //  host info 校 验
        Set<String> hostNameSet = new HashSet<>();
        for (int i = 0; i < hostList.size(); i++) {
            List<Object> host = hostList.get(i);
            String hostName = host.get(0).toString();
            String ip = host.get(1).toString();
            String hostType = host.get(2).toString();
            if(
                    StringUtils.isBlank(hostName) ||
                            StringUtils.isBlank(ip) ||
                            StringUtils.isBlank(hostType)
            ) {
                throw new ServiceException(
                        String.format("host sheet中第%d行的主机名、ip、主机类型不可为空", i),
                        ErrorCodeEnum.META_DATA_IN_EXCEL_FIELD_IS_NULL_EXISTS.getCode()
                );
            }
            if(!hostType.equals(HostTypeEnum.HOST.getDescription())) {
                throw new ServiceException(
                        String.format("host sheet中第%d行对应主机类型值非法，合法值范围为：[物理机]", i),
                        ErrorCodeEnum.META_DATA_IN_EXCEL_FIELD_INVALID_EXISTS.getCode()
                );
            }
            hostNameSet.add(hostName);
        }
        if(hostNameSet.size() != hostList.size()) {
            throw new ServiceException(
                    "host sheet存在主机名重复的主机信息",
                    ErrorCodeEnum.META_DATA_IN_EXCEL_HOST_HOST_NAME_DUPLICATE.getCode()
            );
        }
        for (String hostName : hostNameSet) {
            HostDO hostDO = hostManageService.getHostByHostName(hostName);
            if(null != hostDO) {
                throw new ServiceException(
                        String.format("host sheet中主机名%s在系统中已存在", hostName),
                        ErrorCodeEnum.HOST_NAME_DUPLICATE.getCode()
                );
            }
        }
        //  application info 校 验
        Set<String> applicationNameSet = new HashSet<>();
        for (int i = 0; i < applicationList.size(); i++) {
            List<Object> application = applicationList.get(i);
            String applicationName = application.get(0).toString();
            String relationHost = application.get(1).toString();
            if(
                    StringUtils.isBlank(applicationName) ||
                            StringUtils.isBlank(relationHost)
            ) {
                throw new ServiceException(
                        String.format("application sheet中第%d行的应用名、关联主机对应主机名不可为空", i),
                        ErrorCodeEnum.META_DATA_IN_EXCEL_FIELD_IS_NULL_EXISTS.getCode()
                );
            }
            String[] relationHostNameArray = relationHost.split(CommonConstant.COMMA);
            for (String relationHostName : relationHostNameArray) {
                if(
                        !hostNameSet.contains(relationHostName) &&
                                null == hostManageService.getHostByHostName(relationHostName)
                ) {
                    throw new ServiceException(
                            String.format("application sheet中第%d行的关联主机对应主机名%s在host sheet与系统中不存在", i, relationHostName),
                            ErrorCodeEnum.META_DATA_IN_EXCEL_APPLICATION_HOST_NAME_NOT_EXISTS.getCode()
                    );
                }
            }
            applicationNameSet.add(applicationName);
        }
        if(applicationNameSet.size() != applicationList.size()) {
            throw new ServiceException(
                    "application sheet存在应用名重复的应用信息",
                    ErrorCodeEnum.META_DATA_IN_EXCEL_APPLICATION_APPLICATION_NAME_DUPLICATE.getCode()
            );
        }
        for (String applicationName : applicationNameSet) {
            ServiceDO serviceDO = serviceManageService.getServiceByServiceName(applicationName);
            if(null != serviceDO) {
                throw new ServiceException(
                        String.format("application sheet中应用名%s在系统中已存在", applicationName),
                        ErrorCodeEnum.SERVICE_NAME_DUPLICATE.getCode()
                );
            }
        }
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
