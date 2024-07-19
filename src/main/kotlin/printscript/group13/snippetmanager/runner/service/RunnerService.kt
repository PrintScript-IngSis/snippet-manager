package printscript.group13.snippetmanager.runner.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import printscript.group13.snippetmanager.runner.input.FormatterInputDTO
import printscript.group13.snippetmanager.runner.input.InterpreterInputDTO
import printscript.group13.snippetmanager.runner.input.LinterInputDTO
import printscript.group13.snippetmanager.runner.output.FormatterOutput
import printscript.group13.snippetmanager.runner.output.InterpreterOutput
import printscript.group13.snippetmanager.runner.output.LinterOutput

@Service
class RunnerService(
    @Value("\${runner.url}/api/run") private val url: String,
) {
    @Autowired
    private lateinit var restTemp: RestTemplate
    private val logger = LoggerFactory.getLogger(RunnerService::class.java)

    fun runCode(input: InterpreterInputDTO): ResponseEntity<InterpreterOutput> {
        val completeUrl = "$url/interpret"
        logger.info("Running interpreter")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity(input, headers)
        val response = restTemp.postForEntity(completeUrl, requestEntity, InterpreterOutput::class.java)
        return ResponseEntity(response.body, response.statusCode)
    }

    fun lintCode(input: LinterInputDTO): LinterOutput {
        val completeUrl = "$url/lint"
        logger.info("Running linter")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity(input, headers)
        val response = restTemp.postForEntity(completeUrl, requestEntity, LinterOutput::class.java)
        return response.body!!
    }

    fun formatCode(input: FormatterInputDTO): FormatterOutput {
        val completeUrl = "$url/format"
        logger.info("Running format")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestEntity = HttpEntity(input, headers)
        val response = restTemp.postForEntity(completeUrl, requestEntity, FormatterOutput::class.java)
        return response.body!!
    }
}
