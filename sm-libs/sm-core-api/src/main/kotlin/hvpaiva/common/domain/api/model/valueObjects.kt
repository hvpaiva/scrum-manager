package hvpaiva.common.domain.api.model

import java.time.LocalDateTime

data class AuditEvent(val who: String, val `when`: LocalDateTime)