package printscript.group13.snippetmanager.service

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import printscript.group13.snippetmanager.dto.*
import printscript.group13.snippetmanager.dto.runner.input.FormatterInputDTO
import printscript.group13.snippetmanager.dto.runner.input.InterpreterInputDTO
import printscript.group13.snippetmanager.dto.runner.input.LinterInputDTO
import printscript.group13.snippetmanager.dto.runner.output.FormatterOutput
import printscript.group13.snippetmanager.dto.runner.output.InterpreterOutput
import printscript.group13.snippetmanager.dto.runner.output.LinterOutput
import printscript.group13.snippetmanager.exceptions.*
import printscript.group13.snippetmanager.dto.Permission
import printscript.group13.snippetmanager.dto.PermissionDTO
import printscript.group13.snippetmanager.dto.ShareDTO
import printscript.group13.snippetmanager.dto.Snippet
import printscript.group13.snippetmanager.dto.SnippetDTO
import printscript.group13.snippetmanager.exceptions.InvalidShareRequestException
import printscript.group13.snippetmanager.exceptions.PermissionDeniedException
import printscript.group13.snippetmanager.exceptions.PermissionNotFoundException
import printscript.group13.snippetmanager.exceptions.SnippetNotFoundException
import printscript.group13.snippetmanager.exceptions.SnippetValidationException
import printscript.group13.snippetmanager.input.SnippetInput
import printscript.group13.snippetmanager.repository.SnippetRepository
import java.util.UUID

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val permissionService: PermissionService,
    private val runnerService: RunnerService,
) {
    private val logger = LoggerFactory.getLogger(SnippetService::class.java)
    fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO {
        if (snippetInput.name.isBlank() || snippetInput.code.isBlank()) {
            throw SnippetValidationException("Snippet name or code cannot be empty")
        }
        logger.info("Creating snippet")

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
        val permission: ResponseEntity<PermissionDTO>
        try {
            permission = permissionService.getUserPermissions(userId, shareDTO.snippetId)
        } catch (e: HttpClientErrorException.NotFound) {
            throw PermissionNotFoundException()
        }
        if (permission.body?.permission == "owner") {
            logger.info("Sharing snippet")
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
        val permission: ResponseEntity<PermissionDTO>
        try {
            permission = permissionService.getUserPermissions(userId, snippetId)
        } catch (e: HttpClientErrorException.NotFound) {
            throw PermissionNotFoundException()
        }

        if (permission.body?.permission != "" && permission.hasBody()) {
            val snippet = snippetRepository.findById(snippetId).orElseThrow { SnippetNotFoundException() }
            logger.info("Getting snippet by id: $snippetId")
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
        val permission: ResponseEntity<PermissionDTO>
        try {
            permission = permissionService.getUserPermissions(userId, snippetId)
        } catch (e: HttpClientErrorException.NotFound) {
            logger.info("Permission not found to delete snippet id: $snippetId")
            throw PermissionNotFoundException()
        }
        if (permission.body?.permission == "owner") {
            snippetRepository.deleteById(snippetId)
            permissionService.deletePermission(snippetId)
            logger.info("Deleted snippet id: $snippetId")
        }
        else {
            logger.info("Permission denied to delete snippet id: $snippetId")
            throw PermissionDeniedException()
        }
    }

    fun updateSnippet(
        snippetId: UUID,
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO {
        val permission: ResponseEntity<PermissionDTO>
        try {
            permission = permissionService.getUserPermissions(userId, snippetId)
        } catch (e: HttpClientErrorException.NotFound) {
            throw PermissionNotFoundException()
        }
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
    ): InterpreterOutput? {
        val permission: ResponseEntity<PermissionDTO>
        try {
            permission = permissionService.getUserPermissions(userId, snippetId)
        } catch (e: HttpClientErrorException.NotFound) {
            throw PermissionNotFoundException()
        }
        if (permission.body?.permission != "" && permission.hasBody()) {
            val snippet = snippetRepository.findById(snippetId).orElseThrow() { SnippetNotFoundException() }
            val runnerInput = InterpreterInputDTO(snippet.code)
            val response = runnerService.runCode(runnerInput)
            return if (response.statusCode.is2xxSuccessful) {
                response.body
            } else {
                throw InterpreterException(response.body?.error ?: "There was an error while running the code")
            }
        } else {
            throw PermissionDeniedException()
        }
    }

    fun lintSnippet(
        snippetId: UUID,
        userId: String,
    ): LinterOutput? {
        val permission: ResponseEntity<PermissionDTO>
        try {
            permission = permissionService.getUserPermissions(userId, snippetId)
        } catch (e: HttpClientErrorException.NotFound) {
            throw PermissionNotFoundException()
        }
        if (permission.body?.permission != "" && permission.hasBody()) {
            val snippet = snippetRepository.findById(snippetId).orElseThrow() { SnippetNotFoundException() }
            val runnerInput = LinterInputDTO(snippet.code)
            val response = runnerService.lintCode(runnerInput)
            return if (response.statusCode.is2xxSuccessful) {
                response.body
            } else {
                throw LinterException("There was an error while linting the code")
            }
        } else {
            throw PermissionDeniedException()
        }
    }

    fun formatSnippet(
        snippetId: UUID,
        userId: String,
    ): FormatterOutput? {
        val permission: ResponseEntity<PermissionDTO>
        try {
            permission = permissionService.getUserPermissions(userId, snippetId)
        } catch (e: HttpClientErrorException.NotFound) {
            throw PermissionNotFoundException()
        }
        if (permission.body?.permission != "" && permission.hasBody()) {
            val snippet = snippetRepository.findById(snippetId).orElseThrow() { SnippetNotFoundException() }
            val runnerInput = FormatterInputDTO(snippet.code)
            val response = runnerService.formatCode(runnerInput)
            return if (response.statusCode.is2xxSuccessful) {
                response.body
            } else {
                throw FormatterException(response.body?.error ?: "There was an error while formatting the code")
            }
        } else {
            throw PermissionDeniedException()
        }
    }
}
