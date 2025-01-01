package ltd.hlaeja.service

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID
import ltd.hlaeja.entity.AccountEntity
import ltd.hlaeja.repository.AccountRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
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
        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))

    fun getUserByUsername(
        username: String,
    ): Mono<AccountEntity> = accountRepository.findByUsername(username)
        .doOnNext { log.debug { "Get account ${it.id} for username $username" } }
        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND)))

    fun addAccount(
        accountEntity: AccountEntity,
    ): Mono<AccountEntity> = accountRepository.save(accountEntity)
        .doOnNext { log.debug { "Added new type: $it.id" } }
        .onErrorResume {
            log.debug { it.localizedMessage }
            when {
                it is DuplicateKeyException -> Mono.error(ResponseStatusException(HttpStatus.CONFLICT))
                else -> Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST))
            }
        }
}
