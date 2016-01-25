package ru.smarty.testme.controllers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.util.FastByteArrayOutputStream
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.ResponseBody
import ru.smarty.testme.model.Test
import ru.smarty.testme.model.TestPass
import ru.smarty.testme.model.Views
import ru.smarty.testme.repositories.TestRepository
import java.io.CharArrayWriter
import java.io.PrintWriter
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
    @JsonView(Views.Public::class)
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
    @JsonView(Views.Public::class)
    fun startNext(passCode: String): CodeWithQuestionData {
        val pass = passes[passCode] ?: throw NotFound("Can't find pass with code [$passCode]")

        if (pass.isDone()) {
            throw BadRequest("Pass [$passCode] is already done.")
        }

        return CodeWithQuestionData(
                testName = pass.test.title,
                passCode = passCode,
                done = pass.isDone(),
                questionData = pass.currentQuestionData(true)
        )
    }

    data class CodeWithQuestionData(val testName: String, val passCode: String, val done: Boolean, val questionData: TestPass.QuestionData?)


    data class SubmitAnswerRequest(val passCode: String, val answers: List<Int>)

    @ResponseBody
    @RequestMapping("/data/submit-answer", method = arrayOf(POST))
    fun submitAnswer(@RequestBody request: SubmitAnswerRequest): Boolean {
        val (passCode, answers) = request

        val pass = passes[passCode] ?: throw NotFound("Can't find pass with code [$passCode]")

        if (pass.isDone()) {
            throw BadRequest("Pass [$passCode] is already done.")
        }

        pass.answer(answers)
        if (!pass.isDone()) {
            pass.startNext()
        }

        return pass.isDone()
    }

    @ResponseBody
    @RequestMapping("/data/score", method = arrayOf(GET))
    fun score(passCode: String): Double {
        val pass = passes[passCode] ?: throw NotFound("Can't find pass with code [$passCode]")

        if (!pass.isDone()) {
            throw BadRequest("Pass [$passCode] is not yet done.")
        }

        return pass.calculateScore()
    }

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
