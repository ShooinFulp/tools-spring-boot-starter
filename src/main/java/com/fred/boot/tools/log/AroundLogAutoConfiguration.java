package com.fred.boot.tools.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author Fred
 * @date 2021/8/7 14:51
 */
@Configuration
@EnableConfigurationProperties(AroundLogProperties.class)
@ConditionalOnProperty(
        value = {"aroundlog.enabled"},
        havingValue = "true"
)
public class AroundLogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AroundLogAspect aroundLogAspect() {
        return new AroundLogAspect();
    }

}
