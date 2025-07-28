package ltd.hlaeja.controller

import ltd.hlaeja.library.accountRegistry.Account
import ltd.hlaeja.test.container.PostgresContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@PostgresContainer
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AccountsEndpoint {

    @LocalServerPort
    var port: Int = 0

    lateinit var webClient: WebTestClient

    @BeforeEach
    fun setup() {
        webClient = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Test
    fun `get accounts`() {
        // when
        val result = webClient.get().uri("/accounts").exchange()

        // then
        result.expectStatus().isOk()
            .expectBody<List<Account.Response>>()
            .consumeWith {
                assertThat(it.responseBody?.size).isEqualTo(3)
            }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1,3",
            "2,0",
        ],
    )
    fun `get accounts with pages`(page: Int, expected: Int) {
        // when
        val result = webClient.get().uri("/accounts/page-$page").exchange()

        // then
        result.expectStatus().isOk()
            .expectBody<List<Account.Response>>()
            .consumeWith {
                assertThat(it.responseBody?.size).isEqualTo(expected)
            }
    }

    @Test
    fun `get accounts with bad pages`() {
        // when
        val result = webClient.get().uri("/accounts/page-0").exchange()

        // then
        result.expectStatus().isBadRequest
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1,2,2",
            "2,2,1",
            "3,2,0",
            "1,5,3",
            "2,5,0",
        ],
    )
    fun `get accounts with pages and size to show`(page: Int, show: Int, expected: Int) {
        // when
        val result = webClient.get().uri("/accounts/page-$page/show-$show").exchange()

        // then
        result.expectStatus().isOk()
            .expectBody<List<Account.Response>>()
            .consumeWith {
                assertThat(it.responseBody?.size).isEqualTo(expected)
            }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "1,0",
            "0,1",
            "0,0",
            "1,-1",
            "-1,1",
            "-1,-1",
        ],
    )
    fun `get accounts with bad pages or bad size to show`(page: Int, show: Int) {
        // when
        val result = webClient.get().uri("/accounts/page-$page/show-$show").exchange()

        // then
        result.expectStatus().isBadRequest
    }
}
