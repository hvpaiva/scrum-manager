package hvpaiva.planning.domain.api

import hvpaiva.common.domain.api.AuditableAbstractEvent
import hvpaiva.common.domain.api.model.AuditEntry
import hvpaiva.planning.domain.api.model.*

// Planning Aggregate Events

abstract class PlanningEvent(open val aggregateId: PlanningId, override val auditEntry: AuditEntry) :
    AuditableAbstractEvent(auditEntry)

data class PlanningCreatedEvent(
    val name: String,
    val description: String?,
    val team: Set<MemberId>,
    override val aggregateId: PlanningId,
    override val auditEntry: AuditEntry
) : PlanningEvent(aggregateId, auditEntry)

data class TaskCreatedEvent(
    val name: TaskName,
    val taskId: TaskId,
    override val aggregateId: PlanningId,
    override val auditEntry: AuditEntry
) : PlanningEvent(aggregateId, auditEntry)

data class PlanningStartedEvent(
    override val aggregateId: PlanningId,
    override val auditEntry: AuditEntry
) : PlanningEvent(aggregateId, auditEntry)

data class PlanningFinishedEvent(
    val totalEffort: Effort,
    override val aggregateId: PlanningId,
    override val auditEntry: AuditEntry
) : PlanningEvent(aggregateId, auditEntry)

// Task Aggregate Events

abstract class TaskEvent(open val aggregateId: TaskId, override val auditEntry: AuditEntry) :
    AuditableAbstractEvent(auditEntry)

data class TaskEstimationStartedEvent(
    override val aggregateId: TaskId,
    override val auditEntry: AuditEntry
) : TaskEvent(aggregateId, auditEntry)

data class TaskEstimatedEvent(
    val effort: Effort,
    val member: MemberId,
    override val aggregateId: TaskId,
    override val auditEntry: AuditEntry
) : TaskEvent(aggregateId, auditEntry)

data class TaskEstimationFinishedEvent(
    override val aggregateId: TaskId,
    override val auditEntry: AuditEntry
) : TaskEvent(aggregateId, auditEntry)
