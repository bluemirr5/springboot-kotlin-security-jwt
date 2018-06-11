package kr.rang2ne.examples.springsecurityjwt.user

import java.util.*

data class ReqSignInDTO(
        val id: String,
        val password: String
)

data class RespSignDTO(
        val id: String,
        val token: String,
        val loginTime: Date
)

data class ReqSignUpDTO(
        val id: String,
        val password: String,
        val nickName: String,
        val profileImg: String
)
