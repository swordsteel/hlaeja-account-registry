package ltd.hlaeja.controller

import ltd.hlaeja.library.accountRegistry.Authentication
import ltd.hlaeja.service.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {

    @PostMapping("/authenticate")
    fun authenticate(
        @RequestBody request: Authentication.Request,
    ): Mono<Authentication.Response> = authenticationService.authenticate(request.username, request.password)
        .map { Authentication.Response(it) }
}
