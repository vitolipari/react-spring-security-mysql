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
          httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR 
          || httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR
        );
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {

      System.out.println("errore http <---------------------------------------");

        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            // handle SERVER_ERROR


            
            System.out.println(httpResponse.getStatusCode().series());
            System.out.println(httpResponse.getStatusCode());


          } 
        else {
          if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

            // handle CLIENT_ERROR
            System.out.println(httpResponse.getStatusCode().series());
            System.out.println(httpResponse.getStatusCode());
          
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {

              System.out.println("404 <-----------------------------");

              System.out.println(httpResponse.getStatusCode().series());
              System.out.println(httpResponse.getStatusCode());  

                throw new IOException();

            }
          }
        }
        
    }
}
