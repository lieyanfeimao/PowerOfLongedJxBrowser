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
 * javascript类注解，程序启动时扫描器会扫描指定包下的包含JsClass注解的类
 * jsname为前端公共JS函数调用java方法时的前缀名  如@JsClass(jsname="Test")，js调用形式为 Java.exec("Test.xx",{}); 
 * @author liuming
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface JsClass {
	/**类实例名，可根据这个名称获取保存在扫描器的类实例对象*/
	String name() default "";
	/**JS函数名称前缀**/
	String jspre() default "";
}
