package engine

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
@Suppress("unused")
class ControllerExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<in Any>? {
        val body = buildResponseBody(ex)

        return ResponseEntity(body, headers, HttpStatus.BAD_REQUEST)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<in Any>? {
        val body = buildResponseBody(ex)

        return ResponseEntity(body, headers, HttpStatus.BAD_REQUEST)
    }

    private fun buildResponseBody(ex: Exception) =
        mapOf("error" to ex.message)
}