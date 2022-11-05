package hvpaiva.handler

import hvpaiva.planning.domain.api.PlanningCreatedEvent
import hvpaiva.query.entity.MemberEntity
import hvpaiva.query.entity.PlanningEntity
import hvpaiva.query.repository.PlanningRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.SequenceNumber
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("planning")
internal class PlanningHandler(
    private val planningRepository: PlanningRepository,
    private val messagingTemplate: SimpMessageSendingOperations
) {
    @EventHandler
    @AllowReplay(true)
    fun handle(event: PlanningCreatedEvent, @SequenceNumber aggregateVersion: Long) {
        planningRepository.save(
            PlanningEntity(
                event.aggregateId.value,
                event.name,
                event.description,
                event.team.map { MemberEntity(it.value) },
                aggregateVersion
            )
        )

        broadcastUpdated()
    }

    @ResetHandler
    fun onReset() = planningRepository.deleteAll()

    private fun broadcastUpdated() =
        messagingTemplate.convertAndSend("/topic/plannings.update", planningRepository.findAll())
}