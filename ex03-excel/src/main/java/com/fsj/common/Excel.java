package com.fsj.common;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author
 * Excel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface Excel {
    /**
     * excel导入或者导出的名称
     * @return
     */
    String name() default "";

    int width() default 20;

    boolean skip() default false;

    int seq() ;

    boolean require() default true;

    boolean need2Check() default false;

    int verifyType() default 0;

    String pattern() default "";



    @Target({ METHOD, FIELD })
    @Retention(RUNTIME)
    @Documented
    @interface Length{
        LengthType type() default LengthType.CHAR;
        int min() default 0;
        int max() default Integer.MAX_VALUE;
    }
    enum LengthType{
        BYTE,CHAR
    }
    @interface Date{
        String format() default "yyyyMMdd";
    }
    @Target({ METHOD, FIELD })
    @Retention(RUNTIME)
    @Documented
    @interface SkipExport{
        SkipExportType[] value() ;
    }
    enum SkipExportType{
        MOP,CORP
    }
    @Target({METHOD,FIELD})
    @Retention(RUNTIME)
    @Documented
    @interface SpELMethod{
        /**
         * 是否用于检验
         * @return
         */
        boolean isValidate() default true;
        /**
         * spel
         * @return
         */
        String expression();

        /**
         * 返回结果的类型
         * @return
         */
        Class result();
        /**
         * spel返回的结果是否可以为null
         * @return
         */
        boolean canBeNull() default false;
        /**
         * 根据spel返回的结果赋值给某个属性
         * ""表示不赋值
         * @return
         */
        String targetField() default "";
    }
}
