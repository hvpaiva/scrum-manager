package hvpaiva.common.domain.api

import hvpaiva.common.domain.api.model.AuditEvent

abstract class AuditableAbstractEvent(open val auditEvent: AuditEvent)