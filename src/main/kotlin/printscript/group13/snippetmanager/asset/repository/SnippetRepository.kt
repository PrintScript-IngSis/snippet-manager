package printscript.group13.snippetmanager.asset.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import printscript.group13.snippetmanager.asset.model.Snippet
import java.util.UUID

@Repository
interface SnippetRepository : JpaRepository<Snippet, UUID>
