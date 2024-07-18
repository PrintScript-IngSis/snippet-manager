package printscript.group13.snippetmanager.dto

import java.util.*

interface Permission {
    val userId: UUID
    val snippetId: UUID
    val permission: String
    val username: String
}
