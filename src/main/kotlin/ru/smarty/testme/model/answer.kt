package ru.smarty.testme.model

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

@Suppress("unused")
@Entity
@Table(schema = "tm", name = "test_pass")
open class TestPass() {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    @get:Size(min = 1)
    open var testCode: String = ""

    @get:Size(min = 1)
    open var testTitle: String = ""

    @get:Size(min = 1)
    open var testDescription: String = ""

    open var testDefaultTime: Int = 0

    @get:Size(min = 1)
    open var code: String = ""

    open var shuffleAnswers = false

    @get:ManyToOne
    open lateinit var appUser: AppUser

    @get:JsonView(Views.FullAdmin::class)
    @get:OneToMany(orphanRemoval = true, cascade = arrayOf(CascadeType.ALL), targetEntity = QuestionAnswer::class, mappedBy = "testPass")
    @get:OrderBy("num")
    @get:BatchSize(size = 100)
    open var questionsWithAnswer: List<QuestionAnswer> = ArrayList()

    @get:JsonView(Views.Admin::class)
    open var currentQuestionNum = -1

    open var created: Date = Date()

    constructor(testCode: String, code: String, user: AppUser, test: Test) : this() {
        questionsWithAnswer = test.questions.mapIndexed { i, question ->
            QuestionAnswer(this, i, question)
        }.toMutableList()

        if (test.shuffleQuestions) {
            Collections.shuffle(questionsWithAnswer)
        }

        questionsWithAnswer.forEachIndexed { i, questionAnswer -> questionAnswer.num = i }

        this.testCode = testCode
        this.testTitle = test.title
        this.testDescription = test.description
        this.testDefaultTime = test.defaultTime
        this.shuffleAnswers = test.shuffleAnswers
        this.code = code
        this.appUser = user
    }

    fun answer(answers: List<Int>, comment: String? = null, textAnswer: String? = null, now: Long = System.currentTimeMillis()) {
        require(!isDone())

        this.questionsWithAnswer[currentQuestionNum].answer(answers, comment, textAnswer, now)
    }

    fun currentQuestionData(now: Long = System.currentTimeMillis()): QuestionData {
        require(!isDone())

        val answer = questionsWithAnswer[currentQuestionNum]
        val answers = answer.question.answers.map { it.text }.withIndex().toMutableList()
        if (shuffleAnswers) {
            Collections.shuffle(answers)
        }

        val time = answer.time
        return QuestionData(
                question = answer.question.question,
                time = time,
                answers = answers,
                isMultiAnswer = answer.question.isMultiAnswer(),
                isOpenQuestion = answer.question.isOpenQuestion(),
                msLeft = (answer.started!!.time + time * 1000 - now).toInt(),
                index = currentQuestionNum + 1,
                total = questionsWithAnswer.size
        )
    }

    fun startNext(now: Long = System.currentTimeMillis()) {
        require(!isDone()) { "Already done!" }

        currentQuestionNum += 1
        if (!isDone()) {
            questionsWithAnswer[currentQuestionNum].start(now)
        }
    }

    @Transient
    fun isDone() = currentQuestionNum >= questionsWithAnswer.size

    data class QuestionData(
            val question: String,
            val time: Int,
            val isMultiAnswer: Boolean,
            val isOpenQuestion: Boolean,
            val answers: List<IndexedValue<String>>,
            val msLeft: Int,
            val index: Int,
            val total: Int)

    @JsonView(Views.Admin::class)
    @JsonGetter("score")
    fun score(): ScoreData {
        val categoryToScore = HashMap<String, Pair<Double, Double>>()
        var totalGood = 0.0
        var totalMax = 0.0
        var toBeGraded = 0

        for (qa in questionsWithAnswer) {
            if (qa.question.isOpenQuestion() && qa.mark == null) {
                toBeGraded += 1
                continue
            }

            val score = qa.score()
            totalGood += score
            totalMax += qa.question.weight

            val existing = categoryToScore[qa.question.category]
            categoryToScore[qa.question.category] = Pair((existing?.first ?: 0.0) + score, (existing?.second ?: 0.0) + qa.question.weight)
        }

        val categoryScore = categoryToScore.map {
            val (category, pair) = it;
            CategoryScore(category, 5.0 * pair.first / Math.max(1.0, pair.second))
        }.toMutableList()

        Collections.sort(categoryScore, { a, b -> a.category.compareTo(b.category) })

        return ScoreData(5.0 * totalGood / totalMax, categoryScore, toBeGraded)
    }

