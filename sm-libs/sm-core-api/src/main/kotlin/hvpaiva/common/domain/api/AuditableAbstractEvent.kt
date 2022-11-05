package hvpaiva.common.domain.api

import hvpaiva.common.domain.api.model.AuditEntry

abstract class AuditableAbstractEvent(open val auditEntry: AuditEntry)