package com.liparistudios.reactspringsecmysql.controller.api.v1;

import com.liparistudios.reactspringsecmysql.service.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final TokenService tokenService;

    public AuthController( TokenService tokenService ) {
        this.tokenService = tokenService;
    }

    //@PostMapping("/token")
    @GetMapping("/token")
    public String token(Authentication authentication) {
        String token = tokenService.generateToken( authentication );
        System.out.println("Token granted for user "+ authentication.getName());
        System.out.println( token );
        return token;
    }

}
