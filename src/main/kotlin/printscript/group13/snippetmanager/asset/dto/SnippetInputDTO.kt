package printscript.group13.snippetmanager.asset.dto

import jakarta.validation.constraints.NotNull

data class SnippetInputDTO(
    @field:NotNull(message = "name type is missing")
    val name: String,
    @field:NotNull(message = "content type is missing")
    val content: String,
    @field:NotNull(message = "property language is missing")
    val language: String,
    @field:NotNull(message = "property type is missing")
    val extension: String,
    @field:NotNull(message = "userName inputs is missing")
    val userName: String,
)