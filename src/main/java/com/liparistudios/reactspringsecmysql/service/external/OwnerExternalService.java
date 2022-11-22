package com.liparistudios.reactspringsecmysql.service.external;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Data
@Service
public class OwnerExternalService extends ExternalService {

/*
    public OwnerExternalService(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
        //TODO Auto-generated constructor stub
    }
* /

    @Value("${platform}")
    protected String platform;

    @Value("${platform-key}")
    protected String platformKey;

    private String JWT_HEADER = "{\"alg\":\"HS256\"}";
    private String JWT_PAYLOAD = "{\"platform\":\""+ platform +"\"}";

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    private String hmacSha256(String data, String secret) {
        try {


            System.out.println("generazione token");
            System.out.println("data");
            System.out.println( data );
            System.out.println( secret );

    
            //MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);//digest.digest(secret.getBytes(StandardCharsets.UTF_8));
    
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);
    
            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    
            return encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            return null;
        }
    }
    */


    /**
     * Fornisce l'API-KEY per accedere ai servizi esterni, ma proprietari
     * 
     * TODO
     * 
     *
     * @return
     * @throws UnsupportedEncodingException
     * /
    private String getPlatformToken() throws UnsupportedEncodingException {

        System.out.println("preparazione token di piattaforma");

        // String jwtHeader = Base64.getEncoder().withoutPadding().encodeToString( JWT_HEADER.getBytes() );
        String jwtHeader = Base64.getEncoder().withoutPadding().encodeToString( "{\"alg\":\"HS256\"}".getBytes() );
        String jwtPayload = Base64.getEncoder().withoutPadding().encodeToString( ("{\"platform\":\""+ platform +"\"}").getBytes() );
        // String jwtPayload = Base64.getEncoder().withoutPadding().encodeToString( JWT_PAYLOAD.getBytes() ).getBytes();

        // String jwtHeader = Base64.getEncoder().withoutPadding().encodeToString( "{\"alg\":\"HS256\"}".getBytes() );
        // String jwtPayload = Base64.getEncoder().withoutPadding().encodeToString( ("{\"platform\":\""+ platform +"\"}").getBytes() );


        String signature = hmacSha256(jwtHeader + "." + jwtPayload, platformKey);
        String jwtToken = jwtHeader + "." + jwtPayload + "." + signature;

        System.out.println("\n\ntoken");
        System.out.println("esempio jwt di {platform: mobileagent_portal-logs} con chiave in chiaro");
        System.out.println("eyJhbGciOiJIUzI1NiJ9.eyJwbGF0Zm9ybSI6Im1vYmlsZWFnZW50X3BvcnRhbC1sb2dzIn0.o8LBsN0cFLLWOIomKn8_M_-jcDV3G2W7Fda56WqXcWo");
        System.out.println("token generato");
        System.out.println( jwtToken );

        System.out.println("controllo header");
        System.out.println( JWT_HEADER );
        System.out.println( jwtHeader );
        System.out.println("controllo payload");
        System.out.println( JWT_PAYLOAD );
        System.out.println( jwtPayload );
        System.out.println( platform );

        System.out.println("\ncon chiave in base64");
        System.out.println("eyJhbGciOiJIUzI1NiJ9.eyJwbGF0Zm9ybSI6Im1vYmlsZWFnZW50X3BvcnRhbC1sb2dzIn0.SE98A5cJXSv0RsDsWXqrIMiX21W3wYyQN_2aqZXUsVg\n");

        return "Bearer "+ jwtToken;
    }
    */


    /**
     * Imposta gli headers, inserendo il token
     * per l'autenticazione delle richiesta
     *
     * @return
     * @throws UnsupportedEncodingException
     * /
    public HttpHeaders setUserDataConnectionHeaders() throws UnsupportedEncodingException {
        return setUserDataConnectionHeaders( new HashMap<String, String>(){{ put("Authorization", getPlatformToken()); }} );
    }
    */

    //@Override
    public HttpHeaders setUserDataConnectionHeaders( Map headers ) throws UnsupportedEncodingException {
        System.out.println("set headers");
        if( headers.get("Authorization") == null ) {
            // headers.put("Authorization", getPlatformToken()); // TODO
        }
        return super.setUserDataConnectionHeaders( headers );
    }



    /**
     * TODO scrivere la documentazione
     *
     * @param externalHTTPResponse
     * @return
     * @throws Exception
     * /
    private Map<String, Object> internalErrorHandler( ResponseEntity<String> externalHTTPResponse ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        Map<String, Object> externalServiceResponseDataMap = mapper.readValue(
            StringEscapeUtils.unescapeHtml4( externalHTTPResponse.getBody() )
            , Map.class
        );
        /*
          {
              "status": "error",
              "msg": ["messaggi", "di", "errore", "o", "stackTrace"],
              "error": "Errore della richiesta"
          }
        * /
        if( externalServiceResponseDataMap.get("error") != null ) {
            if( externalServiceResponseDataMap.get("status") != null && ((String) externalServiceResponseDataMap.get("status")).toLowerCase().indexOf("err") != -1 ) {
                    Map<String, Object> errObj = new HashMap<String, Object>();
                    errObj.put("title", "owner external service Error - "+ externalServiceResponseDataMap.get("error"));
                    errObj.put("code", 100);
                    errObj.put("msg", externalServiceResponseDataMap.get("msg"));
                    externalServiceResponseDataMap.put("error", errObj);
                    return externalServiceResponseDataMap;
            }
        }
        return null;
    }


    /**
     * Controllo generale degli errori possibili ( HTTP, Interni, ecc... )
     * della richiesta al servizio esterno
     *
     * Ritorna o la response icome Map
     * o una Map con gli errori interni
     *
     * @param result
     * @return
     * @throws Exception
     * /
    //@Override
    public Map<String, Object> checkExternalServiceError( ResponseEntity<String> result ) throws Exception {

        /*
        Object HTTPErrorMap = HTTPErrorHandler( result );
        if( HTTPErrorMap != null ) return null;
        * /
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> userDataResponseMap = mapper.readValue( StringEscapeUtils.unescapeHtml4(result.getBody()), Map.class );

        // controllo ERRORE interno alla response
        System.out.println("OwnerExternalService.checkExternalServiceError - controllo errori interni");
        Map<String, Object> internalErrorMap = internalErrorHandler( result );
        System.out.println("OwnerExternalService.checkExternalServiceError - controllo errori interni - result:");
        /*
        {status=error, msg=[email not exist], error={msg=[email not exist], code=100, title=user-data server Error - not exist user}, platform=ncc-core, payload={platform=ncc-core}, headers={Content-Type=application/json;charset=UTF-8, Token=eyJhbGciOiJIUzI1NiJ9.eyJwbGF0Zm9ybSI6InZpYmVzLWNvcmUifQ.C3twyN8IaWI6b4sxEzqkyVtln70pmCKZtrUpO5Ku264, User-Agent=Java/1.8.0_222-heroku, Accept=text/plain, application/json, application/*+json, * / *, Host=liparistudios-user-data.hostinggratis.it, Connection=keep-alive}}
         * /
        System.out.println(internalErrorMap);

        if( internalErrorMap != null ) return internalErrorMap;
        return userDataResponseMap;
    }
*/


}
