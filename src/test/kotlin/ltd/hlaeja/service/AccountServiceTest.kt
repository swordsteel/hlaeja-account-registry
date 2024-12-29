package ltd.hlaeja.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.ZonedDateTime
import java.util.UUID
import ltd.hlaeja.entity.AccountEntity
import ltd.hlaeja.repository.AccountRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class AccountServiceTest {
    companion object {
        val account = UUID.fromString("00000000-0000-0000-0000-000000000002")
    }

    private lateinit var accountRepository: AccountRepository
    private lateinit var accountService: AccountService

    @BeforeEach
    fun setup() {
        accountRepository = mockk()
        accountService = AccountService(accountRepository)
    }

    @Test
    fun `get account by id - success`() {
        // given
        val accountEntity = AccountEntity(
            account,
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            true,
            "username",
            "password",
            "ROLE_TEST",
        )

        every { accountRepository.findById(any(UUID::class)) } returns Mono.just(accountEntity)

        // when
        StepVerifier.create(accountService.getUserById(account))
            .expectNext(accountEntity)
            .verifyComplete()

        // then
        verify { accountRepository.findById(any(UUID::class)) }
    }

    @Test
    fun `get account by id - fail does not exist`() {
        // given
        every { accountRepository.findById(any(UUID::class)) } returns Mono.empty()

        // when
        StepVerifier.create(accountService.getUserById(account))
            .expectError(ResponseStatusException::class.java)
            .verify()

        // then
        verify { accountRepository.findById(any(UUID::class)) }
    }
}
