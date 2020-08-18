package kr.bistroad.userservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Username already exists")
class UsernameExistException : RuntimeException()