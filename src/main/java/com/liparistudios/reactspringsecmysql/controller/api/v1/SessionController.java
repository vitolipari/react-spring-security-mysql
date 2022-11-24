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


	@ResponseBody
    @PostMapping("/handshake")
    public Map<String, Object> sessionHandShake (
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody String certPEM
    ) {

        System.out.println("handshake");
        System.out.println( certPEM );

        return new HashMap<String, Object>(){{ put("status", "SUCCESS"); }};

    }



}
