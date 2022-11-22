package com.liparistudios.reactspringsecmysql.service.external;

import com.liparistudios.reactspringsecmysql.config.RestTemplateResponseErrorHandler;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Service
public class ExternalService {

    //@Autowired
    private RestTemplate restTemplate;

    HttpEntity<Map<String, Object>> request;


    /*
    @Autowired
    public ExternalService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder
          .errorHandler(new RestTemplateResponseErrorHandler())
          .build();
    }

    */


    protected HttpHeaders setUserDataConnectionHeaders( Map headers ) throws UnsupportedEncodingException {
        HttpHeaders userDataRequestHeaders = new HttpHeaders();
        headers
            .forEach( (k, v) -> {
                userDataRequestHeaders.add(k.toString(), v.toString());
            })
        ;
        return userDataRequestHeaders;
    }


    /**
     *
     * Richiesta a servizio esterno<br>
     *
     *
     * @param requestHeaders
     * @param verb
     * @param serverURL
     * @param data
     * @return 
     * @return
     * @throws UnsupportedEncodingException
     */
    public Map remoteServerConnect(
            HttpHeaders requestHeaders,
            HttpMethod verb,
            String serverURL,
            Object data     //Map<String, Object> data
    )  {



        try {
            
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());


            if( verb == HttpMethod.GET ) {

                if( data != null && data instanceof Map ) {

                    serverURL += "?"+ ((Map<?, ?>) data)
                        .entrySet()
                        .stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining("&"))
                    ;
                }

            }

            request = new HttpEntity(data, requestHeaders);

            System.out.println("CHIAMATA A ");
            System.out.println(serverURL);

            System.out.println("Headers");
            System.out.println(requestHeaders);

            System.out.println("Data");
            System.out.println(data);


            if( verb == HttpMethod.POST ) {
                
                // controllare da qui
                System.out.println("remote server connection - raw data");
                System.out.println(data);



                ResponseEntity<Map> response;
                // HttpEntity<?> postRequest;

                // shim data
                if( data instanceof Map ) {
                    for (Map.Entry<String, Object> entry : ((Map<String, Object>)data).entrySet()) {
                        if( entry.getValue() == null ) {
                            ((Map<String, Object>)data).put( entry.getKey(), "null" );
                        }
                    }

                    System.out.println("remote server connection - null filled");
                    System.out.println(data);

                    // conversione Map a MultiValueMap
                    MultiValueMap<String, Object> ddata = new LinkedMultiValueMap<String, Object>();
                    for (Map.Entry<String, Object> entry : ((Map<String, Object>)data).entrySet()) {
                        ddata.add(entry.getKey(), entry.getValue().toString());
                    }

                    System.out.println("remote server connection - multi value map");
                    System.out.println(data);


                    // HttpEntity<?> postRequest = new HttpEntity<MultiValueMap<String, Object>>(ddata, requestHeaders);
                    HttpEntity<MultiValueMap<String, Object>> postRequest = new HttpEntity<MultiValueMap<String, Object>>(ddata, requestHeaders);


                    System.out.println("remote server connection - post request ( MAP )");
                    System.out.println(postRequest);
                    System.out.println(postRequest.getBody());
                    System.out.println(postRequest.getBody().toString());
                    System.out.println(postRequest.getBody().toSingleValueMap());
                    System.out.println(postRequest.getBody().toSingleValueMap().toString());


                    response = restTemplate.exchange(serverURL, verb, postRequest, Map.class);

                    System.out.println("response");
                    System.out.println(response);
                    System.out.println(response.getBody());
                    System.out.println(response.getStatusCode());
                    System.out.println(response.getStatusCodeValue());

                    return response.getBody();

                }
                else {
                    if( data instanceof String ) {
                        String ddata = (String) data;

                        HttpEntity<String> postRequest = new HttpEntity<String>(ddata, requestHeaders);

                        System.out.println("remote server connection - post request ( STRING ) ");
                        System.out.println(postRequest);
                        System.out.println(postRequest.getBody());
                        System.out.println(postRequest.getBody().toString());

                        response = restTemplate.exchange(serverURL, verb, postRequest, Map.class);

                        System.out.println("response");
                        System.out.println(response);
                        System.out.println(response.getBody());
                        System.out.println(response.getStatusCode());
                        System.out.println(response.getStatusCodeValue());

                        return response.getBody();
                    }
                }

                // HttpEntity<?> postRequest = new HttpEntity<String>(ddata, requestHeaders);



            }

