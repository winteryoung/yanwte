package com.github.winteryoung.yanwte.spring.internals;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Yanwte spring 配置。
 *
 * @author fanshen
 * @since 2016/10/22
 */
@Configuration
@ComponentScan("com.github.winteryoung.yanwte.spring.internals")
public class YanwteSpringConfig {
    @Bean
    public YanwteSpringHook yanwteSpringHook() {
        YanwteSpringHook yanwteSpringHook = new YanwteSpringHook();
        yanwteSpringHook.setBasePackage("");
        return yanwteSpringHook;
    }
}
