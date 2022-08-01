package com.didichuxing.datachannel.agentmanager.common.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description : excel导入工具类
 * Version :1.0
 */
public class ExcelUtils {

    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel

    /**
     * @Description：读取给定 excel 文件对应 sheet 内容
     * @param in InputStream 对象
     * @param fileName 文件名
     * @param sheetIndex 第几个sheet（从0开始）
     * @return sheet 内容
     * @throws Exception excel 文件读取过程中出现的异常
     */
    public static List<List<Object>> getListByExcel(
            InputStream in,
            String fileName,
            Integer sheetIndex,
            Integer rowStartIndex
    ) throws Exception{
        List<List<Object>> list = null;
        Workbook work = getWorkbook(in,fileName);
        if(null == work){
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;  //页数
        Row row = null;  //行数
        Cell cell = null;  //列数
        list = new ArrayList();
        if(sheetIndex >= work.getNumberOfSheets()) {
            throw new Exception(
                    String.format("Excel工作薄共%d个sheet，待获取的第%d个sheet不存在", work.getNumberOfSheets(), sheetIndex)
            );
        } else {
            //取某个sheet
            sheet = work.getSheetAt(sheetIndex);
            if(sheet != null){
                //遍历当前sheet中的所有行
                if(rowStartIndex > sheet.getLastRowNum()) {
                    throw new Exception(
                            String.format("Excel工作薄第%d个sheet共%d行，无法从第%d行开始读取", sheetIndex, sheet.getLastRowNum(), rowStartIndex)
                    );
                }
                for (int j = rowStartIndex; j <= sheet.getLastRowNum(); j++) {
                    row = sheet.getRow(j);
                    if(row == null) {
                        throw new Exception(
                                String.format(
                                        "待读取第%d个sheet的第%d行为空", sheetIndex, j
                                )
                        );
                    }
                    //遍历所有的列
                    List<Object> li = new ArrayList<Object>();
                    for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                        cell = row.getCell(y);
                        li.add(getValue(cell));
                    }
                    list.add(li);
                }
            } else {
                throw new Exception(
                        String.format(
                                "待读取第%d个sheet为空", sheetIndex
                        )
                );
            }
        }
        return list;
    }

    /**
     * @Description：根据文件后缀，自适应上传文件的版本
     * @param inStr InputStream 对象
     * @param fileName 文件名
     * @return Workbook
     * @throws Exception excel 格式错误异常
     */
    private static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType)){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * @Description：获取给定单元格内值
     */
    private static String getValue(Cell cell) {
        if(null == cell){
            return "";
        } else {
            return cell.getStringCellValue();
        }
    }

}
