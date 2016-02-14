package ru.smarty.testme.utils

import org.springframework.security.core.context.SecurityContextHolder
import ru.smarty.testme.model.AppUser

infix inline fun <T> T?.orCreate(creation: () -> T): T = this ?: creation()

fun currentUser(): AppUser = SecurityContextHolder.getContext().authentication.principal as AppUser