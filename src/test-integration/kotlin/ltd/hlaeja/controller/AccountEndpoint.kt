package ltd.hlaeja.controller

import java.util.UUID
import ltd.hlaeja.library.accountRegistry.Account
import ltd.hlaeja.test.container.PostgresContainer
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@PostgresContainer
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SoftAssertionsExtension::class)
class AccountEndpoint {

    @InjectSoftAssertions
    lateinit var softly: SoftAssertions

    @LocalServerPort
    var port: Int = 0

    lateinit var webClient: WebTestClient

    @BeforeEach
    fun setup() {
        webClient = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Nested
    inner class GetAccount {

        @Test
        fun `get account with valid uuid`() {
            // given
            val uuid = UUID.fromString("00000000-0000-7000-0000-000000000001")

            // when
            val result = webClient.get().uri("/account-$uuid").exchange()

            // then
            result.expectStatus().isOk()
                .expectBody<Account.Response>()
                .consumeWith {
                    softly.assertThat(it.responseBody?.id).isEqualTo(uuid)
                    softly.assertThat(it.responseBody?.username).isEqualTo("admin")
                    softly.assertThat(it.responseBody?.enabled).isTrue
                    softly.assertThat(it.responseBody?.roles?.size).isEqualTo(1)
                    softly.assertThat(it.responseBody?.roles?.get(0)).isEqualTo("ROLE_ADMIN")
                }
        }

        @Test
        fun `get account with invalid uuid`() {
            // given
            val uuid = UUID.fromString("00000000-0000-7000-0000-000000000000")

            // when
            val result = webClient.get().uri("/account-$uuid").exchange()

            // then
            result.expectStatus().isNotFound
        }

        @Test
        fun `get account with bad uuid`() {
            // given
            val uuidInvalid = "000000000001"

            // when
            val result = webClient.get().uri("/account-$uuidInvalid").exchange()

            // then
            result.expectStatus().isBadRequest
        }
    }

    @Nested
    inner class PutAccount {

        @Test
        fun `success account with all changes`() {
            // given
            val uuid = UUID.fromString("00000000-0000-7000-0000-000000000003")
            val request = Account.Request(
                username = "usernameA",
                password = "abc123",
                enabled = true,
                roles = listOf("ROLE_USER", "ROLE_TEST"),
            )

            // when
            val result = webClient.put().uri("/account-$uuid").bodyValue(request).exchange()

            // then
            result.expectStatus().isOk()
                .expectBody<Account.Response>()
                .consumeWith {
                    softly.assertThat(it.responseBody?.id).isEqualTo(uuid)
                    softly.assertThat(it.responseBody?.username).isEqualTo("usernameA")
                    softly.assertThat(it.responseBody?.enabled).isTrue
                    softly.assertThat(it.responseBody?.roles?.size).isEqualTo(2)
                    softly.assertThat(it.responseBody?.roles).contains("ROLE_USER")
                    softly.assertThat(it.responseBody?.roles).contains("ROLE_TEST")
                }
        }

        @Test
        fun `success account with null password changes`() {
            // given
            val uuid = UUID.fromString("00000000-0000-7000-0000-000000000003")
            val request = Account.Request(
                username = "usernameB",
                password = null,
                enabled = false,
                roles = listOf("ROLE_TEST"),
            )

            // when
            val result = webClient.put().uri("/account-$uuid").bodyValue(request).exchange()

            // then
            result.expectStatus().isOk()
                .expectBody<Account.Response>()
                .consumeWith {
                    softly.assertThat(it.responseBody?.id).isEqualTo(uuid)
                    softly.assertThat(it.responseBody?.username).isEqualTo("usernameB")
                    softly.assertThat(it.responseBody?.enabled).isFalse
                    softly.assertThat(it.responseBody?.roles?.size).isEqualTo(1)
                    softly.assertThat(it.responseBody?.roles).contains("ROLE_TEST")
                }
        }

        @Test
        fun `success account with no changes`() {
            // given
            val uuid = UUID.fromString("00000000-0000-7000-0000-000000000002")
            val request = Account.Request(
                username = "user",
                password = null,
                enabled = true,
                roles = listOf("ROLE_USER"),
            )

            // when
            val result = webClient.put().uri("/account-$uuid").bodyValue(request).exchange()

            // then
            result.expectStatus().isEqualTo(ACCEPTED)
        }

        @Test
        fun `failed username duplicate`() {
            // given
            val uuid = UUID.fromString("00000000-0000-7000-0000-000000000002")
            val request = Account.Request(
                username = "admin",
                password = null,
                enabled = true,
                roles = listOf("ROLE_USER"),
            )

            // when
            val result = webClient.put().uri("/account-$uuid").bodyValue(request).exchange()

            // then
            result.expectStatus().isEqualTo(CONFLICT)
        }

        @Test
        fun `failed account not found`() {
            // given
            val uuid = UUID.fromString("00000000-0000-7000-0000-000000000000")
            val request = Account.Request(
                username = "admin",
                password = null,
                enabled = true,
                roles = listOf("ROLE_USER"),
            )

            // when
            val result = webClient.put().uri("/account-$uuid").bodyValue(request).exchange()

            // then
            result.expectStatus().isNotFound
        }
    }

    @Nested
    inner class PostAccount {

        @ParameterizedTest
        @CsvSource(
            "new-user, new-pass, true, 2, ROLE_USER;ROLE_TEST",
            "admin-user, admin-pass, false, 1, ROLE_ADMIN",
            "test-user, test-pass, true, 1, ROLE_USER",
        )
        fun `success added account`(
            username: String,
            password: String,
            enabled: Boolean,
            size: Int,
            roleList: String,
        ) {
            // given
            val roles: List<String> = roleList.split(";")

            val request = Account.Request(
                username = username,
                password = password,
                enabled = enabled,
                roles = roles,
            )

            // when
            val result = webClient.post().uri("/account").bodyValue(request).exchange()

            // then
            result.expectStatus().isCreated
                .expectBody<Account.Response>()
                .consumeWith {
                    softly.assertThat(it.responseBody?.id?.version()).isEqualTo(7)
                    softly.assertThat(it.responseBody?.username).isEqualTo(username)
                    softly.assertThat(it.responseBody?.enabled).isEqualTo(enabled)
                    softly.assertThat(it.responseBody?.roles?.size).isEqualTo(size)
                    for (role in roles) {
                        softly.assertThat(it.responseBody?.roles).contains(role)
                    }
                }
        }

        @ParameterizedTest
        @CsvSource(
            "'', new-pass, ROLE_TEST",
            "new-user, '', ROLE_ADMIN",
            "new-user, new-pass, ''",
        )
        fun `validation fail on empty values`(
            username: String,
            password: String,
            roleList: String,
        ) {
            // given
            val request = Account.Request(
                username = username,
                password = password,
                enabled = true,
                roles = when {
                    roleList.isEmpty() -> emptyList()
                    else -> listOf(roleList)
                },
            )

            // when
            val result = webClient.post().uri("/account").bodyValue(request).exchange()

            // then
            result.expectStatus().isBadRequest
        }

        @Test
        fun `fail username take`() {
            // given
            val request = Account.Request(
                username = "user",
                password = "new-pass",
                enabled = true,
                roles = listOf("ROLE_USER", "ROLE_TEST"),
            )

            // when
            val result = webClient.post().uri("/account").bodyValue(request).exchange()

            // then
            result.expectStatus().isEqualTo(CONFLICT)
        }

        @Test
        fun `fail password null`() {
            // given
            val request = Account.Request(
                username = "user",
                password = null,
                enabled = true,
                roles = listOf("ROLE_USER", "ROLE_TEST"),
            )

            // when
            val result = webClient.post().uri("/account").bodyValue(request).exchange()

            // then
            result.expectStatus().isBadRequest
        }
    }
}
