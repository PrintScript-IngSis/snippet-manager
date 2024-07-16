package printscript.group13.snippetmanager.permissions.model

import java.util.UUID

interface Permission {
    val assetId: UUID
    val permission: String
}
