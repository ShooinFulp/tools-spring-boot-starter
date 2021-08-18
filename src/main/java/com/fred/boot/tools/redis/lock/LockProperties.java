package com.fred.boot.tools.redis.lock;

import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Fred
 * @date 2021/8/5 15:15
 */
@Data
@ConfigurationProperties(LockProperties.PREFIX)
public class LockProperties {

    public static final String PREFIX = "lock";
    private Boolean enabled;
    private String address;
    private String password;
    private Integer database;
    private Integer poolSize;
    private Integer idleSize;
    private Integer idleTimeout;
    private Integer connectionTimeout;
    private Integer timeout;
    private Mode mode;
    private String masterAddress;
    private String[] slaveAddress;
    private String masterName;
    private String[] sentinelAddress;
    private String[] nodeAddress;

    public LockProperties(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        this.enabled = Boolean.FALSE;
        this.address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        this.password = redisProperties.getPassword();
        this.database = redisProperties.getDatabase();
        this.poolSize = pool.getMaxActive();
        this.idleSize = pool.getMaxIdle();
        this.idleTimeout = Integer.valueOf(String.valueOf(pool.getMaxWait().getSeconds()));
        this.connectionTimeout = Integer.valueOf(String.valueOf(redisProperties.getTimeout().toMillis()));
        this.timeout = Integer.valueOf(String.valueOf(redisProperties.getTimeout().toMillis()));
        this.mode = Mode.single;
    }

    public static enum Mode {
        single,
        master,
        sentinel,
        cluster;
    }
}
