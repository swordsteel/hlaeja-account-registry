package ltd.hlaeja.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import ltd.hlaeja.library.accountRegistry.Account

class AccountValidator : ConstraintValidator<ValidAccount, Any> {

    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        return when (value) {
            is Account.Request -> value.validate()
            else -> true // Default to valid if the type is not a list
        }
    }

    private fun Account.Request.validate(): Boolean = username.isNotBlank() &&
        password?.isNotBlank() ?: true &&
        roles.isNotEmpty()
}
