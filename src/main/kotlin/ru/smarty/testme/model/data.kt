package ru.smarty.testme.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonView

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
    @JsonView(Views.Detailed::class)
    lateinit var answers: List<AnswerVariant>

    fun isMultiAnswer(): Boolean = forceMultiAnswer || answers.filter { it.isCorrect }.size > 1
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