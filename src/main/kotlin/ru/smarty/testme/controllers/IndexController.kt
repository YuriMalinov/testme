package ru.smarty.testme.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import ru.smarty.testme.model.Test
import ru.smarty.testme.repositories.TestRepository

@Controller
open class IndexController @Autowired constructor(
        private val testRepository: TestRepository
) {
    @RequestMapping("/")
    fun index() = "index"

    @ResponseBody
    @RequestMapping("/data/tests")
    fun tests(): List<TestWithCode> = testRepository.tests.map { TestWithCode(it.key, it.value) }

    data class TestWithCode(val code: String, val test: Test)
}