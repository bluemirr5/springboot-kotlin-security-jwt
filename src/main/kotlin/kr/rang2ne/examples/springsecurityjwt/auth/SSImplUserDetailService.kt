package kr.rang2ne.examples.springsecurityjwt.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class SSImplUserDetailService @Autowired constructor(
) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val pass = "\$2a\$10\$J78hWCNBlE3lLJnDl3oAMOqzxvXRKYsLnioX3BMieoaYm1s3PNLUi"
        return SSImplUserDetail("gs.won", pass)
    }
}