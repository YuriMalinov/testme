package ru.smarty.testme.controllers

import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.ResponseBody
import ru.smarty.testme.model.Views
import ru.smarty.testme.repositories.QuestionAnswerRepository
import ru.smarty.testme.repositories.TestPassRepository

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/admin")
open class AdminController @Autowired constructor(
        private val testPassRepository: TestPassRepository,
        private val answerRepository: QuestionAnswerRepository
) {
    @RequestMapping("/")
    fun index(model: Model): String {
        // Actual "view":
        model.addAttribute("mainScript", "/js/admin.js")
        return "index"
    }

    @ResponseBody
    @RequestMapping("/test-pass-list", method = arrayOf(GET))
    open fun testPassList(page: Pageable) = testPassRepository.findAll(page)

    @JsonView(Views.FullAdmin::class)
    @RequestMapping("/test-pass/:id", method = arrayOf(GET))
    @ResponseBody
    open fun testPass(@PathVariable("id") id: Int) = testPassRepository.findOne(id)

    data class GradeRequest(val answerId: Int, val mark: Double)

    @Transactional
    @RequestMapping("/grade-answer", method = arrayOf(POST))
    open fun gradeAnswer(grade: GradeRequest): Value<String> {
        val answer = answerRepository.getOne(grade.answerId) ?: throw NotFound("Can't find QuestionAnswer with id [${grade.answerId}]")
        answer.mark = grade.mark
        answerRepository.save(answer)

        return Value("OK")
    }
}