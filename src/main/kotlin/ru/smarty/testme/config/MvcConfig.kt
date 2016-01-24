package ru.smarty.testme.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MvcConfig {
    @Bean open fun kotlinModule() = KotlinModule()
}