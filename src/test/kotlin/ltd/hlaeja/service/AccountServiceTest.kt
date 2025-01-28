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
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class AccountServiceTest {
    companion object {
        val account = UUID.fromString("00000000-0000-0000-0000-000000000002")
        val accountEntity = AccountEntity(
            account,
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            true,
            "username",
            "password",
            "ROLE_TEST",
        )
        val accounts = Flux.just(
            accountEntity.copy(username = "username1"),
            accountEntity.copy(username = "username2"),
            accountEntity.copy(username = "username3"),
        )
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

    @Test
    fun `get account by username - success`() {
        // given
        every { accountRepository.findByUsername(any()) } returns Mono.just(accountEntity)

        // when
        StepVerifier.create(accountService.getUserByUsername("username"))
            .expectNext(accountEntity)
            .verifyComplete()

        // then
        verify { accountRepository.findByUsername(any()) }
    }

    @Test
    fun `get account by username - fail does not exist`() {
        // given
        every { accountRepository.findByUsername(any()) } returns Mono.empty()

        // when
        StepVerifier.create(accountService.getUserByUsername("username"))
            .expectError(ResponseStatusException::class.java)
            .verify()

        // then
        verify { accountRepository.findByUsername(any()) }
    }

    @Test
    fun `add account - success`() {
        // given
        every { accountRepository.save(any()) } returns Mono.just(accountEntity)

        // when
        StepVerifier.create(accountService.addAccount(accountEntity))
            .expectNext(accountEntity)
            .verifyComplete()

        // then
        verify { accountRepository.save(any()) }
    }

    @Test
    fun `add account - fail duplicated user`() {
        // given
        every { accountRepository.save(any()) } returns Mono.error(DuplicateKeyException("Test"))

        // when
        StepVerifier.create(accountService.addAccount(accountEntity))
            .expectErrorMatches { error ->
                error is ResponseStatusException && error.statusCode == CONFLICT
            }
            .verify()

        // then
        verify { accountRepository.save(any()) }
    }

    @Test
    fun `add account - fail`() {
        // given
        every { accountRepository.save(any()) } returns Mono.error(RuntimeException())

        // when
        StepVerifier.create(accountService.addAccount(accountEntity))
            .expectErrorMatches { error ->
                error is ResponseStatusException && error.statusCode == BAD_REQUEST
            }
            .verify()

        // then
        verify { accountRepository.save(any()) }
    }

    @Test
    fun `get accounts - limit size success`() {
        // given
        every { accountRepository.findAll() } returns accounts

        // when
        StepVerifier.create(accountService.getAccounts(1, 2))
            .expectNextMatches { accountEntity ->
                accountEntity.username == "username1"
            }
            .expectNextMatches { accountEntity ->
                accountEntity.username == "username2"
            }
            .verifyComplete()

        // then
        verify { accountRepository.findAll() }
    }

    @Test
    fun `get accounts - negative page fail`() {
        // given
        every { accountRepository.findAll() } returns accounts

        // when
        StepVerifier.create(accountService.getAccounts(-1, 10))
            .expectErrorMatches { error ->
                error is ResponseStatusException && error.statusCode == BAD_REQUEST
            }
            .verify()

        // then
        verify { accountRepository.findAll() }
    }

    @Test
    fun `get accounts - negative size fail`() {
        // given
        every { accountRepository.findAll() } returns accounts

        // when
        StepVerifier.create(accountService.getAccounts(1, -10))
            .expectErrorMatches { error ->
                error is ResponseStatusException && error.statusCode == BAD_REQUEST
            }
            .verify()

        // then
        verify { accountRepository.findAll() }
    }

    @Test
    fun `update account - success`() {
        // given
        every { accountRepository.save(any()) } returns Mono.just(accountEntity)

        // when
        StepVerifier.create(accountService.updateAccount(accountEntity))
            .expectNext(accountEntity)
            .verifyComplete()

        // then
        verify { accountRepository.save(any()) }
    }

    @Test
    fun `update account - fail duplicated user`() {
        // given
        every { accountRepository.save(any()) } returns Mono.error(DuplicateKeyException("Test"))

        // when
        StepVerifier.create(accountService.updateAccount(accountEntity))
            .expectErrorMatches { error ->
                error is ResponseStatusException && error.statusCode == CONFLICT
            }
            .verify()

        // then
        verify { accountRepository.save(any()) }
    }

    @Test
    fun `update account - fail`() {
        // given
        every { accountRepository.save(any()) } returns Mono.error(RuntimeException())

        // when
        StepVerifier.create(accountService.updateAccount(accountEntity))
            .expectErrorMatches { error ->
                error is ResponseStatusException && error.statusCode == BAD_REQUEST
            }
            .verify()

        // then
        verify { accountRepository.save(any()) }
    }
}
