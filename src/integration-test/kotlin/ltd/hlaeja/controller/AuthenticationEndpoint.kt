package ltd.hlaeja.controller

import org.assertj.core.api.Assertions.assertThat
import ltd.hlaeja.library.accountRegistry.Authentication
import ltd.hlaeja.test.compareToFile
import ltd.hlaeja.test.container.PostgresContainer
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@PostgresContainer
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SoftAssertionsExtension::class)
class AuthenticationEndpoint {

    @InjectSoftAssertions
    lateinit var softly: SoftAssertions

    @LocalServerPort
    var port: Int = 0

    lateinit var webClient: WebTestClient

    @BeforeEach
    fun setup() {
        webClient = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }

    @Test
    fun `login as admin`() {
        // given
        val request = Authentication.Request(
            username = "admin",
            password = "pass",
        )

        // when
        val result = webClient.post().uri("/authenticate").bodyValue(request).exchange()

        // then
        result.expectStatus().isOk()
            .expectBody<Authentication.Response>()
            .consumeWith { assertThat(it.responseBody?.token).compareToFile("authenticate/admin-token.data") }
    }

    @Test
    fun `login as user`() {
        // given
        val request = Authentication.Request(
            username = "user",
            password = "pass",
        )

        // when
        val result = webClient.post().uri("/authenticate").bodyValue(request).exchange()

        // then
        result.expectStatus().isOk()
            .expectBody<Authentication.Response>()
            .consumeWith { assertThat(it.responseBody?.token).compareToFile("authenticate/user-token.data") }
    }

    @Test
    fun `login as disabled user`() {
        // given
        val request = Authentication.Request(
            username = "disabled",
            password = "pass",
        )

        // when
        val result = webClient.post().uri("/authenticate").bodyValue(request).exchange()

        // then
        result.expectStatus().isEqualTo(HttpStatus.LOCKED)
    }

    @Test
    fun `login as non-existent `() {
        // given
        val request = Authentication.Request(
            username = "username",
            password = "pass",
        )

        // when
        val result = webClient.post().uri("/authenticate").bodyValue(request).exchange()

        // then
        result.expectStatus().isNotFound
    }

    @Test
    fun `login as user bad password`() {
        // given
        val request = Authentication.Request(
            username = "user",
            password = "password",
        )

        // when
        val result = webClient.post().uri("/authenticate").bodyValue(request).exchange()

        // then
        result.expectStatus().isUnauthorized
    }
}
