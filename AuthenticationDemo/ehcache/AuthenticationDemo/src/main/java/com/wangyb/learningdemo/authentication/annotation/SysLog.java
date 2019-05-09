package com.wangyb.learningdemo.authentication.annotation;

import java.lang.annotation.*;

/**
 * @author wangyb
 * @Date 2019/5/6 14:24
 * Modified By:
 * Description:
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /** 要执行的操作类型比如：add操作 **/
    public String operationType() default "";

    /** 要执行的具体操作比如：添加用户 **/
    public String operationName() default "";
}
