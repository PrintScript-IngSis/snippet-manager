package printscript.group13.snippetmanager.dto

import java.util.UUID

data class SnippetDTO(
    val id: UUID,
    val name: String,
    val code: String,
    val language: String,
)
