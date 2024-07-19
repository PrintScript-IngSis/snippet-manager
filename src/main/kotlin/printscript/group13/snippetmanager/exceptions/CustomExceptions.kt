package printscript.group13.snippetmanager.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class SnippetNotFoundException : RuntimeException("Snippet not found")

@ResponseStatus(HttpStatus.FORBIDDEN)
class PermissionDeniedException : RuntimeException("User does not have permission to access this snippet")

@ResponseStatus(HttpStatus.NOT_FOUND)
class PermissionNotFoundException : RuntimeException("Permission not found")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidShareRequestException : RuntimeException("Invalid share request")

@ResponseStatus(HttpStatus.CONFLICT)
class SnippetAlreadyExistsException : RuntimeException("Snippet with the same name and content already exists")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class SnippetValidationException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT)
class PermissionAlreadyExistsException : RuntimeException("Permission already exists for this user and snippet")

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InternalServerErrorException : RuntimeException("An unexpected error occurred")

