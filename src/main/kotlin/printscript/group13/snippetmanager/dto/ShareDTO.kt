package printscript.group13.snippetmanager.dto

import java.util.UUID

data class ShareDTO(
    val snippetId: UUID,
    val userId: String
)