package ltd.hlaeja.util

import java.time.ZonedDateTime
import ltd.hlaeja.entity.AccountEntity
import ltd.hlaeja.library.accountRegistry.Account
import org.springframework.http.HttpStatus.EXPECTATION_FAILED
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException

fun AccountEntity.toAccountResponse(): Account.Response = Account.Response(
    id ?: throw ResponseStatusException(EXPECTATION_FAILED),
    updatedAt,
    enabled,
    username,
    roles.split(","),
)

fun Account.Request.toAccountEntity(
    passwordEncoder: PasswordEncoder,
): AccountEntity = AccountEntity(
    id = null,
    createdAt = ZonedDateTime.now(),
    updatedAt = ZonedDateTime.now(),
    enabled = enabled,
    username = username,
    password = passwordEncoder.encode(password),
    roles = roles.joinToString(","),
)
