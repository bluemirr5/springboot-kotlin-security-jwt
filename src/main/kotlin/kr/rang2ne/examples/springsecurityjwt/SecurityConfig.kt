package kr.rang2ne.examples.springsecurityjwt

import kr.rang2ne.examples.springsecurityjwt.springsecurity.SSImplUserDetailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig @Autowired constructor(
        val ssImplUserDetailService: SSImplUserDetailService
) : WebSecurityConfigurerAdapter() {
    @Value("\${jwt.header}")
    private val tokenHeader: String? = null
    @Value("\${jwt.secret}")
    private val secret: String? = null


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth
                .userDetailsService<UserDetailsService>(ssImplUserDetailService)
                .passwordEncoder(passwordEncoder())
    }

    override fun configure(httpSecurity: HttpSecurity) {
        super.configure(httpSecurity)
        httpSecurity.csrf().disable()
                .exceptionHandling().authenticationEntryPoint { _, response, _ ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                }.and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/h2-console/**/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
        httpSecurity.headers().frameOptions().sameOrigin().cacheControl()
    }

    override fun configure(web: WebSecurity) {
        super.configure(web)
        web
                .ignoring()
                .antMatchers(
                        HttpMethod.POST,
                        "/pub/**"
                )
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                )

                // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
                .and()
                .ignoring()
                .antMatchers("/h2-console/**/**")
    }
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