    data class ScoreData(
            val totalScore: Double,
            val perCategoryScore: List<CategoryScore>,
            val questionsToBeGraded: Int
    )

    data class CategoryScore(val category: String, val score: Double)
}

@Suppress("unused")
@Entity
@Table(schema = "tm", name = "question_answer")
open class QuestionAnswer() {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    open var started: Date? = null
    open var answered: Date? = null

    @get:JsonIgnore
    @get:ManyToOne
    open var testPass: TestPass? = null

    @get:Type(type = "ru.smarty.testme.utils.IntArrayUserType")
    open var answers: List<Int> = emptyList()
    open var originalIndex: Int = 0

    open var num: Int = 0

    @get:Transient
    open var question: Question = Question.Null

    open var comment: String? = null

    open var textAnswer: String? = null

    open var mark: Double? = null

    /**
     * This field is for hibernate. The full json copy of question is stored.
     * It has drawbacks: it'll be hard to compare answers for different questions.
     * Still it wouldn't complicate schema. Because there might be different versions
     * of question, you need to store them all and point to specific version.
     */
    @Suppress("unused")
    open var questionJson: String
        @JsonIgnore get() = mapper.writeValueAsString(question)
        set(value: String) {
            question = mapper.readValue(value, Question::class.java)
        }

    open val time: Int
        @Transient get() = question.timeOverride ?: testPass!!.testDefaultTime

    constructor(testPass: TestPass, originalIndex: Int, question: Question) : this() {
        this.originalIndex = originalIndex
        this.question = question
        this.testPass = testPass
    }

    fun start(now: Long = System.currentTimeMillis()) {
        started = Date(now)
    }

    fun answer(answers: List<Int>, comment: String? = null, textAnswer: String? = null, now: Long = System.currentTimeMillis()) {
        require(started != null, { "Must be started before it's answered" })
        require(answers.all { it >= 0 && it < question.answers.size }, { "incorrect answer index" })

        this.answered = Date(now)
        this.answers = answers
        this.comment = comment
        this.textAnswer = textAnswer
    }

    @JsonView(Views.FullAdmin::class)
    @JsonGetter("duration")
    fun duration() = if (answered != null) answered!!.time - started!!.time else null

    @JsonGetter
    fun timeout() = answered != null && duration()!! > (time + 10) * 1000

    @JsonView(Views.FullAdmin::class)
    @JsonGetter("score")
    fun score(): Double {
        // A more clever logic for multianswers might be implemented
        if (question.answers.size == 0) {
            return mark ?: 0.0
        } else if (answered != null && !timeout()) {
            if (question.advancedWeight) {
                val correctIndexes = question.answers.withIndex().filter { it.value.correct }.map { it.index }
                val answersSum = answers.map { if (it in correctIndexes) 1 else -1 }.sum()
                val missedSum = correctIndexes.filter { it !in answers }.size
                return Math.max(0.0, (answersSum - missedSum) * 1.0 / correctIndexes.size) * question.weight
            } else if (question.answers.withIndex().filter { it.value.correct }.map { it.index }.toSet() == answers.toSet()) {
                return question.weight
            } else {
                return 0.0
            }
        } else {
            return 0.0
        }
    }

    companion object {
        val mapper = ObjectMapper()

        init {
            mapper.registerModule(KotlinModule())
            //            mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            //            mapper.setConfig(mapper.serializationConfig.withView(Views.Serialize::class.java))
        }
    }
}