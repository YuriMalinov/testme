package ru.smarty.testme.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import ru.smarty.testme.repositories.TestPassRepository

@Controller
@RequestMapping("/admin")
open class AdminController @Autowired constructor(
        private val testPassRepository: TestPassRepository
) {
    @RequestMapping("", method = arrayOf(GET))
    open fun index() = "admin/index"

    @RequestMapping("/test-pass-list", method = arrayOf(GET))
    open fun testPassList(page: Pageable) = testPassRepository.findAll(page)
}