package hvpaiva.web

import hvpaiva.common.domain.api.model.AuditEntry
import hvpaiva.planning.domain.api.CreatePlanningCommand
import hvpaiva.planning.domain.api.model.MemberId
import hvpaiva.planning.domain.api.model.PlanningId
import hvpaiva.query.entity.PlanningEntity
import hvpaiva.query.repository.PlanningRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import java.time.OffsetDateTime
import java.util.concurrent.CompletableFuture

@Controller
class WebController(
    private val commandGateway: CommandGateway,
    private val planningRepository: PlanningRepository
) {

    @MessageMapping("/plannings/create")
    fun createPlanning(request: CreatePlanningRequest): CompletableFuture<CreatePlanningCommand> =
        commandGateway.send(
            CreatePlanningCommand(
                request.name,
                request.description,
                request.team,
                PlanningId(),
                AuditEntry("API", OffsetDateTime.now())
            )
        )

    @SubscribeMapping("/plannings")
    fun allPlannings() = PlanningListResponse(planningRepository.findAll())

    @SubscribeMapping("/plannings/{id}")
    fun findPlanning(@DestinationVariable id: String) = planningRepository
        .findById(id)

    data class CreatePlanningRequest(
        val name: String,
        val description: String?,
        val team: Set<MemberId>
    )

    data class PlanningListResponse(
        val plannings: Iterable<PlanningEntity>
    )
}