package kr.rang2ne.examples.springsecurityjwt.common

import java.util.*

data class RespError (
        val code: Int,
        val message: String,
        val time: Date
)