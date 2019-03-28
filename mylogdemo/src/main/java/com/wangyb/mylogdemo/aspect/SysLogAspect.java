package com.wangyb.mylogdemo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @since 2019/1/17 09:23
 * Modified By:
 * Description: 可以使用这个方法将controller层的日志获取到并存入文件或者数据库，此处不做示范
 */
@Aspect
@Component
@Slf4j
public class SysLogAspect {

    // 定义切点，在注解的位置切入代码
    @Pointcut("@annotation(com.wangyb.mylogdemo.aspect.MyLog)")
    public void sysLogPointCut() {

    }

    // 切面，此处可将获得的日志信息存入数据库
    @AfterReturning("sysLogPointCut()")
    private void saveSysLog(JoinPoint joinPoint) {

        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        MyLog myLog = method.getAnnotation(MyLog.class);
        if (!StringUtils.isEmpty(myLog)) {
            log.info("获得日志：" + myLog.value());
        }
    }
}

