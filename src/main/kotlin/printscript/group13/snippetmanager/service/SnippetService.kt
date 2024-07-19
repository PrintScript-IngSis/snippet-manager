package printscript.group13.snippetmanager.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import printscript.group13.snippetmanager.dto.*
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
        val newSnippet =
            Snippet(
                id = UUID.randomUUID(),
                name = snippetInput.name,
                code = snippetInput.code,
                language = snippetInput.language,
                userId = userId,
            )
        permissionService.createPermission(
            PermissionDTO(
                userId = userId,
                snippetId = newSnippet.id,
                permission = "owner",
            ),
        )
        val snippet = snippetRepository.save(newSnippet)
        return SnippetDTO(
            id = snippet.id,
            name = snippet.name,
            code = snippet.code,
            language = snippet.language,
        )
    }

    fun shareSnippet(
        shareDTO: ShareDTO,
        userId: String,
    ): ResponseEntity<Permission> {
        val permision = permissionService.getUserPermissions(userId, shareDTO.snippetId)
        if (permision.body?.permission == "owner") {
            return permissionService.createPermission(
                PermissionDTO(
                    userId = shareDTO.userId,
                    snippetId = shareDTO.snippetId,
                    permission = "read",
                ),
            )
        }
        return ResponseEntity.badRequest().build()
    }

//    fun getAllSnippets(userId: String): List<SnippetDTO> {
//        val snippets = snippetRepository.findById(userId)
//    }

    fun getSnippetById(
        snippetId: UUID,
        userId: String,
    ): SnippetDTO {
        if (permissionService.getUserPermissions(userId, snippetId).body?.permission != "") {
            val snippet = snippetRepository.findById(snippetId).get()
            return SnippetDTO(
                id = snippet.id,
                name = snippet.name,
                code = snippet.code,
                language = snippet.language,
            )
        } else {
            throw Exception("User does not have permission to access this snippet")
        }
    }

    fun deleteSnippet(
        snippetId: UUID,
        userId: String,
    ) {
        if (permissionService.getUserPermissions(userId, snippetId).body?.permission == "owner") {
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
        if (permissionService.getUserPermissions(userId, snippetId).body?.permission == "owner") {
            val snippet = snippetRepository.findById(snippetId).get()
            snippet.name = snippetInput.name
            snippet.code = snippetInput.code
            snippet.language = snippetInput.language
            snippetRepository.save(snippet)
            return SnippetDTO(
                id = snippet.id,
                name = snippet.name,
                code = snippet.code,
                language = snippet.language,
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
