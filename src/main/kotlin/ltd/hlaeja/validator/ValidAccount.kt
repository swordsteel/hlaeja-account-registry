package ltd.hlaeja.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

@Constraint(validatedBy = [AccountValidator::class])
@Retention(RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ValidAccount(
    val message: String = "Roles must not be empty",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
