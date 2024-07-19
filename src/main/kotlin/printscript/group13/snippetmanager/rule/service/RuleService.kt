package printscript.group13.snippetmanager.rule.service

import printscript.group13.snippetmanager.rule.dto.RunRuleDTO


interface RuleService<Rules, Output> {
    fun createOrGetRules(userId: String): Rules

    suspend fun updateRules(
        userId: String,
        rules: Rules,
    ): Rules

    fun runRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): Output

    abstract fun AzureObjectStoreService(linterBucketUrl: String): Output
}
