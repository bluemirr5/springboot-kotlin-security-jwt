package kr.rang2ne.examples.springsecurityjwt.springsecurity

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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
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

                }
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