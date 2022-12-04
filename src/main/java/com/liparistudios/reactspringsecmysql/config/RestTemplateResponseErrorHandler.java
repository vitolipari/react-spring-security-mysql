package com.liparistudios.reactspringsecmysql.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

// NON Usata
//@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (
          httpResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {

      System.out.println("errore http <---------------------------------------");

        if (httpResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            // handle SERVER_ERROR



          } 
        else {
          if (httpResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {


            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {

              System.out.println("404 <-----------------------------");



                throw new IOException();

            }
          }
        }
        
    }
}
