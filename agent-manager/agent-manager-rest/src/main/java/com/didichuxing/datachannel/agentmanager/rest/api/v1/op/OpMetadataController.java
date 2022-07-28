package com.didichuxing.datachannel.agentmanager.rest.api.v1.op;

import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFilePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata.MetaDataFilePaginationRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata.MetadataFileDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileContent;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metadata.MetaDataFilePaginationRecordVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.metadata.MetadataManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "OP-Metadata维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_OP_PREFIX + "metadata")
public class OpMetadataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpMetadataController.class);

    @Autowired
    private MetadataManageService metadataManageService;

    @ApiOperation(
            value = "上传 metadata excel 文件 & 描述信息，返回元数据上传记录 id。错误状态码：" +
                    "10000：入参错误 " +
                    "31004：上传文件在后台创建失败",
            notes = ""
    )
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Result<Long> addMetadataFile(MetadataFileDTO dto) {
        Long id = metadataManageService.addMetadataFile(dto, SpringTool.getUserName());
        return Result.buildSucc(id);
    }

    @ApiOperation(
            value = "根据元数据上传记录 id 获取对应 metadata excel 文件内容信息，错误状态码：" +
                    "35000：元数据文件上传记录在系统中不存在 " +
                    "31002：元数据文件不存在 " +
                    "31006：Excel 文件读取失败",
            notes = ""
    )
    @RequestMapping(value = "/file-content/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<MetaDataFileContent> getMetaDataFileContent(@PathVariable Long id) {
        return Result.buildSucc(metadataManageService.getMetaDataFileContent(id));
    }

    @ApiOperation(
            value = "根据元数据上传记录 id 删除对应 metadata excel 文件上传记录，错误状态码：" +
                    "35000：元数据文件上传记录在系统中不存在 " +
                    "31002：元数据文件不存在 " +
                    "31005：元数据文件删除失败",
            notes = ""
    )
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteMetaDataFile(@PathVariable Long id) {
        metadataManageService.deleteMetaDataFile(id);
        return Result.buildSucc();
    }

    @ApiOperation(
            value = "根据元数据上传记录 id 导入 metadata excel 文件中元数据内容，错误状态码：" +
                    "35000：元数据文件上传记录在系统中不存在 " +
                    "31002：元数据文件不存在 " +
                    "31006：Excel 文件读取失败 " +
                    "35001：元数据Excel文件内容为空 " +
                    "35002：元数据Excel文件内容中host sheet为空 " +
                    "35003：元数据Excel文件内容中application sheet为空 " +
                    "35004：元数据Excel文件内容中sheet存在空值字段 " +
                    "35005：元数据Excel文件内容中sheet存在非法值字段 " +
                    "35006：元数据Excel文件内容中host sheet存在主机名重复主机记录 " +
                    "23003：元数据Excel文件内容中host sheet中主机名在系统中已存在 " +
                    "35007：元数据Excel文件内容中application sheet中关联主机对应主机名在host sheet与系统中不存在 " +
                    "35008：元数据Excel文件内容中application sheet存在应用名重复应用记录 " +
                    "27001：元数据Excel文件内容中application sheet中服务名在系统中已存在 ",
            notes = ""
    )
    @RequestMapping(value = "/import-result/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result importMetaData(@PathVariable Long id) {
        metadataManageService.importMetaData(id, SpringTool.getUserName());
        return Result.buildSucc();
    }

    @ApiOperation(value = "查询 meta data file 列表", notes = "")
    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    @ResponseBody
    public Result<PaginationResult<MetaDataFilePaginationRecordVO>> listMetaDataFilss(@RequestBody MetaDataFilePaginationRequestDTO dto) {
        MetaDataFilePaginationQueryConditionDO metaDataFilePaginationQueryConditionDO = metaDataFilePaginationRequestDTO2AgentVersionPaginationQueryConditionDO(dto);
        List<MetaDataFilePaginationRecordVO> metaDataFilePaginationRecordVOList = metaDataFileDOList2MetaDataFilePaginationRecordVOList(
                metadataManageService.paginationQueryByCondition(
                        metaDataFilePaginationQueryConditionDO
                )
        );
        PaginationResult<MetaDataFilePaginationRecordVO> paginationResult = new PaginationResult(
                metaDataFilePaginationRecordVOList,
                metadataManageService.queryCountByCondition(
                        metaDataFilePaginationQueryConditionDO
                ),
                dto.getPageNo(),
                dto.getPageSize()
        );
        return Result.buildSucc(paginationResult);
    }

    @ApiOperation(value = "返回 meta data excel 文件模板下载请求对应链接，端口号后拼该链接即为 excel 模板文件下载链接", notes = "")
    @RequestMapping(value = "/meta-data-excel-template", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> downloadMetaDataExcelTemplate() {
        return Result.buildSucc(
                "/files/meta_data_excel_template.xlsx"
        );
    }

    private List<MetaDataFilePaginationRecordVO> metaDataFileDOList2MetaDataFilePaginationRecordVOList(List<MetaDataFileDO> metaDataFileDOList) {
        if(CollectionUtils.isNotEmpty(metaDataFileDOList)) {
            List<MetaDataFilePaginationRecordVO> metaDataFilePaginationRecordVOList = new ArrayList<>(metaDataFileDOList.size());
            for (MetaDataFileDO metaDataFileDO : metaDataFileDOList) {
                MetaDataFilePaginationRecordVO metaDataFilePaginationRecordVO = new MetaDataFilePaginationRecordVO();
                metaDataFilePaginationRecordVO.setFileMd5(metaDataFileDO.getFileMd5());
                metaDataFilePaginationRecordVO.setFileName(metaDataFileDO.getFileName());
                metaDataFilePaginationRecordVO.setDescription(metaDataFileDO.getDescription());
                metaDataFilePaginationRecordVO.setUploadTime(metaDataFileDO.getCreateTime().getTime());
                metaDataFilePaginationRecordVO.setId(metaDataFileDO.getId());
                metaDataFilePaginationRecordVOList.add(metaDataFilePaginationRecordVO);
            }
            return metaDataFilePaginationRecordVOList;
        }
        return null;
    }

    private MetaDataFilePaginationQueryConditionDO metaDataFilePaginationRequestDTO2AgentVersionPaginationQueryConditionDO(MetaDataFilePaginationRequestDTO dto) {
        MetaDataFilePaginationQueryConditionDO metaDataFilePaginationQueryConditionDO = new MetaDataFilePaginationQueryConditionDO();
        if(StringUtils.isNotBlank(dto.getFileMd5())) {
            metaDataFilePaginationQueryConditionDO.setFileMd5(dto.getFileMd5().replace("_", "\\_").replace("%", "\\%"));
        }
        if(StringUtils.isNotBlank(dto.getFileName())) {
            metaDataFilePaginationQueryConditionDO.setFileName(dto.getFileName().replace("_", "\\_").replace("%", "\\%"));
        }
        if(StringUtils.isNotBlank(dto.getDescription())) {
            metaDataFilePaginationQueryConditionDO.setDescription(dto.getDescription().replace("_", "\\_").replace("%", "\\%"));
        }
        if(null != dto.getUploadTimeEnd()) {
            metaDataFilePaginationQueryConditionDO.setCreateTimeEnd(new Date(dto.getUploadTimeEnd()));
        }
        if(null != dto.getUploadTimeStart()) {
            metaDataFilePaginationQueryConditionDO.setCreateTimeStart(new Date(dto.getUploadTimeStart()));
        }
        metaDataFilePaginationQueryConditionDO.setLimitFrom(dto.getLimitFrom());
        metaDataFilePaginationQueryConditionDO.setLimitSize(dto.getLimitSize());
        metaDataFilePaginationQueryConditionDO.setSortColumn(dto.getSortColumn());
        metaDataFilePaginationQueryConditionDO.setAsc(dto.getAsc());
        return metaDataFilePaginationQueryConditionDO;
    }

}


