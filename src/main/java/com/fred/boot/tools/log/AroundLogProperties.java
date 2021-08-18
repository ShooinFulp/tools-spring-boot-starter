package com.fred.boot.tools.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Fred
 * @date 2021/8/7 14:51
 */
@Data
@ConfigurationProperties(AroundLogProperties.PREFIX)
public class AroundLogProperties {
    public static final String PREFIX = "aroundlog";
    private Boolean enabled;
}
