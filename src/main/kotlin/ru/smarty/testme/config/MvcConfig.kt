package ru.smarty.testme.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
open class MvcConfig : WebMvcConfigurerAdapter() {
    @Bean open fun kotlinModule() = KotlinModule()

    @Bean open fun resourceHeadersFilter() = object: Filter {
        override fun destroy() {
        }

        override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
            request as HttpServletRequest
            response as HttpServletResponse
            if (request.servletPath.startsWith("/external")) {
                response.addHeader("Cache-Control", "max-age=2592000")
            }
            chain.doFilter(request, response)
        }

        override fun init(p0: FilterConfig?) {
        }
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/login").setViewName("login")
    }
}