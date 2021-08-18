package com.fred.boot.tools.redis.lock;

import com.fred.boot.tools.spel.ExpressionEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author fred
 */
@Aspect
@Slf4j
public class RedisLockAspect  {
    private static final ExpressionEvaluator<String> EVALUATOR = new ExpressionEvaluator<>();
    private final RedisLockClient redisLockClient;

    @Around("@annotation(redisLock)")
    public Object aroundRedisLock(ProceedingJoinPoint point, Lock redisLock) {
        log.info("======================into lock=======================");
        String lockName = redisLock.value();
        Assert.hasText(lockName, "@Lock value must have length; it must not be null or empty");
        String lockParam = redisLock.param();
        String lockKey;
        if (StringUtils.isNotBlank(lockParam)) {
            String evalAsText = this.evalLockParam(point, lockParam);
            lockKey = lockName + ':' + evalAsText;
        } else {
            lockKey = lockName;
        }

        LockType lockType = redisLock.type();
        long waitTime = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();
        TimeUnit timeUnit = redisLock.timeUnit();

        return this.redisLockClient.lock(lockKey, lockType, waitTime, leaseTime, timeUnit, point::proceed);
    }

    /**
     * 解析EL表达式
     * @param point 切入点
     * @param lockParam 需要解析的EL表达式
     * @return 解析出的值
     */
    private String evalLockParam(ProceedingJoinPoint point, String lockParam) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        Object[] args = point.getArgs();
        Object target = point.getTarget();
        Class<?> targetClass = target.getClass();
        EvaluationContext context = EVALUATOR.createEvaluationContext(target, target.getClass(), method, args);
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        return EVALUATOR.condition(lockParam, elementKey, context, String.class);
    }

    public RedisLockAspect(final RedisLockClient redisLockClient) {
        this.redisLockClient = redisLockClient;
    }
}