package kr.rang2ne.examples.springsecurityjwt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig @Autowired constructor() : WebSecurityConfigurerAdapter() {
    @Value("\${jwt.header}")
    private val tokenHeader: String? = null
    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    override fun authenticationManagerBean() = super.authenticationManagerBean()!!

//    override fun configure(httpSecurity: HttpSecurity?) {
//        super.configure(httpSecurity)
//        httpSecurity?.
//                csrf()?.disable()?.
//                exceptionHandling()?.authenticationEntryPoint { _, response, _ ->
//                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
//                }?.and()?.
//                sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)?.and()?.
//                authorizeRequests()?.
//                antMatchers("/h2-console/**/**")?.permitAll()?.
//                antMatchers("/auth/**")?.permitAll()?.
//                anyRequest()?.authenticated()
//
//        httpSecurity?.addFilterBefore(object : OncePerRequestFilter() {
//            override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
//                fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T {
//                    val claims = Jwts.parser()
//                            .setSigningKey(secret)
//                            .parseClaimsJws(token)
//                            .body
//                    return claimsResolver.apply(claims)
//                }
//
//                val requestHeader = request.getHeader(tokenHeader)
//                if(requestHeader?.startsWith("Bearer ") == true) {
//                    val reqToken = requestHeader.substring(7)
//                    val userId = getClaimFromToken(reqToken, Function<Claims, String> { it.subject })
//                    if (SecurityContextHolder.getContext().authentication == null) {
//                    }
//                } else {
//
//                }
//                TODO("JwtAuthorizationTokenFilter 따라 구현")
//            }
//
//        }, UsernamePasswordAuthenticationFilter::class.java)
//    }

}