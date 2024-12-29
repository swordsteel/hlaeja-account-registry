package ltd.hlaeja.service

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.UUID
import ltd.hlaeja.entity.AccountEntity
import ltd.hlaeja.repository.AccountRepository
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
}
