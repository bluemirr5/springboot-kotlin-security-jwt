package kr.rang2ne.examples.springsecurityjwt.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AuthController
@Autowired constructor(
        val authenticationManager: AuthenticationManager,
        val userDetailsService: UserDetailsService
) {
    @Value("\${jwt.header}")
    private val tokenHeader: String? = null
    @Value("\${jwt.expiration}")
    private val expiration: Long? = null
    @Value("\${jwt.secret}")
    private val secret: String? = null

    @PostMapping(value = ["\${jwt.route.authentication.path}"])
    fun signIn(
            @RequestBody authModel: AuthModel
    ): ResponseEntity<*> {

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
        val userDetail = SSImplUserDetail(authModel)
        val claims = HashMap<String, Any>()

        val createdDate = Date()
        val expirationDate = Date(createdDate.time + (expiration!! * 1000))

        val token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetail.getUsername())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact()

        return ResponseEntity.ok(token)
    }
}