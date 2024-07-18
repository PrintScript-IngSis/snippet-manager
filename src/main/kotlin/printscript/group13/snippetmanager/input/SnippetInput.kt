package printscript.group13.snippetmanager.input

import org.jetbrains.annotations.NotNull

data class SnippetInput(
    @NotNull
    val name: String,
    @NotNull
    val code: String,
    @NotNull
    val language: String
)