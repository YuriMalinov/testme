package ru.smarty.testme.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
open class MvcConfig {
    @Bean open fun kotlinModule() = KotlinModule()

//    @Bean open fun objectMapperCustomize(): Jackson2ObjectMapperBuilder {
//        val builder = Jackson2ObjectMapperBuilder()
//        builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
//        return builder
//    }
}