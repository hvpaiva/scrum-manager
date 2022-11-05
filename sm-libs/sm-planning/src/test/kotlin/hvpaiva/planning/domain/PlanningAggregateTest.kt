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

class PlanningAggregateTest {

    private lateinit var fixture: FixtureConfiguration<Planning>
    private val clock = Clock.fixed(
        LocalDateTime.parse("2020-01-01T00:00:00").toInstant(ZoneOffset.UTC),
        ZoneId.of("UTC")
    )
    private val auditEntry = AuditEntry("user", OffsetDateTime.now(clock))
    private val planningId = PlanningId("planningId")
    private val memberId = MemberId("memberId")
    private val taskId = TaskId("taskId")
    private val taskName = TaskName("2121", "taskName")

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(Planning::class.java)
        fixture.registerCommandDispatchInterceptor(BeanValidationInterceptor())
    }

    @Test
    internal fun `should create planning`() {
        fixture.givenNoPriorActivity()
            .`when`(CreatePlanningCommand("name", "description", setOf(memberId), planningId, auditEntry))
            .expectEvents(PlanningCreatedEvent("name", "description", setOf(memberId), planningId, auditEntry))
    }

    @Test
    internal fun `should not create planning with no members`() {
        fixture.givenNoPriorActivity()
            .`when`(CreatePlanningCommand("name", "description", setOf(), planningId, auditEntry))
            .expectException(IllegalArgumentException::class.java)
            .expectExceptionMessage("Team must not be empty")
    }

    @Test
    internal fun `should create task`() {
        val planningCreatedEvent = PlanningCreatedEvent("name", "description", setOf(memberId), planningId, auditEntry)
        val createTaskCommand = CreateTaskCommand(taskName, taskId, planningId, auditEntry)
        fixture.given(planningCreatedEvent)
            .`when`(createTaskCommand)
            .expectEvents(TaskCreatedEvent(taskName, taskId, planningId, auditEntry))
    }

    @Test
    internal fun `should start the planning`() {
        val planningCreatedEvent = PlanningCreatedEvent("name", "description", setOf(memberId), planningId, auditEntry)
        fixture.given(planningCreatedEvent)
            .`when`(StartPlanningCommand(planningId, auditEntry))
            .expectEvents(PlanningStartedEvent(planningId, auditEntry))
    }

    @Test
    internal fun `should not start the planning if it is already started`() {
        val planningCreatedEvent = PlanningCreatedEvent("name", "description", setOf(memberId), planningId, auditEntry)
        val planningStartedEvent = PlanningStartedEvent(planningId, auditEntry)
        fixture.given(planningCreatedEvent, planningStartedEvent)
            .`when`(StartPlanningCommand(planningId, auditEntry))
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Planning already started")
    }

    @Test
    internal fun `should not start the planning if it is already finished`() {
        val planningCreatedEvent = PlanningCreatedEvent("name", "description", setOf(memberId), planningId, auditEntry)
        val planningStartedEvent = PlanningStartedEvent(planningId, auditEntry)
        val planningFinishedEvent = PlanningFinishedEvent(Effort.NONE, planningId, auditEntry)
        fixture.given(planningCreatedEvent, planningStartedEvent, planningFinishedEvent)
            .`when`(StartPlanningCommand(planningId, auditEntry))
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Planning already finished")
    }

    @Test
    internal fun `should finish the planning`() {
        val planningCreatedEvent = PlanningCreatedEvent("name", "description", setOf(memberId), planningId, auditEntry)
        val planningStartedEvent = PlanningStartedEvent(planningId, auditEntry)
        fixture.given(planningCreatedEvent, planningStartedEvent)
            .`when`(FinishPlanningCommand(Effort.NONE, planningId, auditEntry))
            .expectEvents(PlanningFinishedEvent(Effort.NONE, planningId, auditEntry))
    }

    @Test
    internal fun `should not finish the planning if it is not started`() {
        val planningCreatedEvent = PlanningCreatedEvent("name", "description", setOf(memberId), planningId, auditEntry)
        fixture.given(planningCreatedEvent)
            .`when`(FinishPlanningCommand(Effort.NONE, planningId, auditEntry))
            .expectException(IllegalStateException::class.java)
            .expectExceptionMessage("Planning not started")
    }

    @Test
    internal fun `should not finish the planning if it is already finished`() {
        val planningCreatedEvent = PlanningCreatedEvent("name", "description", setOf(memberId), planningId, auditEntry)
        val planningStartedEvent = PlanningStartedEvent(planningId, auditEntry)
        val planningFinishedEvent = PlanningFinishedEvent(Effort.NONE, planningId, auditEntry)
        fixture.given(planningCreatedEvent, planningStartedEvent, planningFinishedEvent)
            .`when`(FinishPlanningCommand(Effort.NONE, planningId, auditEntry))
            .expectException(IllegalStateException::class.java)
    }
}