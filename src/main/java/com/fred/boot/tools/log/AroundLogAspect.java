package com.fred.boot.tools.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Fred
 * @date 2021/8/7 14:27
 */
@Aspect
@Slf4j
public class AroundLogAspect {

    @Around("@annotation(aroundLog)")
    public Object aroundLog(ProceedingJoinPoint point, AroundLog aroundLog) {

        StringBuilder sb = new StringBuilder();
        StopWatch started = new StopWatch();

        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();

            sb.append("\n<===================================START===================================>\n");
            sb.append("call time:>").append(LocalDateTime.now()).append("\n");
            ObjectMapper mapper = new ObjectMapper();

            String methodName = method.getName();
            sb.append("methodName:").append(methodName).append("\n");

            Parameter[] parameters = method.getParameters();
            Object[] args = point.getArgs();
            sb.append("request:>\n");
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    String paramName = parameters[i].getName();
                    String argStr= null;
                    try {
                        argStr  = mapper.writeValueAsString(args[i]);
                    } catch (Exception e) {
                        argStr = "无法解析";
                    }
                    sb.append(paramName).append(":").append(argStr).append("\n");
                }
            }

            started.start();
            Object proceed = point.proceed();

            sb.append("response:>\n").append(mapper.writeValueAsString(proceed)).append("\n");

            return proceed;
        } catch (RuntimeException e) {
            sb.append("RuntimeException:>").append(e.getMessage()).append("\n");
            e.printStackTrace();
            throw e;
        } catch (Throwable throwable) {
            sb.append("Throwable:>").append(throwable.getMessage()).append("\n");
            throwable.printStackTrace();
            throw new RuntimeException("系统异常!");
        }finally {
            started.stop();
            sb.append("call total time(ms) :>").append(started.getTime()).append("\n");
            sb.append("<====================================END====================================>\n");
            log.info(sb.toString());
        }
    }
}
