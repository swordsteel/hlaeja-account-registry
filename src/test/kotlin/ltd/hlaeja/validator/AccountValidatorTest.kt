package ltd.hlaeja.validator

import io.mockk.mockk
import ltd.hlaeja.library.accountRegistry.Account
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AccountValidatorTest {

    val validator = AccountValidator()

    @Test
    fun `validate account - success all values`() {
        // given
        val request = Account.Request(
            username = "validUser",
            password = "strongPassword",
            enabled = true,
            roles = listOf("USER", "TEST"),
        )

        // when
        val result = validator.isValid(request, mockk())

        // then
        assertThat(result).isTrue
    }

    @Test
    fun `validate account - success password null`() {
        // given
        val request = Account.Request(
            username = "validUser",
            password = null,
            enabled = true,
            roles = listOf("USER"),
        )

        // when
        val result = validator.isValid(request, mockk())

        // then
        assertThat(result).isTrue
    }

    @Test
    fun `validate account - failed username empty`() {
        // given
        val request = Account.Request(
            username = "",
            password = "strongPassword",
            enabled = true,
            roles = listOf("USER"),
        )

        // when
        val result = validator.isValid(request, mockk())

        // then
        assertThat(result).isFalse
    }

    @Test
    fun `validate account - failed password empty`() {
        // given
        val request = Account.Request(
            username = "validUser",
            password = "",
            enabled = true,
            roles = listOf("USER"),
        )

        // when
        val result = validator.isValid(request, mockk())

        // then
        assertThat(result).isFalse
    }

    @Test
    fun `validate account - failed roles empty`() {
        // given
        val request = Account.Request(
            username = "validUser",
            password = "",
            enabled = true,
            roles = emptyList(),
        )

        // when
        val result = validator.isValid(request, mockk())

        // then
        assertThat(result).isFalse
    }

    @Test
    fun `validate account - success wrong data type`() {
        // given
        val request = "A string"

        // when
        val result = validator.isValid(request, mockk())

        // then
        assertThat(result).isTrue
    }
}
