package hvpaiva.planning.domain.api

import hvpaiva.common.domain.api.AuditableAbstractCommand
import hvpaiva.common.domain.api.model.AuditEvent
import hvpaiva.planning.domain.api.model.*
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.Valid

abstract class PlanningCommand(open val targetAggregateId: PlanningId, override val auditEvent: AuditEvent) :
    AuditableAbstractCommand(auditEvent)

abstract class TaskCommand(open val targetAggregateId: TaskId, override val auditEvent: AuditEvent) :
    AuditableAbstractCommand(auditEvent)

data class CreatePlanningCommand(
    @field:Valid val name: String,
    val description: String?,
    val team: Set<MemberId>,
    @TargetAggregateIdentifier override val targetAggregateId: PlanningId,
    override val auditEvent: AuditEvent
) : PlanningCommand(targetAggregateId, auditEvent)

data class CreateTaskCommand(
    @field:Valid val name: TaskName,
    @TargetAggregateIdentifier override val targetAggregateId: TaskId,
    override val auditEvent: AuditEvent
) : TaskCommand(targetAggregateId, auditEvent)

data class StartPlanningCommand(
    @TargetAggregateIdentifier override val targetAggregateId: PlanningId,
    override val auditEvent: AuditEvent
) : PlanningCommand(targetAggregateId, auditEvent)

data class StartEstimateTaskCommand(
    @TargetAggregateIdentifier override val targetAggregateId: TaskId,
    override val auditEvent: AuditEvent
) : TaskCommand(targetAggregateId, auditEvent)

data class EstimateTaskCommand(
    @field:Valid val effort: Effort,
    val member: MemberId,
    @TargetAggregateIdentifier override val targetAggregateId: TaskId,
    override val auditEvent: AuditEvent
) : TaskCommand(targetAggregateId, auditEvent)