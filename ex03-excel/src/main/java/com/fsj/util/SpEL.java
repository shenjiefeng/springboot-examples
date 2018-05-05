package com.fsj.util;

import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public class SpEL {
    //private static SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, SpEL.class.getClassLoader());
    private static ExpressionParser parser = new SpelExpressionParser();

    /**
     *
     * 只用于解释变量
     * @param expressionStr el表达式
     * @param vars 表达式的参数map
     * @param classType 返回的classType
     * @param <T>
     * @return
     */
    public static <T> T val(String expressionStr, Map<String,Object> vars,Class<T> classType){
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(vars);
        Expression expression = parser.parseExpression(expressionStr);
        return expression.getValue(context,classType);
    }
    /**
     *
     * 只用于解释方法
     * @param expressionStr el表达式
     * @param beanFactoryResolver 表达式的参数map
     * @param classType 返回的classType
     * @param <T>
     * @return
     */
    public static <T> T val(String expressionStr, BeanFactoryResolver beanFactoryResolver, Class<T> classType){
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(beanFactoryResolver);
        Expression expression = parser.parseExpression(expressionStr);
        return expression.getValue(context,classType);
    }

    /**
     * 用于解释方法及参数中有变量的情况
     * @param expressionStr sple表达式
     * @param beanFactoryResolver 一般为applicationContext
     * @param vars 变量表
     * @param rootObject 根属性
     * @param classType 返回类型
     * @return
     */
    public static <T> T val(String expressionStr, BeanFactoryResolver beanFactoryResolver, Map<String,Object> vars, Object rootObject, Class<T> classType){
        StandardEvaluationContext context = new StandardEvaluationContext(rootObject);
        context.setBeanResolver(beanFactoryResolver);
        if(vars != null){
            context.setVariables(vars);
        }
        Expression expression = parser.parseExpression(expressionStr);
        return expression.getValue(context,classType);
    }
}
