package com.wangyb.mylogdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/2/25 10:19
 * Modified By:
 * Description:
 */
@Component
@Aspect
@Slf4j
public class ControllerAspect {

    @Pointcut("execution(public com.wangyb.mylogdemo.pojo.RestResult com.wangyb.mylogdemo.controller.*.*(..))")
    public void restResult() {

    }

    @Around("restResult()")

    public Object around(ProceedingJoinPoint jp) throws Throwable {
        log.info("by wangyb: {}", getMethInfo(jp));
        return jp.proceed();
    }

    // 打印 controller 方法参数
    private String getMethInfo(ProceedingJoinPoint jp) {
        String className = jp.getSignature().getDeclaringType().getSimpleName();
        // LoginContorller 参数不打印
        String methodName = jp.getSignature().getName();
        String[] parameterNames = ((MethodSignature) jp.getSignature()).getParameterNames();
        StringBuilder sb = new StringBuilder().append(className).append(".").append(methodName).append("->");
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                String value = jp.getArgs()[i] != null ? jp.getArgs()[i].toString() : "null";
                sb.append(parameterNames[i] + ":" + value + "; ");
            }
        }
        return sb.toString();
    }
}
