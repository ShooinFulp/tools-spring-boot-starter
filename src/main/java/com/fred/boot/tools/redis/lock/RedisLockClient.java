package com.fred.boot.tools.redis.lock;


import com.fred.boot.tools.function.CheckedSupplier;

import java.util.concurrent.TimeUnit;

public interface RedisLockClient {
    boolean tryLock(String lockName, LockType lockType, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    void unLock(String lockName, LockType lockType);

    <T> T lock(String lockName, LockType lockType, long waitTime, long leaseTime, TimeUnit timeUnit, CheckedSupplier<T> supplier);

    default <T> T lockFair(String lockName, long waitTime, long leaseTime, CheckedSupplier<T> supplier) {
        return this.lock(lockName, LockType.FAIR, waitTime, leaseTime, TimeUnit.SECONDS, supplier);
    }

    default <T> T lockReentrant(String lockName, long waitTime, long leaseTime, CheckedSupplier<T> supplier) {
        return this.lock(lockName, LockType.REENTRANT, waitTime, leaseTime, TimeUnit.SECONDS, supplier);
    }
}