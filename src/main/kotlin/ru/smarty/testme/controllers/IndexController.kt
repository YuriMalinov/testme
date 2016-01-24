package ru.smarty.testme.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.ResponseBody
import ru.smarty.testme.model.Test
import ru.smarty.testme.model.TestPass
import ru.smarty.testme.repositories.TestRepository
import java.util.*

@Controller
open class IndexController @Autowired constructor(
        private val testRepository: TestRepository
) {
    private val passes = HashMap<String, TestPass>()

    @RequestMapping("/")
    fun index() = "index"

    @ResponseBody
    @RequestMapping("/data/tests")
    fun tests(): List<TestWithCode> = testRepository.tests.map { TestWithCode(it.key, it.value) }

    data class TestWithCode(val code: String, val test: Test)


    class ApplyRequest {
        lateinit var userName: String
        lateinit var testCode: String
    }

    /**
     * Dummy class to fool spring MVC to return String.
     */
    @Suppress("unused")
    class Value<T>(val value: T)

    @ResponseBody
    @RequestMapping("/data/apply", method = arrayOf(POST))
    fun apply(@RequestBody request: ApplyRequest): Value<String> {
        val test = testRepository.testByCode(request.testCode) ?: throw NotFound("Can't find test with code [${request.testCode}]")
        val pass = TestPass(request.testCode, request.userName, test)

        val passCode = java.lang.Long.toUnsignedString(Math.round(Math.random() * Int.MAX_VALUE), 16)
        passes[passCode] = pass

        pass.startNext()

        return Value(passCode)
    }

    @ResponseBody
    @RequestMapping("/data/current-question", method = arrayOf(POST))
    fun startNext(passCode: String): CodeWithQuestionData {
        val pass = passes[passCode] ?: throw NotFound("Can't find pass with code [$passCode]")

        if (pass.isDone()) {
            throw BadRequest("Pass [$passCode] is already done.")
        }

        return CodeWithQuestionData(pass.test.title, passCode, pass.isDone(), pass.currentQuestionData(true))
    }


    data class SubmitAnswerRequest @JsonCreator constructor(val passCode: String, val answers: List<Int>)

    @ResponseBody
    @RequestMapping("/data/submit-answer", method = arrayOf(POST))
    fun submitAnswer(@RequestBody request: SubmitAnswerRequest): Boolean {
        val (passCode, answers) = request

        val pass = passes[passCode] ?: throw NotFound("Can't find pass with code [$passCode]")

        if (pass.isDone()) {
            throw BadRequest("Pass [$passCode] is already done.")
        }

        pass.answer(answers)
        pass.startNext()

        return pass.isDone()
    }

    @ResponseBody
    @RequestMapping("/data/score", method = arrayOf(GET))
    fun score(passCode: String): Double {
        val pass = passes[passCode] ?: throw NotFound("Can't find pass with code [$passCode]")

        if (pass.isDone()) {
            throw BadRequest("Pass [$passCode] is not yet done.")
        }

        return pass.calculateScore()

    }

    data class CodeWithQuestionData(val testName: String, val passCode: String, val done: Boolean, val questionData: TestPass.QuestionData?)
}