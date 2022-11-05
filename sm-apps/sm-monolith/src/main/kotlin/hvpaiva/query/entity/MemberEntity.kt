package hvpaiva.query.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class MemberEntity(
    @Id val id: String
)