package printscript.group13.snippetmanager.runner.input

import printscript.group13.snippetmanager.rule.dto.FormatterRuleInput

data class FormatterInputDTO(
    val code: String,
    val version: String? = null,
    val rules: List<FormatterRuleInput>? = null,
)
