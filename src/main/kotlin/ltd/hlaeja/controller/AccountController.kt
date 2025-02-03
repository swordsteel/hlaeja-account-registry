package ltd.hlaeja.controller

import java.util.UUID
import ltd.hlaeja.validator.ValidAccount
import ltd.hlaeja.entity.AccountEntity
import ltd.hlaeja.library.accountRegistry.Account
import ltd.hlaeja.service.AccountService
import ltd.hlaeja.util.toAccountEntity
import ltd.hlaeja.util.toAccountResponse
import ltd.hlaeja.util.updateAccountEntity
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.CREATED
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
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

    @PutMapping("/account-{uuid}")
    fun updateAccount(
        @PathVariable uuid: UUID,
        @RequestBody @ValidAccount request: Account.Request,
    ): Mono<Account.Response> = accountService.getUserById(uuid)
        .map { user ->
            user.updateAccountEntity(request, passwordEncoder)
                .also { if (hasChange(user, it)) throw ResponseStatusException(ACCEPTED) }
        }
        .flatMap { accountService.updateAccount(it) }
        .map { it.toAccountResponse() }

    @PostMapping("/account")
    @ResponseStatus(CREATED)
    fun addAccount(
        @RequestBody @ValidAccount request: Account.Request,
    ): Mono<Account.Response> = accountService.addAccount(request.toAccountEntity(passwordEncoder))
        .map { it.toAccountResponse() }

    private fun hasChange(
        user: AccountEntity,
        update: AccountEntity,
    ): Boolean = user.password == update.password &&
        user.username == update.username &&
        user.enabled == update.enabled &&
        user.roles == update.roles
}
