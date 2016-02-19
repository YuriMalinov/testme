package ru.smarty.testme.controllers

import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import ru.smarty.testme.model.TestPass
import ru.smarty.testme.model.Views
import ru.smarty.testme.repositories.TestPassRepository
import ru.smarty.testme.repositories.TestRepository
import ru.smarty.testme.utils.currentUser

@Suppress("unused")
@Controller
class TestPassController @Autowired constructor(
        private val testRepository: TestRepository,
        private val passRepository: TestPassRepository
) {

    data class ApplyRequest(val testCode: String)

    @ResponseBody
    @RequestMapping("/data/apply", method = arrayOf(RequestMethod.POST))
    fun apply(@RequestBody request: ApplyRequest): Value<String> {
        val test = testRepository.testByCode(request.testCode) ?: throw NotFound("Can't find test with code [${request.testCode}]")

        val passCode = java.lang.Long.toUnsignedString(Math.round(Math.random() * Int.MAX_VALUE), 16)
        val pass = TestPass(request.testCode, passCode, currentUser(), test)
        pass.startNext()

        passRepository.save(pass)

        return Value(passCode)
    }

    private fun findPass(passCode: String): TestPass =
            passRepository.findByCode(passCode) ?: throw NotFound("Can't find pass with code [$passCode]")

    @ResponseBody
    @RequestMapping("/data/current-question", method = arrayOf(RequestMethod.POST))
    @JsonView(Views.Public::class)
    fun currentQuestion(passCode: String): CodeWithQuestionData {
        val pass = findPass(passCode)

        if (pass.isDone()) {
            throw BadRequest("Pass [$passCode] is already done.")
        }

        return CodeWithQuestionData(
                testName = pass.testTitle,
                passCode = passCode,
                done = pass.isDone(),
                questionData = pass.currentQuestionData()
        )
    }

    data class CodeWithQuestionData(val testName: String, val passCode: String, val done: Boolean, val questionData: TestPass.QuestionData?)

    data class SubmitAnswerRequest(val passCode: String, val answers: List<Int>, val comment: String?, val textAnswer: String?)

    @ResponseBody
    @RequestMapping("/data/submit-answer", method = arrayOf(RequestMethod.POST))
    fun submitAnswer(@RequestBody request: SubmitAnswerRequest): Boolean {
        val (passCode, answers) = request

        val pass = findPass(passCode)

        if (pass.isDone()) {
            throw BadRequest("Pass [$passCode] is already done.")
        }

        pass.answer(answers, request.comment, request.textAnswer)
        if (!pass.isDone()) {
            pass.startNext()
        }
        passRepository.save(pass)

        return pass.isDone()
    }

    @ResponseBody
    @RequestMapping("/data/score", method = arrayOf(RequestMethod.GET))
    fun score(passCode: String): TestPass.ScoreData {
        val pass = findPass(passCode)

        if (!pass.isDone()) {
            throw BadRequest("Pass [$passCode] is not yet done.")
        }

        return pass.score()
    }
}