package ru.smarty.testme

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class TestMeApplication {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(TestMeApplication::class.java, *args)
        }
    }
}
