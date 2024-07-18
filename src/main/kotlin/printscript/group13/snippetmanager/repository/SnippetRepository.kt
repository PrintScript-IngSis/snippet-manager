package printscript.group13.snippetmanager.repository

import org.springframework.data.jpa.repository.JpaRepository
import printscript.group13.snippetmanager.dto.Snippet
import java.util.UUID

interface SnippetRepository : JpaRepository<Snippet, UUID>
