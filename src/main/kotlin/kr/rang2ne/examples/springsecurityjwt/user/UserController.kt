package kr.rang2ne.examples.springsecurityjwt.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class UserController
@Autowired constructor(
        val authenticationManager: AuthenticationManager,
        val userService: UserService,
        val userRepository: UserRepository,
        val passwordEncoder: PasswordEncoder
) {
    @PostMapping(value = ["/pub/signUp"])
    fun signUp(
            @RequestBody reqSignUpDTO: ReqSignUpDTO
    ): ResponseEntity<*> {
        val pass4Store = passwordEncoder.encode(reqSignUpDTO.password)
        userRepository.save(User(
                reqSignUpDTO.id,
                pass4Store,
                reqSignUpDTO.nickName,
                reqSignUpDTO.profileImg,
                Date()
        ))
        return ResponseEntity.ok().build<Any>()
    }


    @PostMapping(value = ["/pub/signin"])
    fun signIn(
            @RequestBody reqSignInDTO: ReqSignInDTO
    ): ResponseEntity<*> {

        // 인증 검증
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(reqSignInDTO.id, reqSignInDTO.password))
        } catch (e: Exception) {
            throw when(e) {
                is DisabledException -> UserException("User is disabled!", e)
                is BadCredentialsException -> UserException("Bad credentials!", e)
                is InternalAuthenticationServiceException -> UserException("No User!", e)
                else -> e
            }
        }

        // 토큰 생성 및 발행
        return ResponseEntity.ok(userService.generateToken(reqSignInDTO.id))
    }
}