package com.fsj.util;

import com.fsj.common.ExcelError;
import com.fsj.common.ExcelResult;
import com.fsj.util.ExcelValidate;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ExcelReader<E> {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelReader.class);
    private Class<E> clazz;
    private int beginRow;
    private int maxRow = -1;
    private ExcelResult<E> excelResult;
    private Map<String,Field> sortNameMap;
    private Row excelField;
    public ExcelReader(Class<E> clazz) {
        this.clazz = clazz;
        excelResult = new ExcelResult<>();
    }

    private E newInstance(){
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            LOG.error("newInstance exception",e);
        } catch (IllegalAccessException e) {
            LOG.error("newInstance exception",e);
        }
            /*ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            type.getActualTypeArguments()[0].getClass().newInstance();
            return (E)type.getActualTypeArguments()[0].getClass().newInstance();*/
        return null;
    }

    public ExcelResult<E> read(File file){
        List<ExcelError> excelErrorList =  excelResult.getExcelErrors();
        List<E> list = excelResult.getList();
        try (InputStream is = new FileInputStream(file)){
            sortNameMap = ExcelUtils.initSortNameFieldMap(clazz, ExcelUtils.ExcelType.IMPORT);
            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);
            if (maxRow > 0 && sheet.getLastRowNum() + 1 > maxRow) {
                LOG.info("导入文档的行数超过限制 rowCount is {} , but maxRow is {}", sheet.getLastRowNum() + 1, maxRow);
                ExcelError excelError = new ExcelError(-2,"导入文档的行数超过限制");
                excelErrorList.add(excelError);
                return excelResult;
            }
            excelField = sheet.getRow(beginRow);
            List<String> keyList = new ArrayList<>(sortNameMap.keySet());
            if (checkField(excelErrorList, keyList)) return excelResult;
            readRow(excelErrorList, list, sheet, keyList);
        } catch (Exception e) {
            LOG.error("excelReader error:{}",e);
        }
        return excelResult;
    }

    /**
     * 读Row
     * @param excelErrorList
     * @param list
     * @param sheet
     * @param keyList
     * @throws IllegalAccessException
     * @throws java.text.ParseException
     */
    private void readRow(List<ExcelError> excelErrorList, List<E> list, Sheet sheet, List<String> keyList) throws IllegalAccessException, java.text.ParseException {
        Row row ;
        List<Field> fieldList = new ArrayList<>(sortNameMap.values());
        for (int rowIndex = beginRow+1; rowIndex<=sheet.getLastRowNum(); rowIndex++ ) {
            row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }
            E e = readCell(excelErrorList, keyList, row, fieldList, rowIndex);
            list.add(e);
        }
    }


    /**
     * 读cell
     * @param excelErrorList
     * @param keyList
     * @param row
     * @param fieldList
     * @param rowIndex
     * @return
     * @throws IllegalAccessException
     * @throws java.text.ParseException
     */
    private E readCell(List<ExcelError> excelErrorList, List<String> keyList, Row row, List<Field> fieldList, int rowIndex) throws IllegalAccessException, java.text.ParseException {
        Cell cell;
        E e = this.newInstance();
        for (int i = 0; i < excelField.getLastCellNum(); i++) {
            cell = row.getCell(i);
            Object value = ExcelUtils.readCellContent(cell);
            Field field = fieldList.get(i);
            //检测属性是否正确
            if(!ExcelValidate.validate(value,field)){
                ExcelError excelError = new ExcelError();
                excelError.setRow(rowIndex+1);
                excelError.setCol(i+1);
                excelError.setName(keyList.get(i));
                excelErrorList.add(excelError);
                continue;
            }
            //赋值
            ExcelUtils.setFieldValue(field, e,null, value);
        }
        return e;
    }

    /**
     *检查excel表字段
     * @param excelErrorList
     * @param keyList
     * @return
     */
    private boolean checkField(List<ExcelError> excelErrorList, List<String> keyList) {
        //导入文档的列数和模板的列数不一致,模板有误
        if (sortNameMap.size() != excelField.getLastCellNum()) {
            LOG.info("导入文档的列数和模板的列数不一致 columnCount is {} , but templateFieldsArrLength is {}",sortNameMap.size(),excelField.getPhysicalNumberOfCells());
            ExcelError excelError = new ExcelError(-2,"导入文档的列数和模板的列数不一致");
            excelErrorList.add(excelError);
        }
        //导入文档的列名和模板的列名不一致
        for (int i = 0; i < excelField.getLastCellNum(); i++) {
            if(!keyList.get(i).equals(String.valueOf(ExcelUtils.readCellContent(excelField.getCell(i))))) {
                LOG.info("导入文档的列名和模板的列名不一致 文档名：{} , 模板名：{}",keyList.get(i),String.valueOf(ExcelUtils.readCellContent(excelField.getCell(i))));
                ExcelError excelError = new ExcelError(-2,"导入文档的列名和模板的列名不一致");
                excelErrorList.add(excelError);
                break;
            }
        }
        if(excelErrorList.size()>0){
            return true;
        }
        return false;
    }



    public void setBeginRow(int beginRow) {
        this.beginRow = beginRow;
    }

    public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
	}

	public static void main(String[] args) {

    }
}
