package ru.smarty.testme.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonView
import java.util.*

class Test {
    lateinit var title: String
    lateinit var description: String
    lateinit var time: String
    var defaultTime: Int = 60

    @JsonView(Views.Detailed::class)
    var questions: List<Question> = arrayListOf()
}

class Question {
    lateinit var question: String
    var forceMultiAnswer: Boolean = false
    var timeOverride: Int? = null
    var weight = 1
    var category = "general"
    /**
     * For multi-answer questions: if true, result = (sum guessed answers - sum false guesses) / sum correct answers in question.
     */
    var advancedWeight = false

    /**
     * If not empty then answers must be empty. It means open question. Criteria describes how to check answers.
     */
    @JsonView(Views.Detailed::class)
    var criteria: List<Criteria> = ArrayList()

    @JsonView(Views.Detailed::class)
    var answers: List<AnswerVariant> = ArrayList()

    fun isMultiAnswer(): Boolean = forceMultiAnswer || answers.filter { it.isCorrect }.size > 1
}

class Criteria(var text: String) {
    companion object {
        @JvmStatic
        @JsonCreator
        fun create(value: String): Criteria {
            return Criteria(value)
        }
    }
}

class AnswerVariant(val text: String, @JsonView(Views.Admin::class) val isCorrect: Boolean) {
    companion object {
        @JvmStatic
        @JsonCreator
        fun create(value: String): AnswerVariant {
            val isCorrect = value.startsWith("+", true)
            return AnswerVariant(value.trimStart('+'), isCorrect)
        }
    }
}