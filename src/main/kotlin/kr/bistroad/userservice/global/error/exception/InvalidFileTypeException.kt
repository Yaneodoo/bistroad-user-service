package kr.bistroad.userservice.global.error.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid file type")
class InvalidFileTypeException : RuntimeException()