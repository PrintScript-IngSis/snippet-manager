package printscript.group13.snippetmanager.asset.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import printscript.group13.snippetmanager.asset.dto.PermissionDTO
import printscript.group13.snippetmanager.asset.dto.ShareDTO
import printscript.group13.snippetmanager.asset.dto.SnippetDTO
import printscript.group13.snippetmanager.asset.dto.SnippetInputDTO
import printscript.group13.snippetmanager.asset.dto.SnippetUpdateDTO
import printscript.group13.snippetmanager.asset.model.ComplianceType
import printscript.group13.snippetmanager.asset.model.Snippet
import printscript.group13.snippetmanager.asset.repository.SnippetRepository
import printscript.group13.snippetmanager.blob.AzureBlobService
import printscript.group13.snippetmanager.exceptions.InternalServerErrorException
import printscript.group13.snippetmanager.exceptions.PermissionNotFoundException
import printscript.group13.snippetmanager.exceptions.SnippetNotFoundException
import printscript.group13.snippetmanager.permission.service.PermissionService
import printscript.group13.snippetmanager.rule.service.LinterRuleService
import printscript.group13.snippetmanager.runner.input.InterpreterInputDTO
import printscript.group13.snippetmanager.runner.input.LinterInputDTO
import printscript.group13.snippetmanager.runner.output.InterpreterOutput
import printscript.group13.snippetmanager.runner.output.LinterOutput
import printscript.group13.snippetmanager.runner.service.RunnerService
import printscript.group13.snippetmanager.util.parseLintingRulesToString
import java.util.Date
import java.util.UUID

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val objectStoreService: AzureBlobService,
    private val permissionService: PermissionService,
    private val runnerService: RunnerService,
    private val linterRuleService: LinterRuleService,
) {
    private val logger = LoggerFactory.getLogger(SnippetService::class.java)

    fun createAsset(
        assetInput: SnippetInputDTO,
        userId: String,
    ): SnippetDTO {
        logger.info("Creating snippet")
        val snippetId = UUID.randomUUID()
        logger.info("Creating permission")
        val permissionResponse = permissionService.create(userId, snippetId, PermissionDTO(userId, snippetId, "owner"))
        if (permissionResponse.statusCode.is2xxSuccessful) {
            logger.info("Creating snippet in bucket")
            val storageResponse = objectStoreService.create(assetInput.content, snippetId)
            if (storageResponse.statusCode.is2xxSuccessful) {
                logger.info("Saving snippet on database")
                return saveSnippet(assetInput, snippetId, userId)
            }
        }
        logger.error("Error while creating snippet")
        throw InternalServerErrorException()
    }

    fun getAssetById(assetId: UUID): SnippetDTO {
        logger.info("Getting snippet by id")
        val result = snippetRepository.findById(assetId)
        if (result.isPresent) {
            logger.info("Snippet was in database")
            val snippet = result.get()
            logger.info("Getting snippet from bucket")
            val content = objectStoreService.get(assetId).body!!
            return SnippetDTO(
                assetId,
                snippet.name!!,
                content,
                snippet.language!!,
                snippet.extension!!,
                complianceType = snippet.compliance,
            )
        }
        logger.error("Snippet not found")
        throw SnippetNotFoundException()
    }

    fun getAssets(userId: String): List<SnippetDTO> {
        logger.info("Getting snippets by user id")
        val permissions = permissionService.getUserPermissionsByUserId(userId).body!!
        logger.info("Getting snippets where user has permissions in permission service")
        return permissions.map { permission ->
            val snippet = snippetRepository.findById(permission.snippetId).get()
            val content = objectStoreService.get(permission.snippetId).body!!

            SnippetDTO(
                permission.snippetId,
                snippet.name!!,
                content,
                snippet.language!!,
                snippet.extension!!,
                permission.userId,
                snippet.compliance,
            )
        }
    }

    fun updateAsset(
        assetId: UUID,
        assetInput: SnippetUpdateDTO,
        userId: String,
    ): SnippetDTO {
        logger.info("Finding snippet by id")
        val snippetOptional = snippetRepository.findById(assetId)
        if (snippetOptional.isPresent && hasPermissions(userId, assetId)) {
            logger.info("Snippet found and user has permissions to update")
            val snippet = snippetOptional.get()
            logger.info("Create or get rules linting for user")
            val lintingRules = linterRuleService.createOrGetRules(userId)
            val lintingRulesToString = parseLintingRulesToString(lintingRules)
            val lintingResult = applyRules(snippet, assetInput.content, lintingRulesToString)
            logger.info("Updating snippet")
            val snippetCopy = snippet.copy(updatedAt = Date(), compliance = lintingResult)
            snippetRepository.save(snippetCopy)
            logger.info("Updating snippet in bucket")
            objectStoreService.update(assetInput.content, assetId)
            logger.info("Snippet updated")
            return SnippetDTO(
                assetId,
                assetInput.name,
                assetInput.content,
                snippet.language!!,
                snippet.extension!!,
                complianceType = lintingResult,
            )
        }
        throw SnippetNotFoundException()
    }

    fun deleteAssetById(
        assetId: UUID,
        userId: String,
    ): String {
        logger.info("Finding snippet by id")
        val result = snippetRepository.findById(assetId)
        if (result.isPresent && isOwner(assetId, userId)) {
            logger.info("Snippet found and user is owner")
            val objectResponse = objectStoreService.delete(assetId)
            logger.info("Snippet deleted from bucket")
            val permissionsResponse = permissionService.deletePermissionsByAssetId(assetId)
            logger.info("Permissions deleted")
            if (objectResponse.statusCode.is2xxSuccessful && permissionsResponse.statusCode.is2xxSuccessful) {
                logger.info("Deleting snippet from database")
                snippetRepository.deleteById(assetId)
                return "Snippet deleted with id $assetId"
            } else {
                logger.error("Error while deleting snippet from bucket")
                throw InternalServerErrorException()
            }
        }
        logger.error("Snippet not found")
        throw SnippetNotFoundException()
    }

    fun shareAsset(
        userId: String,
        shareDTO: ShareDTO,
    ) {
        logger.info("Finding snippet by id")
        val result = snippetRepository.findById(shareDTO.snippetId)
        if (result.isPresent && isOwner(shareDTO.snippetId, userId)) {
            logger.info("Snippet found and user is owner")
            permissionService.create(shareDTO.userId, shareDTO.snippetId, PermissionDTO(shareDTO.userId, shareDTO.snippetId, "read"))
        } else {
            logger.error("Snippet not found")
            throw SnippetNotFoundException()
        }
    }

    fun executeSnippet(
        snippetId: UUID,
        userId: String,
        interpreterInput: InterpreterInputDTO,
    ): InterpreterOutput {
        logger.info("Finding snippet by id")
        val snippet = snippetRepository.findById(snippetId)
        if (snippet.isPresent) {
            logger.info("Snippet found")
            if (!hasPermissions(userId, snippetId)) {
                logger.error("User has not permissions to run snippet")
                throw PermissionNotFoundException()
            }
            logger.info("Running snippet")
            val result = runnerService.runCode(interpreterInput)
            return result.body!!
        }
        logger.error("Snippet not found")
        throw SnippetNotFoundException()
    }

    private fun applyRules(
        snippet: Snippet,
        content: String,
        linterRules: String,
    ): ComplianceType {
        val result =
            runnerService.lintCode(
                LinterInputDTO(content, snippet.language, linterRules),
            )

        return getSnippetCompliance(result)
    }

    private fun getSnippetCompliance(result: LinterOutput): ComplianceType {
        if (result.errors.isNotBlank()) {
            return if (result.errors.isBlank()) {
                ComplianceType.NOT_COMPLIANT
            } else {
                ComplianceType.FAILED
            }
        }
        return ComplianceType.COMPLIANT
    }

    private fun saveSnippet(
        snippetInput: SnippetInputDTO,
        snippetId: UUID,
        userId: String,
    ): SnippetDTO {
        val snippetSaved =
            snippetRepository.save(
                Snippet(
                    id = snippetId,
                    name = snippetInput.name,
                    language = snippetInput.language,
                    extension = snippetInput.extension,
                    compliance = ComplianceType.PENDING,
                ),
            )
        return SnippetDTO(
            snippetId,
            snippetInput.name,
            snippetInput.content,
            snippetInput.language,
            snippetInput.extension,
            userId = userId,
            complianceType = snippetSaved.compliance,
        )
    }

    private fun isOwner(
        assetId: UUID,
        userId: String,
    ) = permissionService.getUserPermissionByAssetId(assetId, userId).body!!.permission == "owner"

    private fun hasPermissions(
        userId: String,
        snippetId: UUID,
    ): Boolean {
        val response = permissionService.getUserPermissionByAssetId(snippetId, userId).body!!
        return response.permission != "read"
    }
}
