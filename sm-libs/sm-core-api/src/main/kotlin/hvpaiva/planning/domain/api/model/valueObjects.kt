package hvpaiva.planning.domain.api.model

import java.io.Serializable
import java.util.*

data class PlanningId(val value: UUID) : Serializable {
    constructor() : this(UUID.randomUUID())

    override fun toString(): String = value.toString()
}

data class TaskId(val value: UUID) : Serializable {
    constructor() : this(UUID.randomUUID())

    override fun toString(): String = value.toString()
}

data class TaskName(val code: String, val description: String)

data class Effort(val value: Int)