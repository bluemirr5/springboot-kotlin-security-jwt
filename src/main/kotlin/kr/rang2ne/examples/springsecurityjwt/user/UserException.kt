package kr.rang2ne.examples.springsecurityjwt.user

class UserException(val code: Int, message: String, cause: Throwable) : RuntimeException(message, cause)