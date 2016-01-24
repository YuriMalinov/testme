package ru.smarty.testme.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore

class Test {
    lateinit var title: String
    lateinit var description: String
    lateinit var time: String
    var defaultTime: Int = 60
    var questions: List<Question> = arrayListOf()
}

class Question {
    lateinit var question: String
    var timeOverride: Int? = null
    lateinit var answers: List<AnswerVariant>

    fun isMultiAnswer(): Boolean = answers.filter { it.isCorrect }.size > 1
}

class AnswerVariant(val text: String, val isCorrect: Boolean) {
    companion object {
        @JvmStatic
        @JsonCreator
        fun create(value: String): AnswerVariant {
            val isCorrect = value.startsWith("+", true)
            return AnswerVariant(value.trimStart('+'), isCorrect)
        }
    }
}