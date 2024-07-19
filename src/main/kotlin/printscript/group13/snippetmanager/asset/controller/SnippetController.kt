package printscript.group13.snippetmanager.asset.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import printscript.group13.snippetmanager.asset.dto.ShareDTO
import printscript.group13.snippetmanager.asset.dto.SnippetDTO
import printscript.group13.snippetmanager.asset.dto.SnippetInputDTO
import printscript.group13.snippetmanager.asset.dto.SnippetUpdateDTO
import printscript.group13.snippetmanager.asset.service.SnippetService
import printscript.group13.snippetmanager.runner.input.InterpreterInputDTO
import printscript.group13.snippetmanager.runner.output.InterpreterOutput
import java.util.*

@RestController
@RequestMapping("/snippet")
@Tag(name = "Snippet")
@CrossOrigin("*")
class SnippetController(
    private val snippetService: SnippetService,
) {
    @PostMapping()
    fun createSnippet(
        @Valid @RequestBody snippetInput: SnippetInputDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        val snippet = snippetService.createAsset(snippetInput, userId)
        return ResponseEntity.ok(snippet)
    }

    @GetMapping()
    fun getSnippets(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<SnippetDTO>> {
        val userId = jwt.subject
        return ResponseEntity.ok(snippetService.getAssets(userId))
    }

    @GetMapping("/{id}")
    fun getSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val result = snippetService.getAssetById(snippetId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    fun deleteSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        snippetService.deleteAssetById(snippetId, userId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/share")
    fun shareSnippet(
        @Valid @RequestBody shareDTO: ShareDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        snippetService.shareAsset(userId, shareDTO)
        return ResponseEntity.noContent().build()
    }


    @PutMapping("/{id}")
    fun updateSnippet(
        @PathVariable("id") snippetId: UUID,
        @Valid @RequestBody input: SnippetUpdateDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        val snippet = snippetService.updateAsset(snippetId, input, userId)
        return ResponseEntity.ok(snippet)
    }

    @PostMapping("/run/{snippetId}")
    fun runSnippet(
        @PathVariable("snippetId") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody executeInput: InterpreterInputDTO,
    ): ResponseEntity<InterpreterOutput> {
        val userId = jwt.subject
        val result = snippetService.executeSnippet(snippetId, userId, executeInput)
        return ResponseEntity.ok(result)
    }
}
