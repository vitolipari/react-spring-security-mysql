package com.liparistudios.reactspringsecmysql.controller.api.v1;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Session;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import com.liparistudios.reactspringsecmysql.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.spi.LocaleNameProvider;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {

    @Autowired private SessionService sessionService;
    @Autowired private CustomerServiceImplementation customerService;

    @ResponseBody
    @GetMapping("/open")
    public Map<String, Object> openNewSession(HttpServletRequest request, HttpServletResponse response) {
        Session newSession = sessionService.openNewSession();
        return
            new HashMap<String, Object>(){{
                put("id", newSession.getId());
                put("code", newSession.getCode());
                put("openAt", newSession.getOpen());
                put("expiresAt", newSession.getExp());
            }}
        ;
    }


    @GetMapping("/access/{sessionCode}")
    public ModelAndView accessByVirginSession(
            @PathVariable(name = "sessionCode", required = true, value = "ghost") String code,
//        @RequestParam(required = false, defaultValue = "invalid") String auth,
            HttpServletRequest request,
            HttpServletResponse response
    ) {


        Map<String, Object> pageVars = new HashMap<String, Object>(){{
            put("session",
                    new HashMap<String, Object>(){{
                        put("code", code);
                    }}
            );
        }};
        LocalDateTime now = LocalDateTime.now();
        ModelAndView page = new ModelAndView("mobile/build/index");

        try {

            // controllo sessione
            Session session = sessionService.getSessionByCode( code );
            ((Map<String, Object>) pageVars.get("session")).put("id", session.getId());


            // controllo sessione che può essere abilitata
            if( !session.isLive() ) {
                ((Map<String, Object>) pageVars.get("session")).put("live",
                        new HashMap<String, Object>(){{
                            put("since", session.getAccess());
                        }}
                );
            }

            // controllo sessione scaduta
            if( session.isExpired() ) {
                ((Map<String, Object>) pageVars.get("session")).put("expired", session.isExpired());
            }

            // controllo sessione già attiva
            if( !session.isClosed() ) {
                ((Map<String, Object>) pageVars.get("session")).put("closed", session.isClosed());
            }


            ((Map<String, Object>) pageVars.get("session")).put("access", now);

            // controllo timeToEnable
            if(now.isBefore( session.getOpen().plus(90, ChronoUnit.SECONDS) )) {
                // sessione scansionabile
                session.setAccess( now );

            }
            else {
                // sessione scaduta
                session.setClosed( now );
                ((Map<String, Object>) pageVars.get("session")).put("closed", now);


            }

            sessionService.save( session );



            page.addAllObjects(pageVars);
            return page;

        }
        catch (Exception e) {
            // codice sessione non trovato

            pageVars.put("error",
                    new HashMap<String, Object>(){{
                        put("code", 600);
                        put("title", "Codice sessione non trovato");
                        put("message", e.getMessage());
                        put("stackTrace", e.getStackTrace());
                    }}
            );
            page.addAllObjects(pageVars);
            return page;

        }

    }

    /*
    @ResponseBody
    @PutMapping({"/activate/{sessionCode}", "/enable/{sessionCode}"})
    public ResponseEntity<Session> activateSession(
        @PathVariable String code,
        @RequestBody Long customerId,
        @RequestParam(required = false, defaultValue = "invalid") String auth,
        HttpServletRequest request,
        HttpServletResponse response
    ) {


        // controllo tipo
        switch (auth) {
            case "invalid":

            break;
            case "pre-auth":

            break;
            case "done":

            break;
            default:

            break;
        }

        Session ses = sessionService.getSessionByCode( code );

        // controllo tempo di attivazione
        if( LocalDateTime.now().isBefore( ses.getOpen().plus( ses.getStartUpTime() ) ) ) {

            Customer customer = customerService.loadCustomerById( customerId );
            ses.setCustomer( customer );



        }
        else {
            // la sessione non è stata abilitata in tempo
        }




        return
            ResponseEntity
                .status(HttpStatus.OK)
                .body( ses )
            ;
    }
    */


}
