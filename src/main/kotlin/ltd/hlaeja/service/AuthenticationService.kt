package ltd.hlaeja.service

import ltd.hlaeja.jwt.service.PrivateJwtService
import org.springframework.http.HttpStatus.LOCKED
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Service
class AuthenticationService(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
    private val privateJwtService: PrivateJwtService,
) {
    fun authenticate(
        username: String,
        password: CharSequence,
    ): Mono<String> = accountService.getUserByUsername(username)
        .flatMap {
            if (!passwordEncoder.matches(password, it.password)) {
                Mono.error(ResponseStatusException(UNAUTHORIZED, "Invalid password"))
            } else if (!it.enabled) {
                Mono.error(ResponseStatusException(LOCKED, "Account disabled"))
            } else {
                Mono.just(it)
            }
        }
        .map { accountEntity ->
            privateJwtService.sign(
                "id" to accountEntity.id!!,
                "username" to accountEntity.username,
                "role" to accountEntity.roles,
            )
        }
}
