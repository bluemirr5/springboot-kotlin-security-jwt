package kr.rang2ne.examples.springsecurityjwt.user

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class User(
        @Id
        val id: String = "",
        val password: String = "",
        val nickName: String = "",
        val profileImg: String = "",
        val regDate: Date = Date()
)