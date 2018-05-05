package com.fsj.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/26 0026.
 */
public class ExcelResult<E> implements Serializable {
    private List<ExcelError> excelErrors = new ArrayList<>();
    //insert 成功条数
    private int count;
    private List<E> list = new ArrayList<>();
    public List<ExcelError> getExcelErrors() {
        return excelErrors;
    }

    public void setExcelErrors(List<ExcelError> excelErrors) {
        this.excelErrors = excelErrors;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }
}
