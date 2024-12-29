package ltd.hlaeja.util

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.test.assertFailsWith
import ltd.hlaeja.entity.AccountEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException

class MappingKtTest {
    companion object {
        val account = UUID.fromString("00000000-0000-0000-0000-000000000002")
        val timestamp: ZonedDateTime = ZonedDateTime.of(LocalDateTime.of(2000, 1, 1, 0, 0, 0, 1), ZoneId.of("UTC"))
    }

    @BeforeEach
    fun setUp() {
        mockkStatic(ZonedDateTime::class)
        every { ZonedDateTime.now() } returns timestamp
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(ZonedDateTime::class)
    }

    @Nested
    inner class AccountMapping {

        @Test
        fun `test toAccountResponse when id is not null`() {
            // Arrange
            val accountEntity = AccountEntity(
                id = account,
                createdAt = timestamp,
                updatedAt = timestamp,
                enabled = true,
                username = "username",
                password = "password",
                roles = "ROLE_ADMIN,ROLE_USER",
            )

            // Act
            val result = accountEntity.toAccountResponse()

            // Assert
            assertThat(result.id).isEqualTo(accountEntity.id)
            assertThat(result.timestamp).isEqualTo(accountEntity.updatedAt)
            assertThat(result.enabled).isEqualTo(accountEntity.enabled)
            assertThat(result.username).isEqualTo(accountEntity.username)
            assertThat(result.roles).contains("ROLE_ADMIN", "ROLE_USER")
        }

        @Test
        fun `test toAccountResponse when id is null`() {
            // Arrange
            val accountEntity = AccountEntity(
                id = null,
                createdAt = timestamp,
                updatedAt = timestamp,
                enabled = true,
                username = "username",
                password = "password",
                roles = "ROLE_ADMIN,ROLE_USER",
            )

            // Act and Assert
            assertFailsWith<ResponseStatusException> {
                accountEntity.toAccountResponse()
            }
        }
    }
}
