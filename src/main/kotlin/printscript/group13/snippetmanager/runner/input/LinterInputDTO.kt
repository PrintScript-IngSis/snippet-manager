package printscript.group13.snippetmanager.runner.input

data class LinterInputDTO(
    val code: String,
    val version: String? = null,
    val rules: String? = null,
)
