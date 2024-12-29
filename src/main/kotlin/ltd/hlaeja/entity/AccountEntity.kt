package ltd.hlaeja.entity

import java.time.ZonedDateTime
import java.util.UUID
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("accounts")
data class AccountEntity(
    @Id
    val id: UUID? = null,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val enabled: Boolean,
    val username: String,
    val password: String,
    val roles: String,
)
