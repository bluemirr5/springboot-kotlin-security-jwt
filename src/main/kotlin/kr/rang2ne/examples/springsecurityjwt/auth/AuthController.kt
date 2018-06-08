package kr.rang2ne.examples.springsecurityjwt.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController
@Autowired constructor(
        val authenticationManager: AuthenticationManager,
        val authService: AuthService
) {
    @Value("\${jwt.header}")
    private val tokenHeader: String? = null

    @PostMapping(value = ["/pub/login"])
    fun signIn(
            @RequestBody authDTO: AuthDTO
    ): ResponseEntity<*> {
        println("in controller")
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(authDTO.id, authDTO.password))
        } catch (e: Exception) {
            throw when(e) {
                is DisabledException -> CustomAuthException("User is disabled!", e)
                is BadCredentialsException -> CustomAuthException("Bad credentials!", e)
                else -> e
            }
        }

        return ResponseEntity.ok(authService.generateToken(authDTO.id))
    }
}