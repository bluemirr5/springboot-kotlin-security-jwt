package kr.rang2ne.examples.springsecurityjwt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig @Autowired constructor() : WebSecurityConfigurerAdapter() {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    override fun authenticationManagerBean() = super.authenticationManagerBean()!!

    override fun configure(httpSecurity: HttpSecurity?) {
        super.configure(httpSecurity)
        httpSecurity?.
                csrf()?.disable()?.
                exceptionHandling()?.authenticationEntryPoint { _, response, _ ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                }?.and()?.
                sessionManagement()?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)?.and()?.
                authorizeRequests()?.
                antMatchers("/h2-console/**/**")?.permitAll()?.
                antMatchers("/auth/**")?.permitAll()?.
                anyRequest()?.authenticated()

        httpSecurity?.addFilterBefore(object : OncePerRequestFilter() {
            override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
                TODO("JwtAuthorizationTokenFilter 따라 구현")
            }

        }, UsernamePasswordAuthenticationFilter::class.java)
    }
}