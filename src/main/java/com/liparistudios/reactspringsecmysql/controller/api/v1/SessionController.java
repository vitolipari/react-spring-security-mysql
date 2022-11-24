package com.liparistudios.reactspringsecmysql.controller.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liparistudios.reactspringsecmysql.businessLogic.SessionHandlerForControllers;
import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Platform;
import com.liparistudios.reactspringsecmysql.model.Session;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import com.liparistudios.reactspringsecmysql.service.PlatformService;
import com.liparistudios.reactspringsecmysql.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/session")
public class SessionController {


    @Autowired private PlatformService platformService;
    @Autowired private SessionService sessionService;
    @Autowired private CustomerServiceImplementation customerService;

    /**
     * Apertura di una nuova sessione
     * @param request
     * @param response
     * @param platformId
     * @return
     */
    @ResponseBody
    @GetMapping("/open/{platformID}")
    public Map<String, Object> openNewSession(
        HttpServletRequest request,
        HttpServletResponse response,
        @PathVariable(name = "platformID", required = true) Long platformId
    ) {
        Session newSession = sessionService.openNewSession( platformId );

        return
            new HashMap<String, Object>(){{
                put("id", newSession.getId());
                put("code", newSession.getCode());
                put("openAt", newSession.getOpen());
                put("expiresAt", newSession.getExp());
                put("platform", platformService.getPlatformById( platformId ).toMap() );
            }}
        ;
    }



