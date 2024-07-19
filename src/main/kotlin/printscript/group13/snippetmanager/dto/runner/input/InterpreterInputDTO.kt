package printscript.group13.snippetmanager.dto.runner.input

data class InterpreterInputDTO(
    val code: String,
    val version: String? = null,
    val inputs: List<String>? = null,
    val env: Map<String, Any>? = null
)
