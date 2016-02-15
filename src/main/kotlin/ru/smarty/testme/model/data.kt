package ru.smarty.testme.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import java.util.*

class Test {
    lateinit var title: String
    lateinit var description: String
    lateinit var time: String
    var defaultTime: Int = 60
    var shuffleQuestions = false
    var shuffleAnswers = false

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
    @JsonView(Views.Detailed::class, Views.Serialize::class)
    var criteria: List<Criteria> = ArrayList()

    @JsonView(Views.Detailed::class, Views.Serialize::class)
    var answers: List<AnswerVariant> = ArrayList()

    @JsonIgnoreProperties(allowGetters = true)
    @JsonIgnore
    fun isMultiAnswer(): Boolean = forceMultiAnswer || answers.filter { it.correct }.size > 1

    companion object {
        val Null = Question()
        init {
            Null.question = "Null question object"
        }
    }
}

class Criteria(var text: String) {
}

class AnswerVariant(val text: String, @JsonView(Views.Admin::class, Views.Serialize::class) val correct: Boolean) {
    constructor(value: String) : this(value.trimStart('+'), value.startsWith('+')) {
    }
}