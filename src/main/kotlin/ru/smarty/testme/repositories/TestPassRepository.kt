package ru.smarty.testme.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smarty.testme.model.AppUser
import ru.smarty.testme.model.TestPass

@Repository
interface TestPassRepository : JpaRepository<TestPass, Int> {
    fun findByCode(code: String): TestPass?

    fun findAllByOrderByIdDesc(): List<TestPass>

    fun findByAppUser(user: AppUser): List<TestPass>
}