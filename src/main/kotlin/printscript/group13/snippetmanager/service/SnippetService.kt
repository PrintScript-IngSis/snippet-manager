package printscript.group13.snippetmanager.service

import org.springframework.stereotype.Service
import printscript.group13.snippetmanager.dto.SnippetDTO
import printscript.group13.snippetmanager.input.SnippetInput
import printscript.group13.snippetmanager.repository.SnippetRepository
import java.util.UUID

@Service
class SnippetService(private val snippetRepository: SnippetRepository) {
    fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO? {
        return null
    }

    fun shareSnippet(
        snippetId: UUID,
        userId: String,
    ): String? {
        return null
    }

    fun getAllSnippets(userId: String): List<SnippetDTO>? {
        return null
    }

    fun getSnippetById(
        snippetId: UUID,
        userId: String,
    ): SnippetDTO? {
        return null
    }

    fun deleteSnippet(
        snippetId: UUID,
        userId: String,
    ): Unit? {
        return null
    }

    fun updateSnippet(
        snippetId: UUID,
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO? {
        return null
    }

    fun runSnippet(
        snippetId: UUID,
        userId: String,
    ): String? {
        return null
    }
}
