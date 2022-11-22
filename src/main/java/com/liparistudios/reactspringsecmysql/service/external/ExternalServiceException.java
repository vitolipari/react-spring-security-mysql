package com.liparistudios.reactspringsecmysql.service.external;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.Charset;

public class ExternalServiceException extends RestClientResponseException {

    /**
     * Construct a new instance of with the given response data.
     *
     * @param message
     * @param statusCode      the raw status code value
     * @param statusText      the status text
     * @param responseHeaders the response headers (may be {@code null})
     * @param responseBody    the response body content (may be {@code null})
     * @param responseCharset the response body charset (may be {@code null})
     */
    public ExternalServiceException(String message, int statusCode, String statusText, HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {
        super(message, statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }
}
