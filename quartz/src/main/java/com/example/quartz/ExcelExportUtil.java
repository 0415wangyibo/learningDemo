package com.example.quartz;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/11/14 12:11
 * Modified By:
 * Description:依赖于poi构建的工具类，用来导出excel表格
 */
public class ExcelExportUtil<T> {
    private Class<T> clazz;

    public ExcelExportUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    public String getExcel(String excelName, String[] titles, List<T> data, HttpServletResponse response) {
        try {
//            response.setContentType("application/x-xls;charset=UTF-8");
            response.setContentType("application/octet-stream;charset=UTF-8");
            String fileName = excelName + ".xls";
            fileName = MimeUtility.encodeText(URLEncoder.encode(fileName, "UTF-8"), "UTF-8", "B");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = workbook.createSheet(excelName);

            //标题样式
            HSSFFont headFont = workbook.createFont();
            headFont.setFontName("宋体");//字体
            headFont.setFontHeightInPoints((short) 20);//字体大小
            headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗
            HSSFCellStyle headCellStyle = workbook.createCellStyle();
            headCellStyle.setFont(headFont);
            headCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
            headCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平

            //表头样式
            HSSFFont titleFont = workbook.createFont();
            titleFont.setFontName("宋体");
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗
            HSSFCellStyle titleCellStyle = workbook.createCellStyle();
            titleCellStyle.setFont(titleFont);
            titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
            titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平

            //表项样式
            HSSFFont bodyFont = workbook.createFont();
            bodyFont.setFontName("宋体");
            bodyFont.setFontHeightInPoints((short) 16);
            HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
            hssfCellStyle.setFont(bodyFont);
            hssfCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
            hssfCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平

            //设置标题
            HSSFRow hssfRow = hssfSheet.createRow(0);
            hssfRow.setHeight((short) 1000);//设置第一行高度
            HSSFCell hssfCell;
            hssfCell = hssfRow.createCell(0);
            hssfCell.setCellValue(excelName);//设置标题
            hssfCell.setCellStyle(headCellStyle);//设置样式

            //设置表头
            hssfRow = hssfSheet.createRow(1);
            hssfRow.setHeight((short) 550);
            for (int i = 0; i < titles.length; i++) {
                hssfCell = hssfRow.createCell(i);
                hssfCell.setCellValue(titles[i]);
                hssfCell.setCellStyle(titleCellStyle);
            }
            Field[] fields = clazz.getDeclaredFields();
            int length = fields.length;
            // 获取需要写入的数据
            for (int i = 0; i < data.size(); i++) {
                hssfRow = hssfSheet.createRow(i + 2);

                T t = data.get(i);
                boolean flag = false;

                for (int j = 0; j < length; j++) {
                    String fieldName = fields[j].getName();
                    if ("serialVersionUID".equals(fieldName)) {
                        flag = true;
                        continue;
                    }

                    // 类中需要有类成员的get方法
                    Method method = clazz.getMethod("get" + change(fieldName), (Class<?>[]) null);
                    Object obj = method.invoke(t, (Object[]) null);
                    if (flag) {
                        hssfCell = hssfRow.createCell(j - 1);
                        hssfCell.setCellStyle(hssfCellStyle);
                        hssfCell.setCellValue(String.valueOf(obj));
                    } else {
                        hssfCell = hssfRow.createCell(j);
                        hssfCell.setCellStyle(hssfCellStyle);
                        hssfCell.setCellValue(String.valueOf(obj));
                    }
                }
            }
            //设置列宽
            for (int i = 0; i < length; i++) {
                hssfSheet.setColumnWidth((short) i, 6000);
            }
            //合并标题栏
            hssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, length - 1));
            return tryOutput(outputStream, workbook);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String tryOutput(ServletOutputStream outputStream, HSSFWorkbook workbook) {
        // 输出文件到浏览器
        try {
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * @param src 源字符串
     * @return 字符串，将src的第一个字母转换为大写，src为空时返回null
     */
    private String change(String src) {
        if (src != null) {
            StringBuilder stringBuilder = new StringBuilder(src);
            stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
            return stringBuilder.toString();
        } else {
            return null;
        }
    }
}