package ltd.hlaeja.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityWebFilterChain(
        serverHttpSecurity: ServerHttpSecurity,
    ): SecurityWebFilterChain = serverHttpSecurity
        .authorizeExchange(::authorizeExchange)
        .httpBasic(::httpBasic)
        .formLogin(::formLogin)
        .csrf(::csrf)
        .build()

    private fun csrf(
        csrf: CsrfSpec,
    ) = csrf.disable()

    private fun formLogin(
        formLogin: FormLoginSpec,
    ) = formLogin.disable()

    private fun httpBasic(
        httpBasic: HttpBasicSpec,
    ) = httpBasic.disable()

    private fun authorizeExchange(
        authorizeExchange: AuthorizeExchangeSpec,
    ) = authorizeExchange.anyExchange().permitAll()
}
