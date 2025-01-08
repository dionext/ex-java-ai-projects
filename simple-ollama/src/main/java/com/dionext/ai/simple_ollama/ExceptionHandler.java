package com.dionext.ai.simple_ollama;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

@Slf4j
@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler()
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, HttpServletRequest request) {
        return processException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<Object> processException(Exception ex, HttpServletRequest request, HttpStatusCode status) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + "; charset=utf-8");
        try {
            String contextPath = "";
            if (request != null) {
                contextPath = "Error processing context path: " + request.getRequestURI() + " ";
            }
            String errorMessage = ex.toString() + contextPath;
            String message = status.toString();
            //log.error(errorMessage);
            return new ResponseEntity<>(makeSimpleErrorPageBody("Internal Server Error", ex.toString(),
                    printStackTrace(ex), ex.toString(), printStackTrace(ex)), responseHeaders, status);
        } catch (Exception ex1) {
            if (status == HttpStatus.NOT_FOUND)
                return new ResponseEntity<>(makeSimpleErrorPageBody("Page or resource not found", ex.toString(),
                        printStackTrace(ex), ex1.toString(), printStackTrace(ex1)), responseHeaders, status);
            else
                return new ResponseEntity<>(makeSimpleErrorPageBody("Internal Server Error", ex.toString(),
                        printStackTrace(ex), ex1.toString(), printStackTrace(ex1)), responseHeaders, status);
        }
    }

    private String printStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    protected String makeSimpleErrorPageBody(String message, String errorMessage, String stackTrace, String errorMessage1, String stackTrace1) {
        return MessageFormat.format("""
                        <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
                            <head>
                                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                                <meta charset="utf-8"/>
                                <meta http-equiv="Content-Language" content="en"/>
                                <title>Error</title>
                            </head>
                            <body>
                            <h3 align="center">{0}</h3>
                            <pre> {1}  </pre>
                            <pre> {2}  </pre>
                            <pre> {3}  </pre>
                            <pre> {4}  </pre>
                            </body>
                        </html>
                        """,
                message, errorMessage, stackTrace, errorMessage1, stackTrace1);
    }

}
