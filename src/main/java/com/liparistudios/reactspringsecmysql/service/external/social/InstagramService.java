package com.liparistudios.reactspringsecmysql.service.external.social;

import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class InstagramService  {

/*
    @Value("${instagram.clientId}")
    private String INSTAGRAM_CLIEND_ID;

    @Value("${instagram.secretKey}")
    private String INSTAGRAM_SECRET_KEY;

    @Value("${instagrma.requestTokenGrantType}")
    private String INSTAGRAM_REQUEST_TOKEN_GRANT_TYPE;

    @Value("${instagram.authorizeURL}")
    private String INSTAGRAM_API_AUTH_URL;

    @Value("${instagram.tokenRequestURL}")
    private String INSTAGRAM_TOKEN_REQUEST_URL;

    @Value("${instagram.redirectHOST}")
    private String INSTAGRAM_REDIRECT_HOST;

    @Value("${instagram.redirectURI}")
    private String INSTAGRAM_REDIRECT_URI;

    @Value("${instagram.get-profile-data}")
    private String INSTAGRAM_GET_PROFILE_DATA;


    public Map<String, Object> requestAccessToken(String requestTokenCode ) throws Exception {


        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("client_id",     INSTAGRAM_CLIEND_ID);
        parameters.put("client_secret", INSTAGRAM_SECRET_KEY);
        parameters.put("grant_type",    INSTAGRAM_REQUEST_TOKEN_GRANT_TYPE );
        parameters.put("redirect_uri",  INSTAGRAM_REDIRECT_HOST + INSTAGRAM_REDIRECT_URI);
        parameters.put("code",          requestTokenCode );


        return this.remoteServerConnect(
                headers,
                HttpMethod.POST,
                INSTAGRAM_TOKEN_REQUEST_URL,
                parameters
        );



    }

    /**
     * Richiesta a Instagram dei dati dell'utente
     *
     * Request
     * GET "https://api.instagram.com/v1/users/self?access_token=" + customer.getInstagramToken
     *
     * Response
     * {
     *     "data": {
     *         "id": "xxx",
     *         "username": "xxx",
     *         "profile_picture": "xxx",
     *         "full_name": "xxx",
     *         "bio": "xxx",
     *         "website": "xxx",
     *         "is_business": true | false,
     *         "counts": {
     *             "media": n,
     *             "follows": n,
     *             "followed_by": n
     *         }
     *     },
     *     "meta": {
     *         "code": 200
     *     }
     * }
     *
     * @param accessToken
     * @return
     * /
    public Map<String, Object> getProfileBaseData(String accessToken ) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("access_token",          accessToken );

        return this.remoteServerConnect(
                headers,
                HttpMethod.GET,
                INSTAGRAM_GET_PROFILE_DATA,
                parameters
        );
    }


*/
}
