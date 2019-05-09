package com.wangyb.learningdemo.authentication.config;

import com.wangyb.learningdemo.authentication.annotation.SysLog;
import com.wangyb.learningdemo.authentication.entity.SysLogInfo;
import com.wangyb.learningdemo.authentication.pojo.RestResult;
import com.wangyb.learningdemo.authentication.service.SysLogInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Date;

/**
 * @author wangyb
 * @Date 2019/5/6 14:27
 * Modified By:
 * Description:  注：使用ehcache缓存时切面获取不到 RestResult<?>
 */
@Aspect
@Component
@Slf4j
public class ControllerAop {

    @Autowired
    private SysLogInfoService sysLogInfoService;

    @Pointcut("execution(public com.wangyb.learningdemo.authentication.pojo.RestResult *(..))")
    public void controllerAop() {
    }

    @Before("controllerAop()")
    public void doBefore(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        // 记录下请求内容
        log.info("URL:{},RequestMethod:{},MethodName:{}.",
                request.getRequestURI(),
                request.getMethod(),
                getRequestMethodName(joinPoint));
    }

    private String getRequestMethodName(JoinPoint pjp) {
        return pjp.getTarget().getClass().getSimpleName() + "." + pjp.getSignature().getName();
    }

    @Around("controllerAop()")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
        long startTime = Instant.now().toEpochMilli();
        RestResult<?> result;
        try {
            result = (RestResult<?>) pjp.proceed();
            log.info(getRequestMethodName(pjp) + " use time:" + (Instant.now().toEpochMilli() - startTime));
        } catch (Throwable e) {
            result = handlerException(pjp, e);
        }
        return result;
    }

    @AfterReturning(value = "controllerAop()", returning = "rvt")
    public void after(JoinPoint joinPoint, RestResult<?> rvt) {
        try {
            String targetName = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            if (null == method.getAnnotation(SysLog.class)) {
                // 说明此方法没有日志注解
                return;
            }
            // 接收到请求，记录请求内容
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String name = (String) request.getAttribute(Constant.LOGIN_NAME);
            String operationType = method.getAnnotation(SysLog.class).operationType();
            String operationName = method.getAnnotation(SysLog.class).operationName();
            SysLogInfo info = new SysLogInfo();
            info.setOperationName(operationName);
            info.setOperationType(operationType);
            info.setMethod(targetName + "." + methodName + "()");
            info.setCreateBy(name);
            info.setCreateDate(new Date());
            if (rvt.getCode() == 0) {
                info.setResult(true);
            } else {
                info.setResult(false);
            }
            sysLogInfoService.insertSysLog(info);
        } catch (Exception e) {
            log.error("by wangyb-> after(JoinPoint joinPoint) errro: {}.", e.getMessage());
        }
    }

    private RestResult<?> handlerException(ProceedingJoinPoint pjp, Throwable e) {
        RestResult<?> result = new RestResult<>();
        if (e instanceof BindException) {
            result.setCode(RestResult.CHECK_FAIL);
            result.setMsg(e.getMessage());
        } else if (e instanceof AuthenticationException) {
            result.setCode(RestResult.NO_LOGIN);
            result.setMsg(e.getMessage());
        } else if (e instanceof AuthorizationException) {
            result.setCode(RestResult.NO_PERMISSION);
            result.setMsg(e.getMessage());
        } else {
            log.error(pjp.getSignature() + " error ", e);
            result.setMsg(e.toString());
            result.setCode(RestResult.UNKNOWN_EXCEPTION);
        }
        return result;
    }
}
