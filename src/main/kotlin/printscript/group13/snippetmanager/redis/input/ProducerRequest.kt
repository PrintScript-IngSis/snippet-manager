package printscript.group13.snippetmanager.redis.input

import java.util.UUID

data class ProducerRequest(
    val snippetId: UUID,
    val userId: String,
)
