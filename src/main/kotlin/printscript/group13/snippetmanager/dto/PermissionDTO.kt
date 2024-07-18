package printscript.group13.snippetmanager.dto

import java.util.UUID

data class PermissionDTO(
    val userId: String,
    val snippetId: UUID,
    val type: String
)
