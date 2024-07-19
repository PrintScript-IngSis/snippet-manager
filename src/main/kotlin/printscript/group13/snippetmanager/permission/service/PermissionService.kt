package printscript.group13.snippetmanager.permission.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import printscript.group13.snippetmanager.asset.dto.PermissionDTO
import printscript.group13.snippetmanager.permission.dto.SnippetPermissionDTO
import java.util.*

@Service
class PermissionService(
    @Value("\${permission.url}/api/permissions") private val permissionUrl: String,
    private val restTemplate: RestTemplate,
) {
    private val logger = LoggerFactory.getLogger(PermissionService::class.java)
    fun create(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<SnippetPermissionDTO> {
        val url = "$permissionUrl/$assetId/users/$userId"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        logger.info("Creating permission for user $userId and asset $assetId")

        val requestEntity = HttpEntity(permission, headers)

        val response = restTemplate.postForEntity(url, requestEntity, SnippetPermissionDTO::class.java)
        logger.info("Permission created for user $userId and asset $assetId")
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    fun getUserPermissionByAssetId(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<SnippetPermissionDTO> {
        val url = "$permissionUrl/$assetId/users/$userId"
        logger.info("Getting permission for user $userId and asset $assetId")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity<Void>(headers)

        val response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, SnippetPermissionDTO::class.java)
        logger.info("Permission retrieved for user $userId and asset $assetId")
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    fun getUserPermissionsByUserId(userId: String): ResponseEntity<List<SnippetPermissionDTO>> {
        logger.info("Getting permissions for user $userId")
        val url = "$permissionUrl/user/$userId"
        logger.info("Permissions retrieved for user $userId")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Array<SnippetPermissionDTO>::class.java)
        logger.info("Permissions retrieved for user $userId")
        val createdSnippetPermission = response.body?.toList()
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    fun updatePermission(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<SnippetPermissionDTO> {
        val url = "$permissionUrl/$assetId/users/$userId"
        logger.info("Updating permission for user $userId and asset $assetId")

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity(permission, headers)

        val response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, SnippetPermissionDTO::class.java)

        logger.info("Permission updated for user $userId and asset $assetId")
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    fun deletePermissionsByAssetId(assetId: UUID): ResponseEntity<Unit> {
        logger.info("Deleting permissions for asset $assetId")

        val url = "$permissionUrl/$assetId"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity<Void>(headers)

        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Unit::class.java)

        logger.info("Permissions deleted for asset $assetId")
        return ResponseEntity.noContent().build()
    }
}
