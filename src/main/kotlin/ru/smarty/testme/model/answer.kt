package ru.smarty.testme.model

import java.util.*

class TestPass(val testCode: String, val person: String, val test: Test) {
    val questionsWithAnswer: List<QuestionAnswer> = test.questions.mapIndexed { i, question -> QuestionAnswer(i, question, question.timeOverride ?: test.defaultTime) }.toArrayList()
    var currentQuestion = -1
        private set

    init {
        Collections.shuffle(questionsWithAnswer)
    }

    fun answer(answers: List<Int>, now: Long = System.currentTimeMillis()) {
        require(!isDone())

        this.questionsWithAnswer[currentQuestion].answer(answers, now)
    }

    fun currentQuestionData(shuffle: Boolean, now: Long = System.currentTimeMillis()): QuestionData {
        require(!isDone())

        val answer = questionsWithAnswer[currentQuestion]
        val answers = answer.question.answers.map { it.text }.withIndex().toArrayList()
        if (shuffle) {
            Collections.shuffle(answers)
        }

        val time = answer.question.timeOverride ?: test.defaultTime
        return QuestionData(
                question = answer.question.question,
                time = time,
                answers = answers,
                isMultiAnswer = answer.question.isMultiAnswer(),
                msLeft = (answer.started!! + time * 1000 - now).toInt(),
                index = currentQuestion + 1,
                total = questionsWithAnswer.size
        )
    }

    fun startNext(now: Long = System.currentTimeMillis()) {
        require(!isDone()) { "Already done!" }

        currentQuestion += 1
        if (!isDone()) {
            questionsWithAnswer[currentQuestion].start(now)
        }
    }

    fun isDone() = currentQuestion >= questionsWithAnswer.size

    data class QuestionData(val question: String, val time: Int, val isMultiAnswer: Boolean, val answers: List<IndexedValue<String>>, val msLeft: Int, val index: Int, val total: Int)

    fun calculateScore(): Double = questionsWithAnswer.map { it.score() }.sum() * 5 / questionsWithAnswer.size
}

class QuestionAnswer(val originalIndex: Int, val question: Question, val time: Int) {
    var started: Long? = null
    var answered: Long? = null
    var answers: List<Int> = emptyList()

    fun start(now: Long = System.currentTimeMillis()) {
        started = now
    }

    fun answer(answers: List<Int>, now: Long = System.currentTimeMillis()) {
        require(started != null, { "Must be started before it's answered" })
        require(answers.all { it >= 0 && it < question.answers.size }, { "incorrect answer index" })

        this.answered = now
        this.answers = answers
    }

    fun duration() = if (answered != null) answered!! - started!! else null

    fun score(): Double {
        // A more clever logic for multianswers might be implemented
        if (answered != null && duration()!! < time + 10 && question.answers.withIndex().filter { it.value.isCorrect }.map { it.index } == answers) {
            return 1.0
        } else {
            return 0.0
        }
    }
}