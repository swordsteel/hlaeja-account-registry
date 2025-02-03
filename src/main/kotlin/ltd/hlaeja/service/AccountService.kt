package ltd.hlaeja.service

import io.github.oshai.kotlinlogging.KotlinLogging
import java.lang.IllegalArgumentException
import java.util.UUID
import ltd.hlaeja.entity.AccountEntity
import ltd.hlaeja.repository.AccountRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Service
class AccountService(
    private val accountRepository: AccountRepository,
) {

    fun getUserById(
        uuid: UUID,
    ): Mono<AccountEntity> = accountRepository.findById(uuid)
        .doOnNext { log.debug { "Get account ${it.id}" } }
        .switchIfEmpty(Mono.error(ResponseStatusException(NOT_FOUND)))

    fun getUserByUsername(
        username: String,
    ): Mono<AccountEntity> = accountRepository.findByUsername(username)
        .doOnNext { log.debug { "Get account ${it.id} for username $username" } }
        .switchIfEmpty(Mono.error(ResponseStatusException(NOT_FOUND)))

    fun addAccount(
        accountEntity: AccountEntity,
    ): Mono<AccountEntity> = accountRepository.save(accountEntity)
        .doOnNext { log.debug { "Added new type: $it.id" } }
        .onErrorResume(::onSaveError)

    fun getAccounts(page: Int, size: Int): Flux<AccountEntity> = try {
        accountRepository.findAll()
            .skip((page - 1).toLong() * size)
            .take(size.toLong())
            .doOnNext { log.debug { "Retrieved accounts $page with size $size" } }
    } catch (e: IllegalArgumentException) {
        Flux.error(ResponseStatusException(BAD_REQUEST, null, e))
    }

    fun updateAccount(
        accountEntity: AccountEntity,
    ): Mono<AccountEntity> = accountRepository.save(accountEntity)
        .doOnNext { log.debug { "updated users: $it.id" } }
        .onErrorResume(::onSaveError)

    private fun onSaveError(throwable: Throwable): Mono<out AccountEntity> {
        log.debug { throwable.localizedMessage }
        return when {
            throwable is DuplicateKeyException -> Mono.error(ResponseStatusException(CONFLICT))
            else -> Mono.error(ResponseStatusException(BAD_REQUEST))
        }
    }
}
