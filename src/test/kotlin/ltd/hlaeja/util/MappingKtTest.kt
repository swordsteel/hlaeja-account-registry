package ltd.hlaeja.util

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.test.assertFailsWith
import ltd.hlaeja.entity.AccountEntity
import ltd.hlaeja.library.accountRegistry.Account
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.server.ResponseStatusException

@ExtendWith(SoftAssertionsExtension::class)
class MappingKtTest {
    companion object {
        val account = UUID.fromString("00000000-0000-0000-0000-000000000000")
        val utc = ZoneId.of("UTC")
        val timestamp: ZonedDateTime = ZonedDateTime.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0, 1), utc)
        val originalTimestamp: ZonedDateTime = ZonedDateTime.of(LocalDateTime.of(1000, 1, 1, 0, 0, 0, 1), utc)
        val originalUser = AccountEntity(
            id = account,
            username = "username",
            enabled = true,
            roles = "ROLE_TEST",
            password = "password",
            createdAt = originalTimestamp,
            updatedAt = originalTimestamp,
        )
    }

    @InjectSoftAssertions
    lateinit var softly: SoftAssertions
    private val passwordEncoder: BCryptPasswordEncoder = mockk()

    @BeforeEach
    fun setUp() {
        mockkStatic(ZonedDateTime::class)
        every { ZonedDateTime.now() } returns timestamp
        every { passwordEncoder.encode(any()) } answers { firstArg<String>() }
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(ZonedDateTime::class)
    }

    @Nested
    inner class AccountMapping {

        @Test
        fun `test toAccountResponse when id is not null`() {
            // given
            val accountEntity = AccountEntity(
                id = account,
                createdAt = timestamp,
                updatedAt = timestamp,
                enabled = true,
                username = "username",
                password = "password",
                roles = "ROLE_ADMIN,ROLE_USER",
            )

            // when
            val result = accountEntity.toAccountResponse()

            // then
            assertThat(result.id).isEqualTo(accountEntity.id)
            assertThat(result.timestamp).isEqualTo(accountEntity.updatedAt)
            assertThat(result.enabled).isEqualTo(accountEntity.enabled)
            assertThat(result.username).isEqualTo(accountEntity.username)
            assertThat(result.roles).contains("ROLE_ADMIN", "ROLE_USER")
        }

        @Test
        fun `test toAccountResponse when id is null`() {
            // given
            val accountEntity = AccountEntity(
                id = null,
                createdAt = timestamp,
                updatedAt = timestamp,
                enabled = true,
                username = "username",
                password = "password",
                roles = "ROLE_ADMIN,ROLE_USER",
            )

            // when exception
            assertFailsWith<ResponseStatusException> {
                accountEntity.toAccountResponse()
            }
        }
    }

    @Nested
    inner class CreateAccountMapping {

        @Test
        fun `all fields changed`() {
            // given
            val request = Account.Request(
                username = "username",
                enabled = false,
                roles = listOf("ROLE_TEST"),
                password = "password",
            )

            // when
            val updatedUser = request.toAccountEntity(passwordEncoder)

            // then
            softly.assertThat(updatedUser.id).isNull()
            softly.assertThat(updatedUser.createdAt).isEqualTo(timestamp)
            softly.assertThat(updatedUser.updatedAt).isEqualTo(timestamp)
            softly.assertThat(updatedUser.enabled).isEqualTo(request.enabled)
            softly.assertThat(updatedUser.username).isEqualTo(request.username)
            softly.assertThat(updatedUser.password).isEqualTo(request.password)
            softly.assertThat(updatedUser.roles).isEqualTo("ROLE_TEST")
        }

        @Test
        fun `provided password is null`() {
            // Given
            val request = Account.Request(
                username = "username",
                enabled = false,
                roles = listOf("ROLE_TEST"),
                password = null,
            )

            // when exception
            assertFailsWith<ResponseStatusException> {
                request.toAccountEntity(passwordEncoder)
            }
        }
    }

    @Nested
    inner class UpdateAccountMapping {

        @Test
        fun `all fields changed`() {
            // Given
            val request = Account.Request(
                username = "new-username",
                enabled = false,
                roles = listOf("ROLE_MAGIC"),
                password = "new-password",
            )

            // When
            val updatedUser = originalUser.updateAccountEntity(request, passwordEncoder)

            // Then
            softly.assertThat(updatedUser.id).isEqualTo(originalUser.id)
            softly.assertThat(updatedUser.createdAt).isEqualTo(originalUser.createdAt)
            softly.assertThat(updatedUser.updatedAt).isEqualTo(timestamp)
            softly.assertThat(updatedUser.enabled).isEqualTo(request.enabled)
            softly.assertThat(updatedUser.username).isEqualTo(request.username)
            softly.assertThat(updatedUser.password).isEqualTo(request.password)
            softly.assertThat(updatedUser.roles).isEqualTo("ROLE_MAGIC")
        }

        @Test
        fun `provided password is null`() {
            // Given
            val request = Account.Request(
                username = originalUser.username,
                enabled = originalUser.enabled,
                roles = originalUser.roles.split(","),
                password = null,
            )

            // When
            val updatedUser = originalUser.updateAccountEntity(request, passwordEncoder)

            // Then
            softly.assertThat(updatedUser.id).isEqualTo(account)
            softly.assertThat(updatedUser.createdAt).isEqualTo(originalUser.createdAt)
            softly.assertThat(updatedUser.updatedAt).isEqualTo(timestamp)
            softly.assertThat(updatedUser.enabled).isEqualTo(request.enabled)
            softly.assertThat(updatedUser.username).isEqualTo(request.username)
            softly.assertThat(updatedUser.password).isEqualTo(originalUser.password)
            softly.assertThat(updatedUser.roles).isEqualTo(originalUser.roles)
        }

        @Test
        fun `roles changed from single to multiple`() {
            // Given
            val request = Account.Request(
                username = originalUser.username,
                enabled = originalUser.enabled,
                roles = listOf("ROLE_MAGIC", "ROLE_TEST"),
                password = null,
            )

            // When
            val updatedUser = originalUser.updateAccountEntity(request, passwordEncoder)

            // Then
            softly.assertThat(updatedUser.id).isEqualTo(originalUser.id)
            softly.assertThat(updatedUser.createdAt).isEqualTo(originalUser.createdAt)
            softly.assertThat(updatedUser.updatedAt).isEqualTo(timestamp)
            softly.assertThat(updatedUser.enabled).isEqualTo(originalUser.enabled)
            softly.assertThat(updatedUser.username).isEqualTo(originalUser.username)
            softly.assertThat(updatedUser.password).isEqualTo(originalUser.password)
            softly.assertThat(updatedUser.roles).isEqualTo("ROLE_MAGIC,ROLE_TEST")
        }
    }
}
