package com.liparistudios.reactspringsecmysql.service.external.social;

import com.liparistudios.reactspringsecmysql.service.external.ExternalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleService extends ExternalService {

    @Value("${google.auth.web.client_id}")
    private String clientId;

    @Value("${google.auth.web.client_secret}")
    private String clientSecret;

    @Value("${google.auth.web.token_uri}")
    private String tokenUri;

    @Value("${google.auth.web.redirect_url}")
    private String redirectUrl;

    @Value("${google.auth.web.grant_type}")
    private String grantType;

    @Value("${google.profile_uri}")
    private String profileUrl;


    public Map<String, Object> exchangeCode( String code, String domainHost ) throws Exception {

        // headers
        Map<String, String> headers = new HashMap<String, String>(){{
            // put("Content-Type", "application/x-www-form-urlencoded");
        }};

        // payload
        Map<String, Object> payload = new HashMap<String, Object>(){{
            put("code",             code);
            put("client_id",        clientId);
            put("client_secret",    clientSecret);
            put("redirect_uri",     domainHost + redirectUrl);
            put("grant_type",       grantType);
        }};

        return this.remoteServerConnect(
            setUserDataConnectionHeaders( headers ),
            HttpMethod.POST,
            tokenUri,
            payload
        );

    }


    public Map<String, Object> getProfileData( String accessToken ) throws Exception {

        // headers
        Map<String, String> headers = new HashMap<String, String>(){{
            put("Authorization", "Bearer "+ accessToken);
        }};

        return this.remoteServerConnect(
                setUserDataConnectionHeaders( headers ),
                HttpMethod.GET,
                profileUrl,
                null
        );

    }



}
