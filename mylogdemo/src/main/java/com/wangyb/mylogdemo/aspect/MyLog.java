package com.wangyb.mylogdemo.aspect;

import java.lang.annotation.*;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/28 11:53
 * Modified By:
 * Description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyLog {
    String value() default "";
}
