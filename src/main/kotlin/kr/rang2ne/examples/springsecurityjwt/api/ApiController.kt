package kr.rang2ne.examples.springsecurityjwt.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApiController {
    @PostMapping(value = ["/api/test"])
    fun testApi(): ResponseEntity<*> {
        return ResponseEntity.ok("이 메시지는 인증된 api를 호출한 결과 입니다.")
    }
}