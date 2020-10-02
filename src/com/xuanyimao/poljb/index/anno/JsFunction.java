/**
 * 
 */
package com.xuanyimao.poljb.index.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * javascript函数注解，程序启动时扫描器会扫描指定包下的包含JsClass注解的类，收集包含JsFunction注解的方法
 * 
 * @author liuming
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JsFunction {
	/**函数名*/
    String name();
    /**本方法的调用描述，对应文档的“公共函数调用方式，需引入static/js/common.js”，暂为使用*/
    String desc() default "";
}
