package com.liparistudios.reactspringsecmysql.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

// @Service
public class SmsService extends ExternalService {

    @Value("${sms.address}")
    protected String address;

    @Value("${sms.auth.user}")
    protected String username;

    @Value("${sms.auth.password}")
    protected String password;

    @Value("${sms.auth.apikey}")
    protected String apiKey;

    @Value("${sms.auth.token}")
    protected String token;

    @Value("${sms.sender.number}")
    protected String senderNumber;

    public Map<String, Object> send( String number, String content ) throws UnsupportedEncodingException {


        // headers
        Map<String, String> headers = new HashMap<String, String>(){{
            put("Authorization", "Basic "+ token);
            put("Content-Type", "application/x-www-form-urlencoded");
            // put("Content-Type", "application/json");
            put("cache-control", "no-cache");
        }};

        // payload
        /*
        Map<String, Object> payload = new HashMap<String, Object>(){{
            put("to",       "39"+ number);
            put("content",  content);
            put("from",     "39"+ from);
        }};
        */
        String payload = "{\"to\":\""+ number +"\",\"from\":\""+ senderNumber +"\",\"content\":\""+ content +"\"}";

        return this.remoteServerConnect(
                setUserDataConnectionHeaders( headers ),
                HttpMethod.POST,
                address,
                payload
        );

    }


}
