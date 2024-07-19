package printscript.group13.snippetmanager.asset.dto

import printscript.group13.snippetmanager.asset.model.ComplianceType
import java.util.UUID

data class SnippetDTO(
    val id: UUID,
    val name: String,
    val content: String,
    val language: String,
    val extension: String,
    val userId: String? = null,
    val complianceType: ComplianceType?,
)
