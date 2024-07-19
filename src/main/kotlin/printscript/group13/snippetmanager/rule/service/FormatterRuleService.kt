package printscript.group13.snippetmanager.rule.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import printscript.group13.snippetmanager.blob.AzureBlobService
import printscript.group13.snippetmanager.exceptions.RuleNotFoundException
import printscript.group13.snippetmanager.permission.service.PermissionService
import printscript.group13.snippetmanager.rule.dto.FormatterRuleInput
import printscript.group13.snippetmanager.rule.dto.FormatterRules
import printscript.group13.snippetmanager.rule.dto.RunRuleDTO
import printscript.group13.snippetmanager.rule.model.FormatterRule
import printscript.group13.snippetmanager.rule.repository.FormatterRuleRepository
import printscript.group13.snippetmanager.rule.util.createDefaultFormatterRules
import printscript.group13.snippetmanager.runner.input.FormatterInputDTO
import printscript.group13.snippetmanager.runner.output.FormatterOutput
import printscript.group13.snippetmanager.runner.service.RunnerService
import printscript.group13.snippetmanager.util.parseFormattingRulesToString
import printscript.group13.snippetmanager.util.parseToFormatterRules
import java.util.Optional
import java.util.UUID

@Service
class FormatterRuleService(
    private val formatterRuleRepository: FormatterRuleRepository,
    private val runnerService: RunnerService,
    @Value("\${formatter.blob.url}") private val formatterBlobUrl: String,
    private val permissionService: PermissionService,
) {
    @Autowired
    private val bucket = AzureBlobService(formatterBlobUrl)

    private val logger = LoggerFactory.getLogger(FormatterRuleService::class.java)

    fun createOrGetRules(userId: String): FormatterRules {
        logger.info("Creating or formatting getting rules for user $userId")
        val formatterRules = formatterRuleRepository.findByUserId(userId)
        logger.info("Rules found for user $userId")
        return if (formatterRules.isPresent) {
            logger.info("Rules found for user $userId")
            val formatterRulesInputList = getFormatterRules(formatterRules.get())
            return FormatterRules(formatterRulesInputList)
        } else {
            createFormatterRules(userId)
        }
    }

    suspend fun updateRules(
        userId: String,
        rules: FormatterRules,
    ): FormatterRules {
        logger.info("Updating rules for user $userId")
        val formatterRule = formatterRuleRepository.findByUserId(userId)
        logger.info("Rules found for user $userId")
        if (formatterRule.isPresent) {
            return update(rules, formatterRule)
        } else {
            logger.error("User has not linting rules defined")
            throw RuleNotFoundException("User has not linting rules defined")
        }
    }

    fun runRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): FormatterOutput {
        val formatterRules = formatterRuleRepository.findByUserId(userId)
        if (canApplyRules(userId, runRuleDTO.snippetId!!)) {
            if (formatterRules.isEmpty) {
                val result = createFormatterRules(userId)
                return runnerService.formatCode(FormatterInputDTO(runRuleDTO.content!!, runRuleDTO.language, result.rules))
            }
            val formatterRulesInputList = getFormatterRules(formatterRules.get())
            return runnerService.formatCode(FormatterInputDTO(runRuleDTO.content!!, runRuleDTO.language, formatterRulesInputList))
        } else {
            logger.error("User does not have permission to apply rules")
            return FormatterOutput("", "User does not have permission to apply rules.")
        }
    }

    private fun update(
        formatterRules: FormatterRules,
        formatterRule: Optional<FormatterRule>,
    ): FormatterRules {
        val rules = parseFormattingRulesToString(formatterRules)
        bucket.update(rules, formatterRule.get().id!!)
        return formatterRules
    }

    private fun createFormatterRules(userId: String): FormatterRules {
        val defaultRules = createDefaultFormatterRules()
        val formatterRuleId = UUID.randomUUID()
        logger.info("Saving formatting rules into bucket for user $userId")
        bucket.create(parseFormattingRulesToString(defaultRules), formatterRuleId)
        logger.info("Saving formatting rules into database for user $userId")
        formatterRuleRepository.save(FormatterRule(id = formatterRuleId, userId = userId))
        return defaultRules
    }

    private fun getFormatterRules(formatterRule: FormatterRule): List<FormatterRuleInput> {
        val rulesJson = bucket.get(formatterRule.id!!).body!!
        return parseToFormatterRules(rulesJson)
    }

    private fun canApplyRules(
        userId: String,
        snippetId: UUID,
    ): Boolean {
        val response = permissionService.getUserPermissionByAssetId(snippetId, userId).body!!
        return response.permission != "read"
    }
}
