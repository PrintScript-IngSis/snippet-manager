package printscript.group13.snippetmanager.rule.repository


import org.springframework.data.jpa.repository.JpaRepository
import printscript.group13.snippetmanager.rule.model.LinterRule
import java.util.Optional
import java.util.UUID

interface LinterRuleRepository : JpaRepository<LinterRule, UUID> {
    fun findByUserId(userId: String): Optional<LinterRule>
}
