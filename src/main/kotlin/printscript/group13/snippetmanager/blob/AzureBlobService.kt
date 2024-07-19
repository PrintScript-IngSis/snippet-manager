package printscript.group13.snippetmanager.blob

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.UUID

@Service
class AzureBlobService(
    @Value("\${snippet.blob.url}") private val blobUrl: String,
) {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    fun create(
        content: String,
        assetId: UUID,
    ): ResponseEntity<String> {
        val url = "$blobUrl/$assetId"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(content, headers)
        return restTemplate.postForEntity(url, entity, String::class.java)
    }

    fun get(assetId: UUID): ResponseEntity<String> {
        val url = "$blobUrl/$assetId"
        return restTemplate.getForEntity(url, String::class.java)
    }

    fun update(
        content: String,
        assetId: UUID,
    ): ResponseEntity<String> {
        val responseFromDelete = delete(assetId)
        if (responseFromDelete.statusCode.is2xxSuccessful) {
            return create(content, assetId)
        }
        return responseFromDelete
    }

    fun delete(assetId: UUID): ResponseEntity<String> {
        val url = "$blobUrl/$assetId"
        return restTemplate.exchange(url, HttpMethod.DELETE, null, String::class.java)
    }
}
