package printscript.group13.snippetmanager.service

import PermissionTypeDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
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
    fun createPermission(permissionDTO: PermissionDTO): ResponseEntity<Permission> {
        val completeUrl = "$url/${permissionDTO.snippetId}/users/${permissionDTO.userId}"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val permissionDTOType = PermissionTypeDTO(permissionDTO.permission)
        val requestEntity = HttpEntity(permissionDTOType, headers)

        val response = restTemp.postForEntity(completeUrl, requestEntity, Permission::class.java)
        val createdPermission = response.body
        return ResponseEntity(createdPermission, response.statusCode)
    }

    fun getUserPermissions(
        userId: String,
        snippetId: UUID,
    ): ResponseEntity<PermissionDTO> {
        val completeUrl = "$url/$snippetId/users/$userId"
        val response = restTemp.getForEntity(completeUrl, PermissionDTO::class.java)
        val permissions = response.body
        return ResponseEntity(permissions, response.statusCode)
    }

    fun updatePermission(permissionDTO: PermissionDTO): ResponseEntity<Permission> {
        val completeUrl = "$url/api/permissions/${permissionDTO.snippetId}/users/${permissionDTO.userId}"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val permissionDTOType = PermissionTypeDTO(permissionDTO.permission)
        val requestEntity = HttpEntity(permissionDTOType, headers)

        val response = restTemp.exchange(completeUrl, HttpMethod.PATCH, requestEntity, Permission::class.java)
        return ResponseEntity(response.body, response.statusCode)
    }

    fun deletePermission(snippetId: UUID): ResponseEntity<Unit> {
        val completeUrl = "$url/$snippetId"
        restTemp.delete(completeUrl)
        return ResponseEntity.noContent().build()
    }

    fun getPermissionsByUserId(userId: String): ResponseEntity<List<Permission>> {
        val completeUrl = "$url/user/$userId"
        val response = restTemp.getForEntity(completeUrl, Array<Permission>::class.java)
        val permissions = response.body?.toList()
        return ResponseEntity(permissions, response.statusCode)
    }
}
