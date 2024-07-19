package printscript.group13.snippetmanager.service

import PermissionTypeDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import printscript.group13.snippetmanager.dto.Permission
import printscript.group13.snippetmanager.dto.PermissionDTO
import java.util.UUID

@Service
class PermissionService(
    @Value("http://snippet-permission-app:8081/api/permissions") private val url: String,
    private val restTemp: RestTemplate,
) {
    private val logger = LoggerFactory.getLogger(PermissionService::class.java)
    fun createPermission(permissionDTO: PermissionDTO): ResponseEntity<Permission> {
        val completeUrl = "$url/${permissionDTO.snippetId}/users/${permissionDTO.userId}"
        logger.info("Creating permission for ${permissionDTO.userId}")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val permissionDTOType = PermissionTypeDTO(permissionDTO.permission)
        val requestEntity = HttpEntity(permissionDTOType, headers)

        val response = restTemp.postForEntity(completeUrl, requestEntity, Permission::class.java)
        val createdPermission = response.body
        if (createdPermission != null) {
            logger.info("Permission created for userId ${createdPermission.userId} and snippet id ${createdPermission.snippetId}")
        }
        return ResponseEntity(createdPermission, response.statusCode)
    }

    fun getUserPermissions(
        userId: String,
        snippetId: UUID,
    ): ResponseEntity<PermissionDTO> {
        val completeUrl = "$url/$snippetId/users/$userId"
        logger.info("Getting user permissions for userId $userId")
        val response = restTemp.getForEntity(completeUrl, PermissionDTO::class.java)
        val permissions = response.body
        logger.info("Permissions found for userId $userId")
        return ResponseEntity(permissions, response.statusCode)
    }

    fun updatePermission(permissionDTO: PermissionDTO): ResponseEntity<Permission> {
        val completeUrl = "$url/api/permissions/${permissionDTO.snippetId}/users/${permissionDTO.userId}"
        logger.info("Updating permission for ${permissionDTO.userId}")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val permissionDTOType = PermissionTypeDTO(permissionDTO.permission)
        val requestEntity = HttpEntity(permissionDTOType, headers)
        val response = restTemp.exchange(completeUrl, HttpMethod.PATCH, requestEntity, Permission::class.java)
        return ResponseEntity(response.body, response.statusCode)
    }

    fun deletePermission(snippetId: UUID): ResponseEntity<Unit> {
        val completeUrl = "$url/$snippetId"
        logger.info("Deleting user permissions for snippetId $snippetId")
        restTemp.delete(completeUrl)
        logger.info("User deleted for snippetId $snippetId")
        return ResponseEntity.noContent().build()
    }

}
