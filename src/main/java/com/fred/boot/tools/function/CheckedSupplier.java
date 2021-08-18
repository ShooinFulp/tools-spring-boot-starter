package com.fred.boot.tools.function;

/**
 * @author Fred
 * @date 2021/8/5 16:50
 */
@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Throwable;
}
