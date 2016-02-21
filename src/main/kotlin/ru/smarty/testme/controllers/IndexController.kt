package ru.smarty.testme.controllers

import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.ResponseBody
import ru.smarty.testme.model.Test
import ru.smarty.testme.model.TestPass
import ru.smarty.testme.model.Views
import ru.smarty.testme.repositories.TestPassRepository
import ru.smarty.testme.repositories.TestRepository
import ru.smarty.testme.utils.currentUser
import java.io.CharArrayWriter
import java.io.PrintWriter

@Suppress("unused")
@Controller
open class IndexController @Autowired constructor(
        private val testRepository: TestRepository,
        private val passRepository: TestPassRepository
) {
    @RequestMapping("/")
    fun index() = "index"

    @ResponseBody
    @RequestMapping("/data/tests")
    @JsonView(Views.Public::class)
    fun tests(): List<TestData> {
        val passes = passRepository.findByAppUser(currentUser()).groupBy { it.testCode }

        return testRepository.tests.map { TestData(it.key, it.value, passes[it.key] ?: emptyList()) }
    }

    data class TestData(val code: String, val test: Test, val previousPasses: List<TestPass>)

    @ResponseBody
    @RequestMapping("/data/user")
    fun user() = currentUser()

    @RequestMapping("/admin/refresh", method = arrayOf(GET))
    fun refresh(): String {
        try {
            testRepository.updateTests()
            return "OK"
        } catch (e: Exception) {
            val writer = PrintWriter(CharArrayWriter())
            e.printStackTrace(writer)
            return writer.toString()
        }
    }
}
