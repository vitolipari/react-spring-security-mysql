package com.liparistudios.reactspringsecmysql.config;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import java.io.IOException;

public class AppAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    public AppAuthenticationSuccessHandler() {
        super();

        System.out.println("AppAuthenticationSuccessHandler");
        setUseReferer( true );
    }

}
