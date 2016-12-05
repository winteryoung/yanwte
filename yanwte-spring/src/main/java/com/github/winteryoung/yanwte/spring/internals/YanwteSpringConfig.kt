package com.github.winteryoung.yanwte.spring.internals

import com.github.winteryoung.yanwte.YanwteOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent

/**
 * Yanwte spring configuration, for auto configuring beans.

 * @author Winter Young
 * @since 2016/10/22
 */
@Configuration
@ComponentScan("com.github.winteryoung.yanwte.spring.internals")
open class YanwteSpringConfig : ApplicationListener<ContextRefreshedEvent> {
    @Value("\${yanwte.log.extensions.build:false}")
    private var logExtensionsBuild: String = ""

    @Bean
    open fun yanwteSpringHook(): YanwteSpringHook {
        val yanwteSpringHook = YanwteSpringHook()
        yanwteSpringHook.basePackage = ""
        return yanwteSpringHook
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent?) {
        if (event != null) {
            YanwteOptions.let {
                it.logExtensionsBuild = logExtensionsBuild.toBoolean()
            }
        }
    }
}