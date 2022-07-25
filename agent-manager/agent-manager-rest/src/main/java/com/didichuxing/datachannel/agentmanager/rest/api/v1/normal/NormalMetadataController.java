package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.PaginationResult;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFilePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata.MetaDataFilePaginationRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metadata.MetadataFileDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFileContent;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metadata.MetaDataFilePaginationRecordVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.util.FileUtils;
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
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "Normal-Metadata维度相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "metadata")
public class NormalMetadataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NormalMetadataController.class);

    @Autowired
    private MetadataManageService metadataManageService;

    @ApiOperation(value = "上传 metadata excel 文件 & 描述信息，返回元数据上传记录 id", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Result<Long> addMetadataFile(MetadataFileDTO dto) {
        Long id = metadataManageService.addMetadataFile(dto, SpringTool.getUserName());
        return Result.buildSucc(id);
    }

    @ApiOperation(value = "根据元数据上传记录 id 获取对应 metadata excel 文件内容信息", notes = "")
    @RequestMapping(value = "/file_content/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<MetaDataFileContent> getMetaDataFileContent(Long id) {
        return Result.buildSucc(metadataManageService.getMetaDataFileContent(id));
    }

    @ApiOperation(value = "根据元数据上传记录 id 删除对应 metadata excel 文件上传记录", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteMetaDataFile(@PathVariable Long id) {
        metadataManageService.deleteMetaDataFile(id);
        return Result.buildSucc();
    }

    @ApiOperation(value = "根据元数据上传记录 id 导入 metadata excel 文件中元数据内容", notes = "")
    @RequestMapping(value = "/import-result/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result importMetaData(@PathVariable Long id) {
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

    @ApiOperation(value = "下载 meta data excel 文件模板", notes = "")
    @RequestMapping(value = "/meta-data-excel-template", method = RequestMethod.GET)
    @ResponseBody
    public void downloadMetaDataExcelTemplate(HttpServletResponse response) {
        String fileSeparator = System.getProperty("file.separator");
        String path = FileUtils.class.getResource("/").getPath() + "files" + fileSeparator + "meta_data_excel_template.xlsx";
        InputStream is = null;
        OutputStream os = null;
        try{
            File file =new File(path);
            String fileName = file.getName();
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            /*
             * Content-Disposition的作⽤：告知浏览器以何种⽅式显⽰响应返回的⽂件，⽤浏览器打开还是以附件的形式下载到本地保存
             * attachment表⽰以附件⽅式下载   inline表⽰在线打开   "Content-Disposition: inline; filename=⽂件名.mp3"
             * filename表⽰⽂件的默认名称，因为⽹络传输只⽀持URL编码的相关⽀付，因此需要将⽂件名URL编码后进⾏传输,前端收到后需要反编码才能获取到真正的名称
             */
            response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileName,"UTF-8"));
            // 设置浏览器⽂件的⼤⼩
            response.addHeader("Content-Length",""+ file.length());
            os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            os.write(buffer);
            os.flush();
        } catch(IOException ex){
            LOGGER.error(
                    String.format("下载 meta data excel 文件模板失败，原因为：%s ", ex.getMessage()),
                    ex
            );
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException ex) {
                LOGGER.error(
                        String.format("下载 meta data excel 文件模板时，关闭输入流失败，原因为：%s ", ex.getMessage()),
                        ex
                );
            }
            try {
                if(null != os) {
                    os.close();
                }
            } catch (IOException ex) {
                LOGGER.error(
                        String.format("下载 meta data excel 文件模板时，关闭输出流失败，原因为：%s ", ex.getMessage()),
                        ex
                );
            }
        }
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


