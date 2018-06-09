package kr.rang2ne.examples.springsecurityjwt.user

data class ReqSignInDTO(
        val id: String,
        val password: String
)

data class ReqSignUpDTO(
        val id: String,
        val password: String,
        val nickName: String,
        val profileImg: String
)