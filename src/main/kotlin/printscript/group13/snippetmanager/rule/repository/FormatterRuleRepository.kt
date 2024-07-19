package printscript.group13.snippetmanager.rule.repository

import org.springframework.data.jpa.repository.JpaRepository
import printscript.group13.snippetmanager.rule.model.FormatterRule
import java.util.Optional
import java.util.UUID

interface FormatterRuleRepository : JpaRepository<FormatterRule, UUID> {
    fun findByUserId(userId: String): Optional<FormatterRule>
}
