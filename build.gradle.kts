plugins {
    alias(hlaeja.plugins.kotlin.jvm)
    alias(hlaeja.plugins.kotlin.spring)
    alias(hlaeja.plugins.ltd.hlaeja.plugin.certificate)
    alias(hlaeja.plugins.ltd.hlaeja.plugin.service)
    alias(hlaeja.plugins.spring.dependency.management)
    alias(hlaeja.plugins.springframework.boot)
}

dependencies {
    implementation(hlaeja.fasterxml.jackson.module.kotlin)
    implementation(hlaeja.kotlin.logging)
    implementation(hlaeja.kotlin.reflect)
    implementation(hlaeja.kotlinx.coroutines)
    implementation(hlaeja.library.hlaeja.common.messages)
    implementation(hlaeja.library.hlaeja.jwt)
    implementation(hlaeja.springboot.starter.actuator)
    implementation(hlaeja.springboot.starter.r2dbc)
    implementation(hlaeja.springboot.starter.security)
    implementation(hlaeja.springboot.starter.validation)
    implementation(hlaeja.springboot.starter.webflux)

    runtimeOnly(hlaeja.postgresql)
    runtimeOnly(hlaeja.postgresql.r2dbc)

    testImplementation(hlaeja.assertj.core)
    testImplementation(hlaeja.mockk)
    testImplementation(hlaeja.projectreactor.reactor.test)
    testImplementation(hlaeja.kotlin.test.junit5)
    testImplementation(hlaeja.kotlinx.coroutines.test)
    testImplementation(hlaeja.springboot.starter.test)

    testRuntimeOnly(hlaeja.junit.platform.launcher)

    integrationTestImplementation(hlaeja.assertj.core)
    integrationTestImplementation(hlaeja.library.hlaeja.test)
    integrationTestImplementation(hlaeja.projectreactor.reactor.test)
    integrationTestImplementation(hlaeja.kotlin.test.junit5)
    integrationTestImplementation(hlaeja.kotlinx.coroutines.test)
    integrationTestImplementation(hlaeja.springboot.starter.test)

    integrationTestRuntimeOnly(hlaeja.junit.platform.launcher)
}

group = "ltd.hlaeja"

tasks.named("processResources") {
    dependsOn("copyCertificates")
}