            // return checkExternalServiceError( restTemplate.exchange(serverURL, verb, request, String.class) );
            return  restTemplate.exchange(serverURL, verb, request, Map.class).getBody();
                
            
        
        } 
        catch (HttpClientErrorException e) {
            
            System.out.println("errore HttpClientErrorException");
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
            //e.printStackTrace();

            return new HashMap<String, Object>(){{
                put("error", e);
            }};

        }

            

    }


    /**
     * TODO scrivere la documentazione
     *
     * @param result
     * @return
     * @throws Exception
     * /
    public Map<String, Object> checkExternalServiceError( ResponseEntity<String> result ) throws Exception {
        Object HTTPErrorMap = HTTPErrorHandler( result );
        if( HTTPErrorMap != null ) return null;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> userDataResponseMap = mapper.readValue( StringEscapeUtils.unescapeHtml4(result.getBody()), Map.class );
        return userDataResponseMap;
    }



    /**
     *
     * Controlla gli errori HTTP in base allo stato della response
     *
     * @return
     * /
    public Map<String, Object> HTTPErrorHandler(ResponseEntity<String> externalHTTPResponse) throws Exception {
        if( externalHTTPResponse.getBody() == null ) return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        Map<String, Object> serviceResponseMap;
        // stringa attesa un json
        if( externalHTTPResponse.getHeaders().getContentType().toString().indexOf( MediaType.APPLICATION_JSON.toString() ) == 0 ) {
            serviceResponseMap = mapper.readValue(
                    StringEscapeUtils.unescapeHtml4(externalHTTPResponse.getBody())
                    , Map.class
            );
        }

        if( externalHTTPResponse.getHeaders().getContentType().toString().indexOf( MediaType.TEXT_PLAIN.toString() ) == 0 ) {
            // TODO
        }
        if( externalHTTPResponse.getHeaders().getContentType().toString().indexOf( MediaType.TEXT_HTML.toString() ) == 0 ) {
            HttpStatus status = externalHTTPResponse.getStatusCode();
            if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                // TODO da valutare
                throwHttpError( externalHTTPResponse );
            }
            if (status == HttpStatus.NOT_FOUND)                         throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.BAD_GATEWAY)                       throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)          throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.BAD_REQUEST) {
                // TODO da valutare
                throwHttpError( externalHTTPResponse );
            }
            if (status == HttpStatus.FORBIDDEN) {
                // TODO da valutare
                throwHttpError( externalHTTPResponse );
            }
            if (status == HttpStatus.GATEWAY_TIMEOUT)                   throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.INSUFFICIENT_STORAGE)              throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.LENGTH_REQUIRED)                   throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.LOCKED)                            throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.URI_TOO_LONG)                      throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.METHOD_NOT_ALLOWED) {
                // TODO da valutare
                throwHttpError( externalHTTPResponse );
            }
            if (status == HttpStatus.MOVED_PERMANENTLY)                 throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.TOO_MANY_REQUESTS)                 throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.UNSUPPORTED_MEDIA_TYPE) {
                // TODO da valutare
                throwHttpError( externalHTTPResponse );
            }
            if (status == HttpStatus.NOT_IMPLEMENTED)                   throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.PAYLOAD_TOO_LARGE)                 throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.PAYMENT_REQUIRED)                  throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.PERMANENT_REDIRECT)                throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.TEMPORARY_REDIRECT)                throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.NETWORK_AUTHENTICATION_REQUIRED)   throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.NO_CONTENT)                        throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.NOT_ACCEPTABLE)                    throwHttpError( externalHTTPResponse );
            if (status == HttpStatus.HTTP_VERSION_NOT_SUPPORTED)        throwHttpError( externalHTTPResponse );
        }
        return null;
    }



    private void throwHttpError(ResponseEntity<String> externalHTTPResponse ) throws Exception {
        throw new Exception(String.valueOf(new HashMap<String, Object>(){{
            put("code", externalHTTPResponse.getStatusCodeValue());
            put("message", externalHTTPResponse.getStatusCode().getReasonPhrase());
            put("data", externalHTTPResponse.getBody() );
        }}));
    }

    */

}
