package printscript.group13.snippetmanager.rule.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import printscript.group13.snippetmanager.rule.dto.FormatterRules
import printscript.group13.snippetmanager.rule.dto.LinterRuleInput
import printscript.group13.snippetmanager.rule.dto.RunRuleDTO
import printscript.group13.snippetmanager.rule.service.FormatterRuleService
import printscript.group13.snippetmanager.rule.service.LinterRuleService
import printscript.group13.snippetmanager.runner.output.FormatterOutput
import printscript.group13.snippetmanager.runner.output.LinterOutput


@RestController
@RequestMapping("/rule")
@CrossOrigin("*")
class RuleController(
    private val formatterRuleService: FormatterRuleService,
    private val linterRuleService: LinterRuleService,
) {
    @PostMapping("/linter")
    fun createOrGetLinterRules(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<LinterRuleInput> {
        return ResponseEntity.ok(linterRuleService.createOrGetRules(jwt.subject))
    }

    @PutMapping("/linter")
    suspend fun updateLinterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody linterRuleInput: LinterRuleInput,
    ): ResponseEntity<LinterRuleInput> {
        return ResponseEntity.ok(linterRuleService.updateRules(jwt.subject, linterRuleInput))
    }

    @PostMapping("/formatter")
    fun createOrGetFormatterRules(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<FormatterRules> {
        return ResponseEntity.ok(formatterRuleService.createOrGetRules(jwt.subject))
    }

    @PutMapping("/formatter")
    suspend fun updateFormatterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody formatterRules: FormatterRules,
    ): ResponseEntity<FormatterRules> {
        return ResponseEntity.ok(formatterRuleService.updateRules(jwt.subject, formatterRules))
    }

    @PostMapping("/linter/run")
    fun runLinterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody runRuleDTO: RunRuleDTO,
    ): ResponseEntity<LinterOutput> {
        return ResponseEntity.ok(linterRuleService.runRules(jwt.subject, runRuleDTO))
    }

    @PostMapping("/formatter/run")
    fun runFormatterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody runRuleDTO: RunRuleDTO,
    ): ResponseEntity<FormatterOutput> {
        return ResponseEntity.ok(formatterRuleService.runRules(jwt.subject, runRuleDTO))
    }
}
