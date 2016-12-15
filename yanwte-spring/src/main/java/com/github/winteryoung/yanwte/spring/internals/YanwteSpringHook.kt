package com.github.winteryoung.yanwte.spring.internals

import com.github.winteryoung.yanwte.YanwteException
import com.github.winteryoung.yanwte.YanwtePlugin
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Define a bean of this class in your spring application context.
 * This hook will integrate Spring to Yanwte.
 *
 * **Note**, the [basePackage] of your program is required to initialize this class.
 * Be *very careful* to set this right. You may cause other part of the system wrong
 * if you set this wrong. For example, if your classes are all located under
 * `com.yourcompany.yourdepartment.yourproject`, then set this to [basePackage].
 *
 * @author Winter Young
 * @since 2016/1/23
 */
class YanwteSpringHook : InitializingBean, ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    /**
     * The base package of your program. e.g. The base package
     * of yanwte is `com.github.winteryoung.yanwte`
     */
    var basePackage: String? = null

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun afterPropertiesSet() {
        val packageName = basePackage ?: throw YanwteException("The base package of your program is required")
        val springPlugin = SpringPlugin(applicationContext)

        YanwtePlugin.registerPlugin(springPlugin, packageName)
    }
}