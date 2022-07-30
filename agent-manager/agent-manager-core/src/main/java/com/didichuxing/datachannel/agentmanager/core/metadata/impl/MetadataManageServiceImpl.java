package com.didichuxing.datachannel.agentmanager.core.metadata.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.ListCompareResult;
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
import com.didichuxing.datachannel.agentmanager.common.util.*;
import com.didichuxing.datachannel.agentmanager.common.util.Comparator;
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
        /*
         * 1.）handle host info
         */
        List<HostDO> hostDOListTarget = new ArrayList<>();
        for (int i = 0; i < hostList.size(); i++) {
            List<Object> host = hostList.get(i);
            String hostName = host.get(0).toString();
            String ip = host.get(1).toString();
            HostDO hostDO = new HostDO();
            hostDO.setHostName(hostName);
            hostDO.setIp(ip);
            hostDO.setContainer(HostTypeEnum.HOST.getCode());
            hostDOListTarget.add(hostDO);
        }
        List<HostDO> hostDOListSource = hostManageService.list();
        ListCompareResult<HostDO> hostDOListCompareResult = ListCompareUtil.compare(hostDOListSource, hostDOListTarget, new Comparator<HostDO, String>() {
            @Override
            public String getKey(HostDO hostDO) {
                return hostDO.getHostName();
            }
            @Override
            public boolean compare(HostDO t1, HostDO t2) {
                if(
                        t1.getIp().equals(t2.getIp()) &&
                                t1.getContainer().equals(t2.getContainer())
                ) {
                    return true;
                } else {
                    return false;
                }
            }
            @Override
            public HostDO getModified(HostDO source, HostDO target) {
                source.setIp(target.getIp());
                source.setContainer(target.getContainer());
                return source;
            }
        });
        for (HostDO hostDO : hostDOListCompareResult.getCreateList()) {
            hostManageService.createHost(hostDO, operator);
        }
        for (HostDO hostDO : hostDOListCompareResult.getRemoveList()) {
            hostManageService.deleteHost(hostDO.getId(), true, true, operator);
        }
        for (HostDO hostDO : hostDOListCompareResult.getModifyList()) {
            hostManageService.updateHost(hostDO, operator);
        }
        /*
         * handle service info
         */
        List<ServiceDO> serviceDOListTarget = new ArrayList<>();
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
            serviceDOListTarget.add(serviceDO);
        }
        List<ServiceDO> serviceDOListSource = serviceManageService.list();
        /*
         * 2.）全量对比 excel 文件数据 & 系统 数据，进行对应持久化操作
         */
        ListCompareResult<ServiceDO> serviceDOListCompareResult = ListCompareUtil.compare(serviceDOListSource, serviceDOListTarget, new Comparator<ServiceDO, String>() {
            @Override
            public String getKey(ServiceDO serviceDO) {
                return serviceDO.getServicename();
            }
            @Override
            public boolean compare(ServiceDO source, ServiceDO target) {
                List<HostDO> hostDOList = hostManageService.getHostsByServiceId(source.getId());
                List<Long> hostIdListSource = new ArrayList<>();
                for (HostDO hostDO : hostDOList) {
                    hostIdListSource.add(hostDO.getId());
                }
                List<Long> hostIdListTarget = target.getHostIdList();
                ListCompareResult<Long> hostIdListCompareResult = ListCompareUtil.compare(hostIdListSource, hostIdListTarget, new Comparator<Long, Long>() {
                    @Override
                    public Long getKey(Long hostId) {
                        return hostId;
                    }
                    @Override
                    public boolean compare(Long t1, Long t2) {
                        return t1.equals(t2);
                    }
                    @Override
                    public Long getModified(Long source, Long target) {
                        return source;
                    }
                });
                if(
                        CollectionUtils.isEmpty(hostIdListCompareResult.getModifyList()) &&
                                CollectionUtils.isEmpty(hostIdListCompareResult.getCreateList()) &&
                                CollectionUtils.isEmpty(hostIdListCompareResult.getRemoveList())
                ) {
                    return true;
                } else {
                    return false;
                }
            }
            @Override
            public ServiceDO getModified(ServiceDO source, ServiceDO target) {
                source.setHostIdList(target.getHostIdList());
                return source;
            }
        });
        for (ServiceDO serviceDO : serviceDOListCompareResult.getCreateList()) {
            serviceManageService.createService(serviceDO, operator);
        }
        for (ServiceDO serviceDO : serviceDOListCompareResult.getRemoveList()) {
            List<Long> serviceIdList = new ArrayList<>();
            serviceIdList.add(serviceDO.getId());
            serviceManageService.deleteServices(serviceIdList, true, operator);
        }
        for (ServiceDO serviceDO : serviceDOListCompareResult.getModifyList()) {
            serviceManageService.updateService(serviceDO, operator);
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
                        String.format("host sheet中第%d行的主机名、ip、主机类型不可为空", i+1),
                        ErrorCodeEnum.META_DATA_IN_EXCEL_FIELD_IS_NULL_EXISTS.getCode()
                );
            }
            if(!hostType.equals(HostTypeEnum.HOST.getDescription())) {
                throw new ServiceException(
                        String.format("host sheet中第%d行对应主机类型值非法，合法值范围为：[物理机]", i+1),
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
                        String.format("application sheet中第%d行的应用名、关联主机对应主机名不可为空", i+1),
                        ErrorCodeEnum.META_DATA_IN_EXCEL_FIELD_IS_NULL_EXISTS.getCode()
                );
            }
            String[] relationHostNameArray = relationHost.split(CommonConstant.COMMA);
            for (String relationHostName : relationHostNameArray) {
                if(
                        !hostNameSet.contains(relationHostName)
                ) {
                    throw new ServiceException(
                            String.format("application sheet中第%d行的关联主机对应主机名%s在host sheet不存在", i+1, relationHostName),
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
