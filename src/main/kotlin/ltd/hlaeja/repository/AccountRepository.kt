package ltd.hlaeja.repository

import java.util.UUID
import ltd.hlaeja.entity.AccountEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : ReactiveCrudRepository<AccountEntity, UUID>
