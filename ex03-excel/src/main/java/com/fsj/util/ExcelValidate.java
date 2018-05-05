package com.fsj.util;

import com.fsj.common.Excel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.expression.BeanFactoryResolver;

import java.lang.reflect.Field;

public class ExcelValidate {

    public static boolean validate(Object value,Field field){
        if(field!=null){
            Excel excel = field.getAnnotation(Excel.class);
            if (null == excel) {
                return true;
            }
            if(!validateRequire(value, excel)){
                return false;
            }
            if(value != null) {
                if(!validateLength(value, field)){
                    return false;
                }
                if(!validatePattern(value, excel)){
                    return false;
                }
                if(!validateBySpEL(value,field)){
                    return false;
                }

            }
        }
        return true;
    }

    private static boolean validateRequire(Object o, Excel excel) {
        boolean flag=true;
        if(excel.require() && o==null){
            flag = false;
        }
        return flag;
    }

    private static boolean validatePattern(Object o,  Excel excel) {
        boolean flag = true;
        String pattern = excel.pattern();
        if(StringUtils.isNotBlank(pattern)){
            flag = o.toString().matches(pattern);
        }
        return flag;
    }

    private static boolean validateLength(Object o, Field field) {
        Excel.Length length = field.getAnnotation(Excel.Length.class);
        boolean flag = true;
        if(length != null) {
            int max = length.max();
            int min = length.min();
            int tempLength;
            switch (length.type()){
                case BYTE: {
                    tempLength = o.toString().trim().getBytes().length;
                    break;
                }
                case CHAR:{
                    tempLength = o.toString().trim().length();
                    break;
                }
                default:{
                    throw new IllegalArgumentException();
                }
            }
            if ( tempLength < min || tempLength > max) {
                flag = false;
            }
        }
        return flag;
    }

    private static boolean validateBySpEL(Object o, Field field){
        boolean flag = true;
        Excel.SpELMethod spELMethod = field.getAnnotation(Excel.SpELMethod.class);
        if(spELMethod != null){
            if(!spELMethod.isValidate()){
                return flag;
            }
            Object val = SpEL.val(spELMethod.expression(),new BeanFactoryResolver(SpringContextHolder.getApplicationContext()),null,o,spELMethod.result());
            if((val == null || "null".equals(val) ) && !spELMethod.canBeNull()){
                return false;
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        String matchesStr = "^(?=.*\\*).*$";
        System.out.println("小李*".matches(matchesStr));
    }
}
