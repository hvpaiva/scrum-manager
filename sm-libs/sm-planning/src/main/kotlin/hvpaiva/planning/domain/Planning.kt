package hvpaiva.planning.domain

import hvpaiva.planning.domain.api.*
import hvpaiva.planning.domain.api.model.Effort
import hvpaiva.planning.domain.api.model.MemberId
import hvpaiva.planning.domain.api.model.PlanningId
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.time.Duration
import java.time.OffsetDateTime

@Aggregate
internal class Planning {
    @AggregateIdentifier
    private lateinit var id: PlanningId
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var team: Set<MemberId>
    private lateinit var totalEffort: Effort
    private lateinit var totalDuration: Duration
    private lateinit var createdAt: OffsetDateTime
    private var startedAt: OffsetDateTime? = null
    private var finishedAt: OffsetDateTime? = null;

    constructor()

    @CommandHandler
    constructor(command: CreatePlanningCommand) {
        if (command.team.isEmpty()) {
            throw IllegalArgumentException("Team must not be empty")
        }
        AggregateLifecycle.apply(
            PlanningCreatedEvent(
                command.name,
                command.description,
                command.team,
                command.targetAggregateId,
                command.auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: PlanningCreatedEvent) {
        id = event.aggregateId
        name = event.name
        description = event.description ?: ""
        team = event.team
        createdAt = event.auditEntry.`when`
    }

    @CommandHandler
    fun handle(command: StartPlanningCommand) {
        if (finishedAt == null && startedAt != null) throw IllegalStateException("Planning already started")
        if (finishedAt != null) throw IllegalStateException("Planning already finished")

        AggregateLifecycle.apply(PlanningStartedEvent(command.targetAggregateId, command.auditEntry))
    }

    @EventSourcingHandler
    fun on(event: PlanningStartedEvent) {
        this.startedAt = event.auditEntry.`when`
    }

    @CommandHandler
    fun handle(command: FinishPlanningCommand) {
        if (finishedAt == null && startedAt == null) throw IllegalStateException("Planning not started")
        if (finishedAt != null) throw IllegalStateException("Planning already finished")
        
        AggregateLifecycle.apply(
            PlanningFinishedEvent(
                command.totalEffort,
                command.targetAggregateId,
                command.auditEntry
            )
        )
    }

    @EventSourcingHandler
    fun on(event: PlanningFinishedEvent) {
        this.finishedAt = event.auditEntry.`when`
        this.totalEffort = event.totalEffort
        this.totalDuration = Duration.between(startedAt, finishedAt)
    }

    @CommandHandler
    fun handle(command: CreateTaskCommand) {
        AggregateLifecycle.createNew(Task::class.java) { Task(command) }
    }

    override fun toString(): String = ToStringBuilder.reflectionToString(this)

    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}