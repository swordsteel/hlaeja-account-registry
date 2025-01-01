package ltd.hlaeja.controller

import java.util.UUID
import ltd.hlaeja.library.accountRegistry.Account
import ltd.hlaeja.service.AccountService
import ltd.hlaeja.util.toAccountEntity
import ltd.hlaeja.util.toAccountResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AccountController(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
) {

    @GetMapping("/account-{uuid}")
    fun getAccount(
        @PathVariable uuid: UUID,
    ): Mono<Account.Response> = accountService.getUserById(uuid)
        .map { it.toAccountResponse() }

    @PostMapping("/account")
    fun addAccount(
        @RequestBody request: Account.Request,
    ): Mono<Account.Response> = accountService.addAccount(request.toAccountEntity(passwordEncoder))
        .map { it.toAccountResponse() }
}
