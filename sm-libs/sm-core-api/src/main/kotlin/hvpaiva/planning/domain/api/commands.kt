package hvpaiva.planning.domain.api

import hvpaiva.common.domain.api.AuditableAbstractCommand
import hvpaiva.common.domain.api.model.AuditEntry
import hvpaiva.planning.domain.api.model.*
import org.axonframework.modelling.command.TargetAggregateIdentifier
import javax.validation.Valid

// Planning Aggregate Commands
abstract class PlanningCommand(open val targetAggregateId: PlanningId, override val auditEntry: AuditEntry) :
    AuditableAbstractCommand(auditEntry)

data class CreatePlanningCommand(
    @field:Valid val name: String,
    val description: String?,
    val team: Set<MemberId>,
    @TargetAggregateIdentifier override val targetAggregateId: PlanningId,
    override val auditEntry: AuditEntry
) : PlanningCommand(targetAggregateId, auditEntry)

data class CreateTaskCommand(
    @field:Valid val name: TaskName,
    val taskId: TaskId,
    @TargetAggregateIdentifier override val targetAggregateId: PlanningId,
    override val auditEntry: AuditEntry
) : PlanningCommand(targetAggregateId, auditEntry)

data class StartPlanningCommand(
    @TargetAggregateIdentifier override val targetAggregateId: PlanningId,
    override val auditEntry: AuditEntry
) : PlanningCommand(targetAggregateId, auditEntry)

data class FinishPlanningCommand(
    val totalEffort: Effort,
    @TargetAggregateIdentifier override val targetAggregateId: PlanningId,
    override val auditEntry: AuditEntry
) : PlanningCommand(targetAggregateId, auditEntry)

// Task Aggregate Commands

abstract class TaskCommand(open val targetAggregateId: TaskId, override val auditEntry: AuditEntry) :
    AuditableAbstractCommand(auditEntry)

data class StartTaskEstimationCommand(
    @TargetAggregateIdentifier override val targetAggregateId: TaskId,
    override val auditEntry: AuditEntry
) : TaskCommand(targetAggregateId, auditEntry)

data class EstimateTaskCommand(
    @field:Valid val effort: Effort,
    val member: MemberId,
    @TargetAggregateIdentifier override val targetAggregateId: TaskId,
    override val auditEntry: AuditEntry
) : TaskCommand(targetAggregateId, auditEntry)

data class FinishTaskEstimationCommand(
    @TargetAggregateIdentifier override val targetAggregateId: TaskId,
    override val auditEntry: AuditEntry
) : TaskCommand(targetAggregateId, auditEntry)

