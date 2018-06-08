package kr.rang2ne.examples.springsecurityjwt.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController
@Autowired constructor(
        val authenticationManager: AuthenticationManager,
        val authService: AuthService,
        val passwordEncoder: PasswordEncoder
) {
    @Value("\${jwt.header}")
    private val tokenHeader: String? = null


    @PostMapping(value = ["\${jwt.route.authentication.path}"])
    fun signIn(
            @RequestBody authModel: AuthModel
    ): ResponseEntity<*> {
        println("in controller")
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(authModel.id, authModel.password))
        } catch (e: Exception) {
            throw when(e) {
                is DisabledException -> RAuthException("User is disabled!", e)
                is BadCredentialsException -> RAuthException("Bad credentials!", e)
                else -> e
            }
        }

        // load userdetail
//        val userDetail = SSImplUserDetail(authModel.id, authModel.password)
        return ResponseEntity.ok(authService.generateToken(authModel.id))
    }
}