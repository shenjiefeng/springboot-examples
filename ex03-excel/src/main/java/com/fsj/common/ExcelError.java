package com.fsj.common;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/26 0026.
 */
public class ExcelError implements Serializable{
    private int row;
    private int col;
    private String name;
    private String msg;

    public ExcelError() {
    }

    public ExcelError(int row, String name) {
        this.row = row;
        this.name = name;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
    
    
}