    // chiamata cURL
//    curl 'http://localhost:9009/api/v1/session/handshake' \
//            -X 'POST' \
//            -H 'Accept: */*' \
//            -H 'Content-Type: text/plain;charset=UTF-8' \
//            -H 'Origin: http://localhost:9009' \
//            -H 'Cookie: JSESSIONID=D9AAC210B8DEAFFEC3FCAA1AA74CC9D5' \
//            -H 'Content-Length: 1560' \
//            -H 'Accept-Language: en-GB,en;q=0.9' \
//            -H 'Host: localhost:9009' \
//            -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Safari/605.1.15' \
//            -H 'Referer: http://localhost:9009/oauth2/preauthorize?state=eyJhY2Nlc3NfdGltZSI6MTY2OTMxNTU4MzQxMCwic2Vzc2lvbiI6eyJpZCI6NiwiY29kZSI6IjVmZWNlYjY2ZmZjODZmMzhkOTUyNzg2YzZkNjk2Yzc5YzJkYmMyMzlkZDRlOTFiNDY3MjlkNzNhMjdmYjU3ZTkiLCJvcGVuIjoiMjAyMi0xMS0yNCAxOTo0NjowOSIsImFjY2VzcyI6bnVsbCwiZW5hYmxlZCI6bnVsbCwiZXhwIjoiMjAyMy0wMi0yMiAxOTo0NjowOSIsImNsb3NlZCI6bnVsbCwiY3VzdG9tZXIiOm51bGwsInN0YXR1cyI6ImFjdGl2ZSIsImV4cGlyZWQiOnRydWUsImxpdmUiOmZhbHNlLCJhY3RpdmUiOnRydWV9LCJwbGF0Zm9ybSI6eyJpZCI6MiwibmFtZSI6Ik1vYmlsZSBBZ2VudCBEaWFnbm9zdGljIFBvcnRhbCIsImV4cCI6bnVsbCwib3BlbiI6IjIwMjMtMTEtMjAiLCJsb2dvRmlsZVBhdGgiOm51bGwsImxvZ29GaWxlVXJsIjpudWxsLCJzZXNzaW9ucyI6bnVsbCwiZW5hYmxlZCI6bnVsbCwibG9nb0ZpbGVDb250ZW50IjpudWxsfSwicGluIjoiMDEyMzQ1Njc4OTAxMjM0NSJ9&code=4%2F0AfgeXvuGs4b74LMZx-b9UDUgCrNZxCEtWZXvW9rJPf8KXujhPn5pnBuzgE0EWM6ukSFhjg&scope=profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile' \
//            -H 'Accept-Encoding: gzip, deflate' \
//            -H 'Connection: keep-alive' \
//            --data-binary '"-----BEGIN CERTIFICATE-----\n                                    eyJWZXJzaW9uIjoiMyIsIlNlcmlhbCBOdW1iZXIiOiJkMjdlZmY1MWVjYzRjMmZj\nYmM1NGY2MDkwNzNmNzU2ZDQzYTZiMGVlNmMyNTZlNjc3ZWYwZjU2MzRhMDNiNzVk\nIiwiU3ViamVjdCI6eyJDb21tb24gTmFtZSI6Ik1vYmlsZSBBZ2VudCBBdXRoIFNl\ncnZlciJ9LCJJc3N1ZXIiOnsiQ291bnRyeSI6IklUIiwiQ29tbW9uIE5hbWUiOiJD\nYW5pbm9TUkwiLCJTdGF0ZSI6Ikl0YWx5IiwiTG9jYWxpdHkiOiJNYXJzYWxhIiwi\nT3JnYW5pemF0aW9uIjoiQ2FuaW5vU1JMIiwiT3JnYW5pemF0aW9uIFVuaXQiOiJk\nZXYiLCJQb3N0YWwgQ29kZSI6IjkxMDI1IiwiU3RyZWV0IEFkZHJlc3MiOiJ2aWEg\nZGVsbG8gc2JhcmNvLCA5NiJ9LCJWYWxpZGl0eSI6eyJOb3QgQmVmb3JlIjoiMjAy\nMi0xMS0yNFQxODo0NjoyNC45ODNaIiwiTm90IEFmdGVyIjoiMjAyMy0wMi0yMlQx\nODo0NjoyNC45ODNaIn0sIk5vdCBCZWZvcmUiOiIyMDIyLTExLTI0VDE4OjQ2OjI0\nLjk4NFoiLCJOb3QgQWZ0ZXIiOiIyMDIzLTAyLTIyVDE4OjQ2OjI0Ljk4NFoiLCJT\naWduYXR1cmUgQWxnb3JpdGhtIjoiRUNEU0FXaXRoU0hBMjU2IiwiUHVibGljIEtl\neSBJbmZvIjp7IkFsZ29yaXRobSI6IkVsbGlwdGljIEN1cnZlIiwiQ3VydmUiOiJz\nZWNwMjU2azEiLCJLZXkgU2l6ZSI6IjI1NiIsIlB1YmxpYyBWYWx1ZSI6IjA0YzU3\nZWI1YTIxNWI0YTFiZmUzN2Q2MDI4MGE3NDI5NjUwYTNhMzg1NTA3ZmFhNGFmNmYw\nYzYzNjljMTFiYjc0YjAzNjEwOWFhNjFhNjg3ODBkNmQ5ZDM1MTkwNTU2NTlkYzlk\nZTQ2ZGU0MGQyMjhmNTcwODU4ZDIzMTg1MDFlNmUifSwiRmluZ2VycHJpbnQiOiIx\nMjYwMzIyMmY0M2MwM2RiNzg2NmUxNDhkOGFjZDVhYzNjNDAwZGU3NTczMjExNzhi\nZWVlMjczOGNlYTgzM2EyIiwiU2lnbmF0dXJlIjoiMzA0NDAyMjA1NDhlZjQyYzY5\nZWU5YWQ0NzY0ODEwMDRhZmIyYzVjNjQwMzU3MThkOWUwN2ZmMWVkNGZiODQ0MzA3\nZThmZTBiMDIyMDc5ZjZhYTJmNWNlZjRkM2UxNmE5MGU4MzE1NTRhMjc5ODEwODE5\nZGY2ZmVmMjkxZDRmMzljODAyYTVmN2U5ZTkifQ==\n\n\n                                    -----END CERTIFICATE-----"'
    /**
     *
     * @param request
     * @param response
     * @param certPEM
     * @return
     */
	@ResponseBody
    @PostMapping("/handshake")
    public Map<String, Object> sessionHandShake (
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody String certPEM
    ) {

        System.out.println("handshake");
        System.out.println( certPEM );

        certPEM =
            certPEM
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replace("\\n", "")
                .replace("\\t", "")
                .replace(" ", "")
                .trim()
        ;

        System.out.println( certPEM );


        byte[] bytes = null;
        String certJson = new String();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> certMap = null;


        try {
            // decode base64
            bytes = Base64.getDecoder().decode( certPEM );
//            bytes = Base64.getDecoder().decode( certPEM.getBytes(StandardCharsets.UTF_8) );
            certJson = new String(bytes);

            // json to Map
            certMap = mapper.readValue(certJson, Map.class);
            //Map<String, String> map = mapper.readValue(json, new TypeReference<Map<String, String>>() {});

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println( certJson );
        System.out.println( certMap );


        // extract values
        // check validity
        // check fingerprint
        // check signature


        return new HashMap<String, Object>(){{ put("status", "SUCCESS"); }};

    }



}
