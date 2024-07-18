package printscript.group13.snippetmanager.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import printscript.group13.snippetmanager.dto.Permission
import printscript.group13.snippetmanager.dto.PermissionDTO
import java.util.UUID

@Service
class PermissionService(@Value("\${permissions}") private val url: String) {
    private lateinit var restTemp: RestTemplate

    fun createPermission(permissionDTO: PermissionDTO): ResponseEntity<Permission> {
        val completeUrl = "$url/${permissionDTO.snippetId}/users/${permissionDTO.userId}"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity(permissionDTO, headers)

        val response = restTemp.postForEntity(completeUrl, requestEntity, Permission::class.java)
        val createdPermission = response.body
        return ResponseEntity(createdPermission, response.statusCode)
    }

    fun getUserPermissions(userId: String, snippetId: UUID): ResponseEntity<PermissionDTO> {
        val completeUrl = "$url/${snippetId}/users/$userId"
        val response = restTemp.getForEntity(completeUrl, PermissionDTO::class.java)
        val permissions = response.body
        return ResponseEntity(permissions, response.statusCode)
    }
}
