package com.fred.boot.tools.redis.lock;

/**
 * 锁类型
 */
public enum LockType {
    /**
     * 可重入锁
     */
    REENTRANT,
    /**
     * 公平锁
     */
    FAIR
}