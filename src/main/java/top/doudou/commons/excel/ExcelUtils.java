package top.doudou.commons.excel;


import com.google.common.collect.Lists;
import top.doudou.commons.constant.SysConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel工具类
 * @author anjingsi
 * @date 2020-03-26
 */
@Slf4j
public class ExcelUtils {

    /**
     * 将excel数据转换成实体类
     * @param file  excel文件
     * @param target  实体类
     * @param headRow  头所在的行号
     * @param <T>
     * @return
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public static <T>List<T> readExcel(MultipartFile file, Class<T> target, int headRow) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        checkFile(file);
        Workbook workbook = getWorkBook(file);
        List<T> result = Lists.newArrayList();
        if (workbook != null) {
            //只是解析excel中的第一个sheet
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet != null) {
                int firstRowNum = sheet.getFirstRowNum();
                int lastRowNum = sheet.getLastRowNum();
                Map<String,Integer> propertyMap = null;
                if(lastRowNum > 0 ){
                    //生成属性和列对应关系的map，Map<类属性名，对应一行的第几列>
                    propertyMap=generateColumnPropertyMap(sheet,headRow, EntityAnnotationUtils.getAnnotationProperty(target));
                }
                Set<Map.Entry<String, Integer>> entrySet = propertyMap.entrySet();
                for(int rowNum = firstRowNum + headRow; rowNum <= lastRowNum; ++rowNum) {
                    Row row = sheet.getRow(rowNum);
                    if (row != null) {
                        T instance = target.newInstance();
                        boolean flag = true;
                        for (Map.Entry<String, Integer> entry : entrySet) {
                            Object property=getCellValue(row.getCell(entry.getValue()));
                            if(property != null && !property.equals("")){
                                flag = false;
                                BeanUtils.setProperty(instance, entry.getKey(), property);
                            }
                        }
                        if(!flag){
                            try {
                                BeanUtils.setProperty(instance, "excelRow", rowNum);
                            }catch (Exception e){}
                            result.add(instance);
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     * 检查excel文件
     * @param file
     * @throws IOException
     */
    public static void checkFile(MultipartFile file) throws IOException {
        if (null == file) {
            log.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        } else {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
                log.error(fileName + "不是excel文件");
                throw new IOException(fileName + "不是excel文件");
            }
        }
    }

    public static Workbook getWorkBook(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Object workbook = null;
        try {
            InputStream is = file.getInputStream();
            if (fileName.endsWith("xls")) {
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(is);
            }
            return (Workbook)workbook;
        } catch (IOException var4) {
            log.info(var4.getMessage());
            return null;
        }
    }

    /**
     * 生成一个属性-列的对应关系的map
     * @param sheet	表
     * @param alias	别名
     * @return
     */
    private static Map<String,Integer> generateColumnPropertyMap(Sheet sheet,int headRow,LinkedHashMap<String,String> alias) {
        if(alias == null || alias.isEmpty()){
            throw new BizException("实体类上需加上@ExcelMapping注解");
        }
        Map<String,Integer> propertyMap=new HashMap<>();
        Row row = sheet.getRow(headRow-1);
        short firstCellNum = row.getFirstCellNum();
        short lastCellNum = row.getLastCellNum();

        for(int i=firstCellNum;i<lastCellNum;i++) {
            Cell cell = row.getCell(i);
            if(cell==null) {
                continue;
            }
            String cellValue = cell.getStringCellValue();
            String propertyName = alias.get(cellValue);
            if(StringUtils.isBlank(propertyName)){
                continue;
            }
            propertyMap.put(propertyName, i);
        }
        return propertyMap;
    }


    /**
     * 获取当前cell的值
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {
        Object cellValue = "";
        if (cell == null) {
            return cellValue;
        } else {
            switch(cell.getCellTypeEnum()) {
                case NUMERIC:
                case FORMULA:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                        SimpleDateFormat sdf = null;
                        if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                            sdf = new SimpleDateFormat("HH:mm");
                        } else {// 日期
                            sdf = new SimpleDateFormat("yyyy-MM-dd");
                        }
                        cellValue = cell.getDateCellValue();
                    } else if (cell.getCellStyle().getDataFormat() == 58) {// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                        double value = cell.getNumericCellValue();
                        cellValue = DateUtil.getJavaDate(value);
                    } else {
                        double value = cell.getNumericCellValue();
                        cellValue = new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                    break;
                case STRING:
                    cellValue = String.valueOf(cell.getStringCellValue());
                    break;
                case BLANK:
                    cellValue = null;
                    break;
                case BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case ERROR:
                    cellValue = "非法字符";
                    break;
                default:
                    cellValue = "未知类型";
            }
            return cellValue;
        }
    }


    /**
     * 创建文档的基础信息
     * @param documentSummaryInfo
     * @return
     */
    private static HSSFWorkbook createBaseSheet(DocumentSummaryInfo documentSummaryInfo){
        //1.创建Excel文档
        HSSFWorkbook workbook = new HSSFWorkbook();
        //2.创建文档摘要
        workbook.createInformationProperties();
        if(documentSummaryInfo != null){
            //3.获取文档信息，并配置
            DocumentSummaryInformation dsi = workbook.getDocumentSummaryInformation();
            //3.1文档类别
            if(StringUtils.isNotBlank(documentSummaryInfo.getCategory())){
                dsi.setCategory(documentSummaryInfo.getCategory());
            }
            //3.2设置文档管理员
            if(StringUtils.isNotBlank(documentSummaryInfo.getManager())){
                dsi.setManager(documentSummaryInfo.getManager());
            }
            //3.3设置组织机构
            if(StringUtils.isNotBlank(documentSummaryInfo.getCompany())){
                dsi.setCompany(documentSummaryInfo.getCompany());
            }
            //4.获取摘要信息并配置
            SummaryInformation si = workbook.getSummaryInformation();
            //4.1设置文档主题
            if(StringUtils.isNotBlank(documentSummaryInfo.getSubject())){
                si.setSubject(documentSummaryInfo.getSubject());
            }
            //4.2.设置文档标题
            if(StringUtils.isNotBlank(documentSummaryInfo.getTitile())){
                si.setTitle(documentSummaryInfo.getTitile());
            }
            //4.3 设置文档作者
            if(StringUtils.isNotBlank(documentSummaryInfo.getAuthor())){
                si.setAuthor(documentSummaryInfo.getAuthor());
            }
            //4.4设置文档备注
            if(StringUtils.isNotBlank(documentSummaryInfo.getComments())){
                si.setComments(documentSummaryInfo.getComments());
            }
        }
        return workbook;
    }

    /**
     * 设置表头的数据
     * @param excelWriteProperty 表头数据
     */
    private static void setSheetHeader(HSSFSheet sheet,HSSFCellStyle headerStyle,LinkedHashMap<Integer, ExcelWriteDto> excelWriteProperty){
        if(excelWriteProperty == null){
            return;
        }
        //设置标题的显示样式
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        HSSFRow headerRow = sheet.createRow(0);
        for (Map.Entry<Integer, ExcelWriteDto> entry : excelWriteProperty.entrySet()) {
            //设置列的宽度
            sheet.setColumnWidth(entry.getKey(), entry.getValue().getColumnWidth());
            //设置表头的值
            HSSFCell cell = headerRow.createCell(entry.getKey());
            cell.setCellValue(entry.getValue().getValue());
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * 将数据导出到excel中
     * @param list  数据
     * @param documentSummaryInfo  文档的摘要信息
     * @param target 数据的class
     * @param <T>
     * @return
     */
    public static  <T>HSSFWorkbook dateToExcel(List<T> list,DocumentSummaryInfo documentSummaryInfo,Class<T> target){
        HSSFWorkbook workbook = createBaseSheet(documentSummaryInfo);
        //创建Excel表单
        HSSFSheet sheet = workbook.createSheet(documentSummaryInfo == null?"sheet1":documentSummaryInfo.getSheetName());
        HSSFCellStyle headerStyle = workbook.createCellStyle();

        /**
         * 获取写入的属性
         */
        LinkedHashMap<Integer, ExcelWriteDto> excelWriteProperty = EntityAnnotationUtils.getExcelWriteProperty(target);
        if(excelWriteProperty.isEmpty()){
            throw new BizException("实体类中属性缺少ExcelMapping注解");
        }
        Set<Map.Entry<Integer, ExcelWriteDto>> entrySet = excelWriteProperty.entrySet();
        //设置表头
        setSheetHeader(sheet,headerStyle,excelWriteProperty);

        //创建日期显示格式
        HSSFCellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));

//        //将数据写入到excel中
        for(int i = 0; i < list.size(); i++){
            HSSFRow row = sheet.createRow(i + 1);
            T t = list.get(i);
            for (Map.Entry<Integer, ExcelWriteDto> entry : entrySet) {
                Object value = null;
                String fieldName = entry.getValue().getFieldName();
                try {
                    value = t.getClass().getMethod("get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1)).invoke(t);
                    if(t.getClass().getDeclaredField(fieldName).getType() == Date.class){
                        HSSFCell cell = row.createCell(entry.getKey());
                        cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(value));
                        cell.setCellStyle(dateCellStyle);
                        continue;
                    }
                }catch (Exception e){}
                row.createCell(entry.getKey()).setCellValue(value == null ? "":value.toString());
            }
        }
        return workbook;
    }

    public static <T>void exportDate(HttpServletResponse response, String sheetName, List<T> list, String fileName, Class<T> target){
        DocumentSummaryInfo documentSummaryInfo = new DocumentSummaryInfo();
        documentSummaryInfo.setSheetName(sheetName);
        HSSFWorkbook workbook = dateToExcel(list, documentSummaryInfo, target);
        try{
            OutputStream os = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition",
                    "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            response.setContentType(SysConstant.FILE_CONTENT_TYPE);
            workbook.write(os);
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            throw new BizException("文件导出出错,错误的原因为："+e.getMessage());
        }
    }

    public static void lastColumnWrite(String filePath, List<String> importResult,String uploadPath) {
        filePath = fileExist(filePath,uploadPath);
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(filePath);
            Workbook workbook = getWorkBook(filePath,in);
            Sheet sheet = workbook.getSheetAt(0);

            out = new FileOutputStream(filePath);
            int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();
            Row row = sheet.getRow(0);
            row.createCell(columnNum).setCellValue("导入备注");
            //追加列数据
            for(int i=0;i<importResult.size();i++){
                row = sheet.getRow(i+1);
                row.createCell(columnNum).setCellValue(importResult.get(i));
            }
            out.flush();
            workbook.write(out);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Workbook getWorkBook(String filePath,FileInputStream in) {
        Object workbook = null;
        try {
            if (filePath.endsWith("xls")) {
                workbook = new HSSFWorkbook(in);
            } else if (filePath.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(in);
            }
            return (Workbook)workbook;
        } catch (IOException var4) {
            log.info(var4.getMessage());
            return null;
        }
    }

    /**
     * 判断文件是否存在
     * @param filePath
     * @return
     */
    public static String fileExist(String filePath,String uploadPath){
        File file = new File(filePath);
        if(!file.exists()){
            if(filePath.startsWith("/file")){
                return uploadPath + filePath.substring(5);
            }
        }
        return uploadPath;
    }
}
