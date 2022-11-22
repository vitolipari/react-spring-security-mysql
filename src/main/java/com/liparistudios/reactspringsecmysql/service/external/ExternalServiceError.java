package com.liparistudios.reactspringsecmysql.service.external;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ExternalServiceError implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        System.out.println("HasError");
//        System.out.println( response.toString() );  // es 400


        return true;
//        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // your error handling here
        System.out.println("HandlerError");
        // System.out.println( response.toString() );
        System.out.println( response.getRawStatusCode() );  // es 400
        // System.out.println( response.getStatusCode() );
        System.out.println( response.getStatusText() );
        System.out.println( response.getBody().toString() );

        // TODO
        //  throw new ExternalServiceException()

    }

}
