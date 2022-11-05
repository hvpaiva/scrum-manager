package hvpaiva.planning.domain.api.model

import java.io.Serializable
import java.util.*

data class PlanningId(val value: String) : Serializable {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = value
}

data class TaskId(val value: String) : Serializable {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = value
}

data class MemberId(val value: String) : Serializable {
    constructor() : this(UUID.randomUUID().toString())

    override fun toString(): String = value
}

data class TaskName(val code: String, val description: String)

data class Effort(val value: Int) {
    companion object {
        val NONE = Effort()
    }

    constructor() : this(-1)

    override fun toString(): String {
        if (value == -1) {
            return "None"
        }
        return value.toString()
    }


}

data class Estimation(val member: MemberId, val effort: Effort)