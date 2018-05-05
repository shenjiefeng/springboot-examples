package com.fsj.util;

import com.fsj.common.Excel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/4 0004.
 */
public class ExcelWriter<E> {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelWriter.class);
    private int beginRow;
    private List<E> lists;
    private Class<E> clazz;
    private Map<String,Field> sortNameMap;
    private Workbook wb;
    public ExcelWriter(List<E> lists) {
        this.lists = lists;
        wb = new SXSSFWorkbook();
        if(CollectionUtils.isNotEmpty(this.lists)){
            //noinspection unchecked
            clazz = (Class<E>) this.lists.get(0).getClass();
        }
    }

    public Workbook write(){
        Sheet sheet = wb.createSheet();
        if(CollectionUtils.isEmpty(lists)){
            return wb;
        }
        try {
            sortNameMap=ExcelUtils.initSortNameFieldMap(clazz, ExcelUtils.ExcelType.EXPORT);
            writeTitle(sheet);
            writeRow(sheet);
        } catch (Exception e) {
            LOG.error("导出失败：",e);
        }
        return wb;
    }
    private void writeTitle(Sheet sheet){
        XSSFCellStyle titleStyle = ExcelUtils.getTitleCellStyle(wb);
        int columnIndex = 0;
        Excel excel;
        Row row = sheet.createRow(beginRow);
        for(Field field : sortNameMap.values()){
            field.setAccessible(true);
            excel = field.getAnnotation(Excel.class);
            // 列宽注意乘256
            sheet.setColumnWidth(columnIndex, excel.width() * 256);
            // 写入标题
            Cell cell = row.createCell(columnIndex);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(excel.name());
            columnIndex++;
        }
    }
    private void writeRow(Sheet sheet){
        int rowIndex = beginRow+1;
        try {
            for(E e : lists){
                Row row = sheet.createRow(rowIndex);
                int cellIndex=0;
                for(Field field : sortNameMap.values()){
                    Cell cell = row.createCell(cellIndex);
                    Object value = field.get(e);
                    if(value == null){
                        cellIndex++;
                        continue;
                    }
                    setCellValue(cell, value);
                    cellIndex++;
                }
                rowIndex++;
            }
        } catch (IllegalAccessException e) {
            LOG.error("导出失败：",e);
        }
    }

    private void setCellValue(Cell cell, Object value) {
        if(value instanceof Date){
            cell.setCellValue((Date)value);
        }else if(value instanceof Integer){
            cell.setCellValue((Integer)value);
        }else {//最后都按String处理
            cell.setCellValue(value.toString());
        }
    }

}
