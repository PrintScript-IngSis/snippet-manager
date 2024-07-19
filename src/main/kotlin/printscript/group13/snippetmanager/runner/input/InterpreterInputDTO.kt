package printscript.group13.snippetmanager.runner.input

data class InterpreterInputDTO(
    val code: String,
    val version: String? = null,
    val input: String? = null,
    val env: Map<String, Any>? = null,
)
