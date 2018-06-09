package kr.rang2ne.examples.springsecurityjwt.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>
