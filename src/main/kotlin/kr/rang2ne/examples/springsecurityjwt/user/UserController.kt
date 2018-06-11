package kr.rang2ne.examples.springsecurityjwt.user

import kr.rang2ne.examples.springsecurityjwt.common.RespError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.ExceptionHandler
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
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(reqSignInDTO.id, reqSignInDTO.password))
        } catch (e: Exception) {
            throw when(e) {
                is DisabledException -> {
                    println("User is disabled!")
                    UserException(1, "User is disabled!", e)
                }
                is BadCredentialsException -> {
                    println("Bad credentials!")
                    UserException(2, "Bad credentials!", e)
                }
                is InternalAuthenticationServiceException -> {
                    println("No User!")
                    UserException(3, "No User!", e)
                }
                else -> {
                    println("not known")
                    UserException(4, "not known", e)
                }
            }
        }

        return ResponseEntity.ok(ResponseEntity.ok(RespSignDTO(
                reqSignInDTO.id,
                userService.generateToken(reqSignInDTO.id),
                Date()
        )))
    }

    @ExceptionHandler(UserException::class)
    fun handler(e: UserException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(RespError(e.code, e.message?: "", Date()))
    }
}