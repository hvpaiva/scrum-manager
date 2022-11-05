package hvpaiva.planning.domain

import hvpaiva.common.domain.api.model.AuditEntry
import hvpaiva.planning.domain.api.*
import hvpaiva.planning.domain.api.model.*
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.*

class TaskAggregateTest {

    private lateinit var fixture: FixtureConfiguration<Task>
    private val clock = Clock.fixed(
        LocalDateTime.parse("2020-01-01T00:00:00").toInstant(ZoneOffset.UTC),
        ZoneId.of("UTC")
    )
    private val auditEntry = AuditEntry("user", OffsetDateTime.now(clock))
    private val taskId = TaskId("taskId")
    private val planningId = PlanningId("planningId")
    private val taskName = TaskName("2121", "taskName")
    private val memberId = MemberId("memberId")

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(Task::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())
    }

    @Test
    internal fun `should start task estimation`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        fixture.given(taskCreatedEvent)
            .`when`(StartTaskEstimationCommand(taskId, auditEntry))
            .expectEvents(TaskEstimationStartedEvent(taskId, auditEntry))
    }

    @Test
    internal fun `should not start task estimation if task is already started`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        val startTaskEstimationCommand = StartTaskEstimationCommand(taskId, auditEntry)
        fixture.given(taskCreatedEvent, TaskEstimationStartedEvent(taskId, auditEntry))
            .`when`(startTaskEstimationCommand)
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Task already started estimation")
    }

    @Test
    internal fun `should not start task estimation if task is already finished`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        val taskEstimationStartedEvent = TaskEstimationStartedEvent(taskId, auditEntry)
        val taskEstimationFinishedEvent = TaskEstimationFinishedEvent(taskId, auditEntry)

        val startTaskEstimationCommand = StartTaskEstimationCommand(taskId, auditEntry)
        fixture.given(taskCreatedEvent, taskEstimationStartedEvent, taskEstimationFinishedEvent)
            .`when`(startTaskEstimationCommand)
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Task already finished estimation")
    }

    @Test
    internal fun `should estimate task`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        val taskEstimationStartedEvent = TaskEstimationStartedEvent(taskId, auditEntry)
        fixture.given(taskCreatedEvent, taskEstimationStartedEvent)
            .`when`(EstimateTaskCommand(Effort.NONE, memberId, taskId, auditEntry))
            .expectEvents(TaskEstimatedEvent(Effort.NONE, memberId, taskId, auditEntry))
    }

    @Test
    internal fun `should not estimate task if task is not started`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        val estimateTaskCommand = EstimateTaskCommand(Effort.NONE, memberId, taskId, auditEntry)
        fixture.given(taskCreatedEvent)
            .`when`(estimateTaskCommand)
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Task not started estimation")
    }

    @Test
    internal fun `should not estimate task if task is already finished`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        val taskEstimationStartedEvent = TaskEstimationStartedEvent(taskId, auditEntry)
        val taskEstimationFinishedEvent = TaskEstimationFinishedEvent(taskId, auditEntry)
        fixture.given(taskCreatedEvent, taskEstimationStartedEvent, taskEstimationFinishedEvent)
            .`when`(EstimateTaskCommand(Effort.NONE, memberId, taskId, auditEntry))
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Task already finished estimation")
    }

    @Test
    internal fun `should not estimate task if member already estimated`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        val taskEstimationStartedEvent = TaskEstimationStartedEvent(taskId, auditEntry)
        val taskEstimatedEvent = TaskEstimatedEvent(Effort.NONE, memberId, taskId, auditEntry)
        val estimateTaskCommand = EstimateTaskCommand(Effort.NONE, memberId, taskId, auditEntry)
        fixture.given(taskCreatedEvent, taskEstimationStartedEvent, taskEstimatedEvent)
            .`when`(estimateTaskCommand)
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Member already estimated")
    }

    @Test
    internal fun `should finish task estimation`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        val taskEstimationStartedEvent = TaskEstimationStartedEvent(taskId, auditEntry)
        val taskEstimatedEvent = TaskEstimatedEvent(Effort.NONE, memberId, taskId, auditEntry)
        fixture.given(taskCreatedEvent, taskEstimationStartedEvent, taskEstimatedEvent)
            .`when`(FinishTaskEstimationCommand(taskId, auditEntry))
            .expectEvents(TaskEstimationFinishedEvent(taskId, auditEntry))
    }

    @Test
    internal fun `should not finish task estimation if task is not started`() {
        val taskCreatedEvent = TaskCreatedEvent(taskName, taskId, planningId, auditEntry)
        val finishTaskEstimationCommand = FinishTaskEstimationCommand(taskId, auditEntry)
        fixture.given(taskCreatedEvent)
            .`when`(finishTaskEstimationCommand)
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Task not started estimation")
    }
}