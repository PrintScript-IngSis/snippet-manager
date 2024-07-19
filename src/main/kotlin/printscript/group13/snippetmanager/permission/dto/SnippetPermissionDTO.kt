package printscript.group13.snippetmanager.permission.dto

import java.util.UUID

data class SnippetPermissionDTO(
    val userId: String,
    val snippetId: UUID,
    val permission: String,
    val id: UUID,
)
