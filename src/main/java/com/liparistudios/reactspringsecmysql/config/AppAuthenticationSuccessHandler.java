package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private RoleService roleService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    )
            throws IOException {



        System.out.println("onAuthenticationSuccess");
        System.out.println(authentication);
        System.out.println(authentication.isAuthenticated());
        System.out.println(authentication.getDetails());
        System.out.println(authentication.getPrincipal());
        System.out.println(authentication.getCredentials());


        handle(request, response, authentication);
        // clearAuthenticationAttributes(request);

        System.out.println("request");
        System.out.println(request.toString());
        System.out.println(request.getSession());
        System.out.println(request.getAuthType());

        System.out.println("response");
        System.out.println(response.toString());


    }

    protected String determineTargetUrl(final Authentication authentication) {


        System.out.println("determineTargetUrl");
        System.out.println(authentication);
        System.out.println(authentication.toString());
        System.out.println(authentication.getCredentials());
        System.out.println(authentication.getDetails());
        System.out.println(authentication.getPrincipal());

        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();


        List<String> allRolesName =
            roleService
                .findAll()
                .stream()
                .map( role -> role.getName() )
                .collect(Collectors.toList())
        ;

        String roles =
            authorities
                .stream()

                // se le autorities fanno parte di un ruolo
                .filter( auth ->
                    allRolesName
                        .stream()
                        .anyMatch( roleName -> roleName.equals( auth.getAuthority() ) )
                )

                .map( auth -> auth.getAuthority() )
                .collect( Collectors.joining( "," ) )
        ;

        if( roles != null && !roles.equals("") ) {
            return roles;
        }

        throw new IllegalStateException();
    }

    protected void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {


        System.out.println("handle");
        System.out.println(authentication);
        System.out.println("authentication");


        String roles = determineTargetUrl(authentication);

        if (response.isCommitted()) {

            return;
        }

        redirectStrategy.sendRedirect(request, response, "/");
    }


    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

}
