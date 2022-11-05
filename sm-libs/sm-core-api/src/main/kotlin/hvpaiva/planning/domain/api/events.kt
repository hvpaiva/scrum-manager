package hvpaiva.planning.domain.api

import hvpaiva.common.domain.api.AuditableAbstractEvent
import hvpaiva.common.domain.api.model.AuditEvent
import hvpaiva.planning.domain.api.model.*
import java.time.Duration

abstract class PlanningEvent(open val aggregateId: PlanningId, override val auditEvent: AuditEvent) :
    AuditableAbstractEvent(auditEvent)

abstract class TaskEvent(open val aggregateId: TaskId, override val auditEvent: AuditEvent) :
    AuditableAbstractEvent(auditEvent)

data class PlanningCreatedEvent(
    val name: String,
    val description: String?,
    val team: Set<MemberId>,
    override val aggregateId: PlanningId,
    override val auditEvent: AuditEvent
) : PlanningEvent(aggregateId, auditEvent)

data class TaskCreatedEvent(
    val name: TaskName,
    override val aggregateId: TaskId,
    override val auditEvent: AuditEvent
) : TaskEvent(aggregateId, auditEvent)

data class PlanningStartedEvent(
    override val aggregateId: PlanningId,
    override val auditEvent: AuditEvent
) : PlanningEvent(aggregateId, auditEvent)

data class TaskEstimatedEvent(
    val effort: Effort,
    val duration: Duration,
    val estimations: Set<MemberEffort>,
    override val aggregateId: TaskId,
    override val auditEvent: AuditEvent
) : TaskEvent(aggregateId, auditEvent)