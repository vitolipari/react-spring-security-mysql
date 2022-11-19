package com.liparistudios.reactspringsecmysql.controller.api.v1;

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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public Map<String, Object> openNewSession(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "platformID", required = true) Long platformId ) {
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
    @GetMapping("/access/{sessionCode}")
    public ModelAndView accessByVirginSession(
            @PathVariable(name = "sessionCode", required = true, value = "c1051") String code,
            @RequestParam(name = "auth", required = false, defaultValue = "invalid-01234567") String pin,
            HttpServletRequest request,
            HttpServletResponse response
    ) {


        SessionHandlerForControllers sessionHandler = new SessionHandlerForControllers();
        Map<String, Object> pageVars = sessionHandler.accessSession( request, response, code, pin );
        ModelAndView page = new ModelAndView("mobile/build/index");
        page.addAllObjects(pageVars);
        return page;


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
