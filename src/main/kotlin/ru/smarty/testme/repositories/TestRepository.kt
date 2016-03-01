package ru.smarty.testme.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smarty.testme.model.Test

@Repository
interface TestRepository : JpaRepository<Test, Int> {
}