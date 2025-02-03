package ltd.hlaeja.controller

import jakarta.validation.constraints.Min
import ltd.hlaeja.library.accountRegistry.Account
import ltd.hlaeja.service.AccountService
import ltd.hlaeja.util.toAccountResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/accounts")
class AccountsController(
    private val accountService: AccountService,
) {
    companion object {
        const val DEFAULT_PAGE: Int = 1
        const val DEFAULT_SIZE: Int = 25
    }

    @GetMapping
    fun getDefaultAccounts(): Flux<Account.Response> = getAccounts(DEFAULT_PAGE, DEFAULT_SIZE)

    @GetMapping("/page-{page}")
    fun getAccountsPage(
        @PathVariable @Min(1) page: Int,
    ): Flux<Account.Response> = getAccounts(page, DEFAULT_SIZE)

    @GetMapping("/page-{page}/show-{size}")
    fun getAccountsPageSize(
        @PathVariable @Min(1) page: Int,
        @PathVariable @Min(1) size: Int,
    ): Flux<Account.Response> = getAccounts(page, size)

    private fun getAccounts(
        page: Int,
        size: Int,
    ): Flux<Account.Response> = accountService.getAccounts(page, size)
        .map { it.toAccountResponse() }
}
