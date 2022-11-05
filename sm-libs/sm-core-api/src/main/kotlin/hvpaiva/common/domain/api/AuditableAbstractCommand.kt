package hvpaiva.common.domain.api

import hvpaiva.common.domain.api.model.AuditEvent

abstract class AuditableAbstractCommand(open val auditEvent: AuditEvent)