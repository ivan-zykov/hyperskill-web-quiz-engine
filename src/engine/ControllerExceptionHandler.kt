package engine

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

typealias ErrorBody = Map<String, String>

@ControllerAdvice
@Suppress("unused")
class ControllerExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<in Any>? {
        val body = makeErrorBodyFor(ex)

        return ResponseEntity(body, headers, HttpStatus.BAD_REQUEST)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<in Any>? {
        val body = makeErrorBodyFor(ex)

        return ResponseEntity(body, headers, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorBody> {
        val body = makeErrorBodyFor(ex)

        return ResponseEntity<ErrorBody>(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(QuizNotFoundException::class)
    fun handleQuizNotFound(exception: QuizNotFoundException): ResponseEntity<ErrorBody> {
        val body = makeErrorBodyFor(exception)

        return ResponseEntity<ErrorBody>(body, HttpStatus.NOT_FOUND)
    }

    private fun makeErrorBodyFor(ex: Exception): ErrorBody =
        mapOf("error" to (ex.message ?: ""))
}