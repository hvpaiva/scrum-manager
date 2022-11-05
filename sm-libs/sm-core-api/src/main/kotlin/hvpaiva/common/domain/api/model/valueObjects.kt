package hvpaiva.common.domain.api.model

import java.time.OffsetDateTime

data class AuditEntry(val who: String, val `when`: OffsetDateTime)