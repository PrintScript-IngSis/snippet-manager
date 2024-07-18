package printscript.group13.snippetmanager.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import printscript.group13.snippetmanager.dto.SnippetDTO
import printscript.group13.snippetmanager.input.SnippetInput
import printscript.group13.snippetmanager.service.SnippetService
import java.util.UUID

@RestController
class SnippetController(private val snippetService: SnippetService) {
    @PostMapping()
    fun createSnippet(
        @Valid @RequestBody snippetInput: SnippetInput,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        return ResponseEntity.ok(snippetService.createSnippet(snippetInput, userId))
    }

    @PostMapping("/share/{id}")
    fun shareSnippet(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<String> {
        val userId = jwt.subject
        snippetService.shareSnippet(snippetId, userId)
        return ResponseEntity.ok("Snippet shared")
    }

//    @GetMapping()
//    fun getAllSnippets(
//        @AuthenticationPrincipal jwt: Jwt,
//    ): ResponseEntity<List<SnippetDTO>> {
//        val userId = jwt.subject
//        return ResponseEntity.ok(snippetService.getAllSnippets(userId))
//    }

    @GetMapping("/{id}")
    fun getSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        return ResponseEntity.ok(snippetService.getSnippetById(snippetId, userId))
    }

    @DeleteMapping("/{id}")
    fun deleteSnippet(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Unit> {
        val userId = jwt.subject
        snippetService.deleteSnippet(snippetId, userId)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable("id") snippetId: UUID,
        @Valid @RequestBody snippetInput: SnippetInput,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        return ResponseEntity.ok(snippetService.updateSnippet(snippetId, snippetInput, userId))
    }

    @PostMapping("/run/{id}")
    fun runSnippet(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<String> {
        val userId = jwt.subject
        return ResponseEntity.ok(snippetService.runSnippet(snippetId, userId))
    }
}
