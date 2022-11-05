package hvpaiva.planning.domain

import hvpaiva.planning.domain.api.*
import hvpaiva.planning.domain.api.model.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.time.Duration
import java.time.OffsetDateTime

@Aggregate(snapshotTriggerDefinition = "taskSnapshotTriggerDefinition")
internal class Task {

    @AggregateIdentifier
    private lateinit var id: TaskId
    private lateinit var planningId: PlanningId
    private lateinit var name: TaskName
    private lateinit var effort: Effort
    private lateinit var duration: Duration
    private lateinit var estimations: Set<Estimation>
    private lateinit var createdAt: OffsetDateTime
    private var startedEstimationAt: OffsetDateTime? = null
    private var finishedEstimationAt: OffsetDateTime? = null

    constructor()

    constructor(command: CreateTaskCommand) {
        AggregateLifecycle.apply(
            TaskCreatedEvent(
                command.name,
                command.taskId,
                command.targetAggregateId,
                command.auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: TaskCreatedEvent) {
        id = event.taskId
        planningId = event.aggregateId
        name = event.name
        createdAt = event.auditEntry.`when`
        estimations = emptySet()
    }

    @CommandHandler
    fun handle(command: StartTaskEstimationCommand) {
        if (finishedEstimationAt == null && startedEstimationAt != null) throw IllegalStateException("Task already started estimation")
        if (finishedEstimationAt != null) throw IllegalStateException("Task already finished estimation")

        AggregateLifecycle.apply(
            TaskEstimationStartedEvent(
                command.targetAggregateId,
                command.auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: TaskEstimationStartedEvent) {
        startedEstimationAt = event.auditEntry.`when`
    }

    @CommandHandler
    fun handle(command: EstimateTaskCommand) {
        if (estimations.any { it.member == command.member }) {
            throw IllegalStateException("Member already estimated")
        }
        if (finishedEstimationAt == null && startedEstimationAt == null) throw IllegalStateException("Task not started estimation")
        if (finishedEstimationAt != null) throw IllegalStateException("Task already finished estimation")


        AggregateLifecycle.apply(
            TaskEstimatedEvent(
                command.effort,
                command.member,
                command.targetAggregateId,
                command.auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: TaskEstimatedEvent) {
        startedEstimationAt = event.auditEntry.`when`
        estimations = estimations + Estimation(event.member, event.effort)
    }

    @CommandHandler
    fun handle(command: FinishTaskEstimationCommand) {
        if (startedEstimationAt == null) throw IllegalStateException("Task not started estimation")

        AggregateLifecycle.apply(
            TaskEstimationFinishedEvent(
                command.targetAggregateId,
                command.auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: TaskEstimationFinishedEvent) {
        finishedEstimationAt = event.auditEntry.`when`
        duration = Duration.between(startedEstimationAt, finishedEstimationAt)
        effort = estimations.map { it.effort.value }.average().toInt().let { Effort(it) }
    }

}