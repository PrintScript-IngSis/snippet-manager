package printscript.group13.snippetmanager.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import printscript.group13.snippetmanager.rule.dto.FormatterRuleInput
import printscript.group13.snippetmanager.rule.dto.FormatterRules
import printscript.group13.snippetmanager.rule.dto.LinterRuleInput

fun parseToLinterRules(rules: String): LinterRuleInput {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<LinterRuleInput>(rules)
}

fun parseLintingRulesToString(linterRuleInput: LinterRuleInput): String {
    val json = Json { ignoreUnknownKeys = true }
    return json.encodeToString(linterRuleInput)
}

fun parseToFormatterRules(rulesJson: String): List<FormatterRuleInput> {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<List<FormatterRuleInput>>(rulesJson)
}

fun parseFormattingRulesToString(formatterRules: FormatterRules): String {
    val json = Json { ignoreUnknownKeys = true }
    return json.encodeToString(formatterRules.rules)
}
