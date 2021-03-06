package ru.smarty.testme.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFound(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.FORBIDDEN)
class Forbidden(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequest(message: String) : RuntimeException(message)