package printscript.group13.snippetmanager.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import printscript.group13.snippetmanager.dto.*
import printscript.group13.snippetmanager.exceptions.*
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
        if (snippetInput.name.isBlank() || snippetInput.code.isBlank()) {
            throw SnippetValidationException("Snippet name or code cannot be empty")
        }

        val newSnippet = Snippet(
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
        val permission = permissionService.getUserPermissions(userId, shareDTO.snippetId)
        if (permission.body?.permission == "owner") {
            if (permissionService.getUserPermissions(shareDTO.userId, shareDTO.snippetId).body?.permission != null) {
                throw PermissionAlreadyExistsException()
            }
            return permissionService.createPermission(
                PermissionDTO(
                    userId = shareDTO.userId,
                    snippetId = shareDTO.snippetId,
                    permission = "read",
                ),
            )
        }
        throw InvalidShareRequestException()
    }

    fun getSnippetById(
        snippetId: UUID,
        userId: String,
    ): SnippetDTO {
        val permission = permissionService.getUserPermissions(userId, snippetId)
        if (permission.body?.permission != "" && permission.hasBody()) {
            val snippet = snippetRepository.findById(snippetId).orElseThrow { SnippetNotFoundException() }
            return SnippetDTO(
                id = snippet.id,
                name = snippet.name,
                code = snippet.code,
                language = snippet.language,
            )
        } else {
            throw PermissionDeniedException()
        }
    }

    fun deleteSnippet(
        snippetId: UUID,
        userId: String,
    ) {
        val permission = permissionService.getUserPermissions(userId, snippetId)
        if (permission.body?.permission == "owner") {
            snippetRepository.deleteById(snippetId)
        } else {
            throw PermissionDeniedException()
        }
    }

    fun updateSnippet(
        snippetId: UUID,
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO {
        val permission = permissionService.getUserPermissions(userId, snippetId)
        if (permission.body?.permission == "owner") {
            val snippet = snippetRepository.findById(snippetId).orElseThrow { SnippetNotFoundException() }
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
            throw PermissionDeniedException()
        }
    }

    fun runSnippet(
        snippetId: UUID,
        userId: String,
    ): String? {
        // Implement snippet running logic here
        return null
    }
}
