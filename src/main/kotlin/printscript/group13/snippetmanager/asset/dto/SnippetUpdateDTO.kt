package printscript.group13.snippetmanager.asset.dto

import jakarta.validation.constraints.NotNull

data class SnippetUpdateDTO(
    @field:NotNull(message = "name type is missing")
    val name: String,
    @field:NotNull(message = "content type is missing")
    val content: String,
)