package printscript.group13.snippetmanager.asset.dto

import java.util.UUID

data class PermissionDTO(
    val userId: String?,
    val snippetId: UUID?,
    val permission: String,
)
