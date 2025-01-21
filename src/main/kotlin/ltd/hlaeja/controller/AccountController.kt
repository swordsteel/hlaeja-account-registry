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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class AccountController(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
) {
    companion object {
        const val DEFAULT_PAGE: Int = 1
        const val DEFAULT_SIZE: Int = 25
    }

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

    @GetMapping("/accounts")
    fun getDefaultAccounts(): Flux<Account.Response> = getAccounts(DEFAULT_PAGE, DEFAULT_SIZE)

    @GetMapping("/accounts/page-{page}")
    fun getAccountsPage(
        @PathVariable page: Int,
    ): Flux<Account.Response> = getAccounts(page, DEFAULT_SIZE)

    @GetMapping("/accounts/page-{page}/show-{size}")
    fun getAccountsPageSize(
        @PathVariable page: Int,
        @PathVariable size: Int,
    ): Flux<Account.Response> = getAccounts(page, size)

    private fun getAccounts(
        page: Int,
        size: Int,
    ): Flux<Account.Response> = accountService.getAccounts(page, size)
        .map { it.toAccountResponse() }
}
