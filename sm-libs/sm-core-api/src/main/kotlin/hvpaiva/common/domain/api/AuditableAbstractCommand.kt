package hvpaiva.common.domain.api

import hvpaiva.common.domain.api.model.AuditEntry

abstract class AuditableAbstractCommand(open val auditEntry: AuditEntry)