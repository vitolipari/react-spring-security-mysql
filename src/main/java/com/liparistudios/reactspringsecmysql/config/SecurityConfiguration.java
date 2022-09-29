package com.liparistudios.reactspringsecmysql.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfiguration {

    @Autowired
    private RsaKeyProperties rsaKeyProperties;

    // @Autowired
    // private AuthEntryPointJwt unauthorizedHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        return (
            http

                // CORS
                .cors()

                // CSRF disabilitato
                .and()
                .csrf( csfr -> csfr.disable() )
                // .csrf().disable()

                /*
                .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                    By default the CookieServerCsrfTokenRepository will write to a cookie named XSRF-TOKEN and read it from a header named X-XSRF-TOKEN or the HTTP parameter _csrf.
                    These defaults come from AngularJS
                    The sample explicitly sets cookieHttpOnly=false. This is necessary to allow JavaScript (i.e. AngularJS) to read it.
                    If you do not need the ability to read the cookie with JavaScript directly, it is recommended to omit cookieHttpOnly=false
                    (by using new CookieServerCsrfTokenRepository() instead) to improve security.
                 */


                .authorizeRequests( auth ->
                    auth

                        // rotte libere
                        .antMatchers("").permitAll()
                        .antMatchers("").permitAll()

                        // rotte private
                        .anyRequest().authenticated()

                )


                // Gestione eccezioni
                // .exceptionHandling()
                // .authenticationEntryPoint( unauthorizedHandler )

                // Sessione
                // .and()

                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)


                .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
                // .sessionCreationPolicy( SessionCreationPolicy.STATELESS )


                //.and()
                .httpBasic( Customizer.withDefaults() )
                .build()

        );


    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.getRsaPublicKey() ).build();
    }


}
