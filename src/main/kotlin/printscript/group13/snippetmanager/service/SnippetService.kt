package printscript.group13.snippetmanager.service

import org.springframework.stereotype.Service
import printscript.group13.snippetmanager.dto.PermissionDTO
import printscript.group13.snippetmanager.dto.Snippet
import printscript.group13.snippetmanager.dto.SnippetDTO
import printscript.group13.snippetmanager.input.SnippetInput
import printscript.group13.snippetmanager.repository.SnippetRepository
import java.util.UUID

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionService: PermissionService,
) {
    fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO {
        val newSnippet = Snippet(
            id = UUID.randomUUID(),
            name = snippetInput.name,
            code = snippetInput.code,
            language = snippetInput.language,
            userId = userId
        )
        permissionService.createPermission(PermissionDTO(
            userId = userId,
            snippetId = newSnippet.id,
            type = "owner")
        )
        val snippet = snippetRepository.save(newSnippet)
        return SnippetDTO(
            id = snippet.id,
            name = snippet.name,
            code = snippet.code,
            language = snippet.language
        )
    }

    fun shareSnippet(
        snippetId: UUID,
        userId: String,
    ) {
        val permision = permissionService.getUserPermissions(userId, snippetId)
        if (permision.body?.type  == "owner") {
            permissionService.createPermission(PermissionDTO(
                userId = userId,
                snippetId = snippetId,
                type = "read")
            )
        }
    }

//    fun getAllSnippets(userId: String): List<SnippetDTO> {
//        val snippets = snippetRepository.findById(userId)
//    }

    fun getSnippetById(
        snippetId: UUID,
        userId: String,
    ): SnippetDTO {
        if(permissionService.getUserPermissions(userId, snippetId).body?.type != "") {
            val snippet = snippetRepository.findById(snippetId).get()
            return SnippetDTO(
                id = snippet.id,
                name = snippet.name,
                code = snippet.code,
                language = snippet.language
            )
        } else {
            throw Exception("User does not have permission to access this snippet")
        }
    }

    fun deleteSnippet(
        snippetId: UUID,
        userId: String,
    ) {
        if (permissionService.getUserPermissions(userId, snippetId).body?.type == "owner") {
//            permissionService.deletePermission(userId, snippetId)
            snippetRepository.deleteById(snippetId)
        } else {
            throw Exception("User does not have permission to delete this snippet")
        }
    }

    fun updateSnippet(
        snippetId: UUID,
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO? {
        if (permissionService.getUserPermissions(userId, snippetId).body?.type == "owner") {
            val snippet = snippetRepository.findById(snippetId).get()
            snippet.name = snippetInput.name
            snippet.code = snippetInput.code
            snippet.language = snippetInput.language
            snippetRepository.save(snippet)
            return SnippetDTO(
                id = snippet.id,
                name = snippet.name,
                code = snippet.code,
                language = snippet.language
            )
        } else {
            throw Exception("User does not have permission to update this snippet")
        }
    }

    fun runSnippet(
        snippetId: UUID,
        userId: String,
    ): String? {
        return null
    }
}
