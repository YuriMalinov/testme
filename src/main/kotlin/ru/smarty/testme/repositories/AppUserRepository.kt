package ru.smarty.testme.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.smarty.testme.model.AppUser

@Repository
interface AppUserRepository : JpaRepository<AppUser, Int> {
    fun findByLogin(login: String): AppUser?
}