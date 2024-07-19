package printscript.group13.snippetmanager.dto

import java.util.UUID

data class Permission (
    val userId: String,
    val snippetId: UUID,
    val permission: String,
    val id: UUID
)
