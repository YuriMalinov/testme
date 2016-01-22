package ru.smarty.testme.model

import java.util.*

class TestPass(val testCode: String, val person: String, test: Test) {
    val answers: List<QuestionAnswer> = test.questions.mapIndexed { i, question -> QuestionAnswer(i, question) }.toArrayList()

    init {
        Collections.shuffle(answers)
    }
}

class QuestionAnswer(val originalIndex: Int, val question: Question) {
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

    fun isCorrect() = answers == question.answers.withIndex().filter { it.value.isCorrect }.map { it.index }
}