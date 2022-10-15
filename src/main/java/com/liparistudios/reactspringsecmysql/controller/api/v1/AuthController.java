package com.liparistudios.reactspringsecmysql.controller.api.v1;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.LoginAuthPack;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import com.liparistudios.reactspringsecmysql.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final TokenService tokenService;

    public AuthController( TokenService tokenService ) {
        this.tokenService = tokenService;
    }


    @Autowired
    private CustomerServiceImplementation customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //@PostMapping("/token")
    @GetMapping("/token")
    public String token(Authentication authentication) {
        String token = tokenService.generateToken( authentication );
        System.out.println("Token granted for user "+ authentication.getName());
        System.out.println( token );
        return token;
    }


    @ResponseBody
    @PostMapping("/public/customer-login")
    public Map<String, Object> login(
            HttpServletRequest request,
            @RequestBody LoginAuthPack credentials
            // @RequestParam String email,
            // @RequestParam String password
    ) {
        System.out.println("POST al login");
        System.out.println( "AUTH type" );
        System.out.println( request.getAuthType() );
        System.out.println("principal");
        System.out.println( request.getUserPrincipal() );

        System.out.println("email arrivata");
        System.out.println( credentials.getEmail() );
        System.out.println();

        Map<String, Object> result = null;
        try {

            Customer customer =
                    customerService
                            .loadCustomerByEmail( credentials.getEmail() )
            ;

            if( customer.getPassword().equals( passwordEncoder.encode( credentials.getPassword() ) ) ) {
                // login OK
                result = new HashMap<String, Object>(){{
                    put("status", "success");
                }};
            }
            else {
                // password errata
                result = new HashMap<String, Object>(){{
                    put("status", "error");
                    put("error", new HashMap<String, Object>(){{
                        put("message", "password errata");
                        put("correct password", customer.getPassword());
                        put("search password", passwordEncoder.encode( credentials.getPassword() ));
                    }});
                }};
            }

        }
        catch (Exception e) {

            // probabilmente email non esiste

            System.out.println("errore al login");
            e.printStackTrace();
            result = new HashMap<String, Object>(){{
                put("status", "fail");
                put("error", new HashMap<String, Object>(){{
                    put("message", "Eccezione");
                    put("exception", e.getMessage());
                }});
            }};

        }
        return result;
    }

}
