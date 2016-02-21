package ru.smarty.testme.controllers

import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.ResponseBody
import ru.smarty.testme.model.QuestionAnswer
import ru.smarty.testme.model.Views
import ru.smarty.testme.repositories.QuestionAnswerRepository
import ru.smarty.testme.repositories.TestPassRepository
import ru.smarty.testme.utils.currentUser

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/admin")
open class AdminController @Autowired constructor(
        private val testPassRepository: TestPassRepository,
        private val answerRepository: QuestionAnswerRepository
) {
    @RequestMapping("")
    fun index(model: Model): String {
        return "admin"
    }

    @JsonView(Views.Admin::class)
    @RequestMapping("/test-pass", method = arrayOf(GET))
    @ResponseBody
    open fun testPassList() = testPassRepository.findAllByOrderByIdDesc()

    @JsonView(Views.FullAdmin::class)
    @RequestMapping("/test-pass/{id}", method = arrayOf(GET))
    @ResponseBody
    open fun testPass(@PathVariable("id") id: Int) = testPassRepository.findOne(id)


    data class GradeAnswerRequest(val id: Int, val mark: Double?)

    @Transactional
    @RequestMapping("/question-answer/{id}", method = arrayOf(POST), params = arrayOf("grade=true"))
    @ResponseBody
    open fun gradeAnswer(@RequestBody grade: GradeAnswerRequest): QuestionAnswer {
        val answer = answerRepository.getOne(grade.id) ?: throw NotFound("Can't find QuestionAnswer with id [${grade.id}]")
        answer.mark = if (grade.mark == null) null else Math.min(grade.mark, answer.question.weight * 1.0)
        answer.markedBy = currentUser()
        return answerRepository.save(answer)
    }
}