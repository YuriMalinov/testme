package ru.smarty.testme.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType.ALL

@Entity
@Table(name = "test", schema = "tm")
open class Test {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    open lateinit var title: String
    open lateinit var description: String
    open var timeDescription: String? = null
    open var defaultTime: Int = 60
    open var shuffleQuestions = false
    open var shuffleAnswers = false

    open var created: Date = Date()

    @get:OneToMany(orphanRemoval = true, cascade = arrayOf(ALL), mappedBy = "test")
    @JsonView(Views.Detailed::class)
    open var questions: List<Question> = arrayListOf()

    fun rebind() {
        questions.forEach { q ->
            q.test = this
            q.rebind()
        }
    }
}

@Entity
@Table(name = "question", schema = "tm")
open class Question {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    @JsonIgnore
    @get:ManyToOne
    open lateinit var test: Test

    open lateinit var question: String
    open var forceMultiAnswer: Boolean = false
    open var timeOverride: Int? = null
    open var weight = 1.0
    open var category = "general"
    /**
     * For multi-answer questions: if true, result = (sum guessed answers - sum false guesses) / sum correct answers in question.
     */
    open var advancedWeight = false

    /**
     * If not empty then answers must be empty. It means open question. Criteria describes how to check answers.
     */
    @get:OneToMany(orphanRemoval = true, cascade = arrayOf(ALL), mappedBy = "question")
    @get:JsonView(Views.Detailed::class, Views.FullAdmin::class)
    open var criteria: List<Criteria> = ArrayList()

    @get:OneToMany(orphanRemoval = true, cascade = arrayOf(ALL), mappedBy = "question")
    @get:JsonView(Views.Detailed::class, Views.FullAdmin::class)
    open var answers: List<AnswerVariant> = ArrayList()

    @JsonIgnoreProperties(allowGetters = true)
    @JsonIgnore
    @Transient
    fun isMultiAnswer(): Boolean = forceMultiAnswer || answers.filter { it.correct }.size > 1


    @JsonView(Views.Detailed::class, Views.FullAdmin::class)
    @JsonIgnoreProperties(allowGetters = true)
    @JsonIgnore
    @Transient
    fun isOpenQuestion() = answers.size == 0

    fun rebind() {
        answers.forEach { a -> a.question = this }
        criteria.forEach { c -> c.question = this }
    }

    companion object {
        val Null = Question()

        init {
            Null.question = "Null question object"
        }
    }
}


@Entity
@Table(name = "question_criteria", schema = "tm")
open class Criteria() {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    @JsonIgnore
    @get:ManyToOne
    open lateinit var question: Question

    open lateinit var text: String

    constructor(text: String) : this() {
        this.text = text
    }
}


@Entity
@Table(name = "question_answer_variant", schema = "tm")
open class AnswerVariant() {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    @JsonIgnore
    @get:ManyToOne
    open lateinit var question: Question

    open lateinit var text: String

    @JsonView(Views.Admin::class)
    open var correct: Boolean = false

    constructor(value: String) : this() {
        this.text = value.trimStart('+')
        this.correct = value.startsWith('+')
    }
}