package kr.rang2ne.examples.springsecurityjwt.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SSImplUserDetail : UserDetails {
    val id: String
    val pass: String

    constructor(authModel: AuthModel) {
        id = authModel.id
        pass = authModel.password
    }

    override fun getUsername() = id
    override fun getPassword() = pass

    override fun isEnabled() = true
    override fun isCredentialsNonExpired() = true
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()
}