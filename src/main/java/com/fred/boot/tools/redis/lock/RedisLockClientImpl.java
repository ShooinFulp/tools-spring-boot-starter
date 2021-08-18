package com.fred.boot.tools.redis.lock;

import com.fred.boot.tools.function.CheckedSupplier;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class RedisLockClientImpl implements RedisLockClient {
    private final RedissonClient redissonClient;

    //private final LongAdder lock = new LongAdder();
    //private final LongAdder unLock = new LongAdder();

    @Override
    public boolean tryLock(String lockName, LockType lockType, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        RLock lock = this.getLock(lockName, lockType);
        return lock.tryLock(waitTime, leaseTime, timeUnit);
    }

    @Override
    public void unLock(String lockName, LockType lockType) {
        RLock lock = this.getLock(lockName, lockType);
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }

    }

    private RLock getLock(String lockName, LockType lockType) {
        RLock lock;
        if (LockType.REENTRANT == lockType) {
            lock = this.redissonClient.getLock(lockName);
        } else {
            lock = this.redissonClient.getFairLock(lockName);
        }

        return lock;
    }

    @Override
    public <T> T lock(String lockName, LockType lockType, long waitTime, long leaseTime, TimeUnit timeUnit, CheckedSupplier<T> supplier) {
        T o;
        try {
            boolean result = this.tryLock(lockName, lockType, waitTime, leaseTime, timeUnit);
            if (!result) {
                throw new RuntimeException("业务繁忙!请稍后再试!");
            }
            //lock.increment();
            o = supplier.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException("系统异常");
        } finally {
            this.unLock(lockName, lockType);
            //unLock.increment();
        }
        //System.out.println(lock.longValue()+"===="+unLock.longValue()+"解锁成功?"+redissonClient.getLock(lockName).isLocked());
        return o;
    }

    public RedisLockClientImpl(final RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
}