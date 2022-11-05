package hvpaiva.query.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
data class PlanningEntity(
    @Id var id: String,
    var name: String,
    var description: String?,
    @ManyToMany var team: List<MemberEntity> = emptyList(),
    var aggregateVersion: Long
)
