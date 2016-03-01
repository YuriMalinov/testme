package ru.smarty.testme.controllers

import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import ru.smarty.testme.model.Test
import ru.smarty.testme.model.TestPass
import ru.smarty.testme.model.Views
import ru.smarty.testme.repositories.TestFileRepository
import ru.smarty.testme.repositories.TestPassRepository
import ru.smarty.testme.repositories.TestRepository
import ru.smarty.testme.utils.currentUser
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Suppress("unused")
@Controller
open class IndexController @Autowired constructor(
        private val testRepository: TestRepository,
        private val testFileRepository: TestFileRepository,
        private val passRepository: TestPassRepository,
        private val entityManager: EntityManager
) {
    @RequestMapping("/")
    open fun index() = "index"

    @ResponseBody
    @RequestMapping("/data/tests")
    @JsonView(Views.Public::class)
    open fun tests(): List<TestData> {
        val allPasses = passRepository.findByAppUser(currentUser())
        val passes = allPasses.groupBy { it.test.id }

        return testRepository.findAll().map { TestData(it.id, it, passes[it.id] ?: emptyList()) }
    }

    data class TestData(val id: Int, val test: Test, val previousPasses: List<TestPass>)

    @ResponseBody
    @RequestMapping("/data/user")
    open fun user() = currentUser()

    @RequestMapping("/admin/import-files")
    @Transactional
    @ResponseBody
    open fun importFiles(): Value<String> {
        val sb = StringBuilder()

        testFileRepository.tests.forEach {
            val test = it.value
            test.rebind()

            entityManager.persist(test)
            entityManager.flush()

            sb.append(test.id).append(": ").append(test.title).append("\n")
        }

        return Value(sb.toString())
    }
}
