package kr.rang2ne.examples.springsecurityjwt.springsecurity

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
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


        val obj = object : OncePerRequestFilter() {
            override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
                val requestHeader = request.getHeader(tokenHeader)
                if(requestHeader != null && requestHeader.startsWith("Bearer ")) {
                    val requestToken = requestHeader.substring(7)
                    val claims = Jwts.parser()
                            .setSigningKey(secret)
                            .parseClaimsJws(requestToken)
                            .body
                    val username = claims.subject
                    if(username != null && SecurityContextHolder.getContext().authentication == null) {
                        val userDetail = userDetailsService().loadUserByUsername(username)
                        if(claims.subject.equals(userDetail.username) && !claims.expiration.before(Date())) {
                            val authentication = UsernamePasswordAuthenticationToken(userDetail, null, userDetail.authorities)
                            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                            SecurityContextHolder.getContext().authentication = authentication
                        }
                    }
                }
                filterChain.doFilter(request, response)
            }
        }

        httpSecurity.addFilterBefore(obj, UsernamePasswordAuthenticationFilter::class.java)

        httpSecurity.headers().frameOptions().sameOrigin().cacheControl()
    }

    override fun configure(web: WebSecurity) {
        super.configure(web)
        web
                .ignoring()
                .antMatchers(HttpMethod.POST, "/pub/**")
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
                .and()
                .ignoring()
                .antMatchers("/h2-console/**/**")
    }
}