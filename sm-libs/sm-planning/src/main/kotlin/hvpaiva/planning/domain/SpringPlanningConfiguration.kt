package hvpaiva.planning.domain

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class SpringPlanningConfiguration {
    @Value("\${axon.snapshot.trigger.threshold.planning}")
    private val snapshotTriggerThresholdPlanning: Int = 100

    @Value("\${axon.snapshot.trigger.threshold.task}")
    private val snapshotTriggerThresholdTask: Int = 100

    @Bean("planningSnapshotTriggerDefinition")
    fun customerSnapshotTriggerDefinition(snapshottter: Snapshotter) =
        EventCountSnapshotTriggerDefinition(snapshottter, snapshotTriggerThresholdPlanning)

    @Bean("taskSnapshotTriggerDefinition")
    fun customerOrderSnapshotTriggerDefinition(snapshottter: Snapshotter) =
        EventCountSnapshotTriggerDefinition(snapshottter, snapshotTriggerThresholdTask)

}