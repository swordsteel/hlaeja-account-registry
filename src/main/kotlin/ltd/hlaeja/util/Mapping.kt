package ltd.hlaeja.util

import ltd.hlaeja.entity.AccountEntity
import ltd.hlaeja.library.accountRegistry.Account
import org.springframework.http.HttpStatus.EXPECTATION_FAILED
import org.springframework.web.server.ResponseStatusException

fun AccountEntity.toAccountResponse(): Account.Response = Account.Response(
    id ?: throw ResponseStatusException(EXPECTATION_FAILED),
    updatedAt,
    enabled,
    username,
    roles.split(","),
)

