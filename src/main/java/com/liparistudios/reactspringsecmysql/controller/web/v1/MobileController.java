package com.liparistudios.reactspringsecmysql.controller.web.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.liparistudios.reactspringsecmysql.businessLogic.SessionHandlerForControllers;
import com.liparistudios.reactspringsecmysql.model.Session;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import com.liparistudios.reactspringsecmysql.service.SessionService;
import com.liparistudios.reactspringsecmysql.service.external.social.GoogleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;



import java.time.LocalDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class MobileController {

    @Autowired
    private GoogleService googleService;

    /**
     * Accesso alla sessione, avviene quando viene scansionato il QRCode
     *
     * controlla se la sessione esiste, se è in live, se è scaduta
     * o se è chiusa.
     * Se la sessione esiste non è in live, non è chiusa, e
     *
     * @param code
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/session/access/{sessionCode}")
    public ModelAndView accessByVirginSession(
        @PathVariable(name = "sessionCode", required = true, value = "c1051") String code,
        @RequestParam(name = "auth", required = false, defaultValue = "invalid-01234567") String pin,
        @RequestParam(name = "platform", required = true) Long platformID,
        HttpServletRequest request,
        HttpServletResponse response
    ) {


        SessionHandlerForControllers sessionHandler = new SessionHandlerForControllers();
        Map<String, Object> pageVars = sessionHandler.accessSession( request, response, code, pin, platformID );
        ModelAndView page = new ModelAndView("mobile/build/index");
        page.addAllObjects(pageVars);
        return page;


    }


    @GetMapping("/oauth2/preauthorize")
    public ModelAndView preAuth (
        @RequestParam(name = "state", required = true) String state,
        @RequestParam(name = "code", required = true) String code,
        @RequestParam(name = "scope", required = true) String scope,
        HttpServletRequest request,
        HttpServletResponse response
    ) {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> pageVars = new HashMap<String, Object>();
        try {

            String baseUrl = request.getScheme() +"://"+ request.getHeader("Host");

            // get the profile data
            Map<String, Object> getGoogleAccessToken = googleService.exchangeCode( code, baseUrl );
            Map<String, Object> googleProfileData = googleService.getProfileData( (String) getGoogleAccessToken.get("access_token") );

            System.out.println("Dati Google AccessToken");
            System.out.println( getGoogleAccessToken );

            System.out.println("Dati Google Profile");
            System.out.println( googleProfileData );

            googleProfileData.put("access_token", (String) getGoogleAccessToken.get("access_token"));
            googleProfileData.put("id_token", (String) getGoogleAccessToken.get("id_token"));


            // da Map a json
            // String googleProfileJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( googleProfileData );
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String json = objectMapper.writeValueAsString( googleProfileData );
            String pageVarsContent = Base64.getEncoder().encodeToString(json.getBytes());



            pageVars.put("profile", pageVarsContent);
            pageVars.put("session", state);

        }
        catch (Exception e) {
            e.printStackTrace();

            pageVars.put("error",
                new HashMap<String, Object>(){{
                    put("code", 602);
                    put("title", "Errore in PreAuth");
                    put("message", e.getMessage());
                    put("stackTrace", e.getStackTrace());
                }}
            );
        }

        ModelAndView page = new ModelAndView("mobile-auth/build/index");
        page.addAllObjects(pageVars);
        return page;


    }



    /*
    @GetMapping("/oath2/gobackwithprofiledata")
    public ModelAndView comeBackFromAuth(
        @RequestParam(name = "state", required = true) String state,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        // TODO
    }
     */


}
