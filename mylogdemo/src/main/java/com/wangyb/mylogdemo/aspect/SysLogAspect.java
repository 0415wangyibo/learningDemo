package com.wangyb.mylogdemo.aspect;

import com.google.gson.Gson;
import com.wangyb.mylogdemo.pojo.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

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

    // 切面，配置通知
    @Around(value = "sysLogPointCut()")
    private Object saveSysLog(ProceedingJoinPoint joinPoint) {

        // 从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();
        //获得参数
        StringBuilder detailBuilder = new StringBuilder();
        Object[] objects = joinPoint.getArgs();
        if (null != objects && objects.length > 0) {
            for (int i = 0; i < objects.length; i++) {
                if (null != objects[i]) {
                    detailBuilder.append(objects[i].toString() + " ");
                }
            }
        }
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String queryString = request.getQueryString();
//        log.info("QueryString:" + queryString);
        MyLog myLog = method.getAnnotation(MyLog.class);
        if (!StringUtils.isEmpty(myLog)) {
            log.info("操作名：" + myLog.value());
        }
        //可结合登录认证获得用户名
        log.info("用户名：test");
        log.info("访问controller时间："+LocalDateTime.now());
        String detail = detailBuilder.toString();
        //超过255长度进行截取
        if (!StringUtils.isEmpty(detail) && detail.length() > 255) {
            detail = detail.substring(0, 254);
        }
        log.info("参数：" + detail);
        Object object = null;
        try {
            object = joinPoint.proceed();
            Gson gson = new Gson();
            String gsonString = gson.toJson(object);
            RestResult restResult = gson.fromJson(gsonString, RestResult.class);
            log.debug(restResult.toString());
            log.info("是否访问成功："+(restResult.getCode() == 0 ? 1 : 0));
            log.info("访问结果备注："+restResult.getMsg());
        } catch (Throwable throwable) {
            log.error("获取方法结果失败");
        }
        return object;
    }
}

