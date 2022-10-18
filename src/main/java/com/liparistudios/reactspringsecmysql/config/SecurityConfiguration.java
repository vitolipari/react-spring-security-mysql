package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.service.CustomerService;
import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import com.liparistudios.reactspringsecmysql.service.SystemUserDetailsService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfiguration {


//    private RsaKeyProperties rsaKeys;

    // @Autowired
    // private AuthEntryPointJwt unauthorizedHandler;


    private final CustomerServiceImplementation customerService;


    public SecurityConfiguration( CustomerServiceImplementation service/*, RsaKeyProperties rsaKeyProperties*/ ) {
        this.customerService = service;
//        this.rsaKeys = rsaKeyProperties;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        return (
            http

                // CORS
//                .cors( cors -> cors.disable() )
                .cors()

                // CSRF disabilitato
                .and()
                .csrf( csrf -> csrf.disable() )

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
//                        .antMatchers("/api/v1/customer").permitAll()         // registrazione di un nuovo customer
//                        .antMatchers("/api/v1/permissions").permitAll()              // lista di tutti i permessi
                        .antMatchers("/api/v1/customer/signup").permitAll()         // registrazione di un nuovo customer
                        .antMatchers("/api/v1/role").permitAll()                    // lista di tutti i ruoli

                        // public routes
                        .antMatchers("/public").permitAll()
                        .antMatchers("/public/**").permitAll()

                        .antMatchers("/h2-console").permitAll()
                        .antMatchers("/h2-console/").permitAll()
                        .antMatchers("/h2-console/*").permitAll()
                        .antMatchers("/h2-console/**").permitAll()

                        // FrontEnd
                        .antMatchers("/").permitAll()
                        .antMatchers("/**/*.png").permitAll()
                        .antMatchers("/**/*.jpg").permitAll()
                        .antMatchers("/**/*.ico").permitAll()
                        .antMatchers("/*.ico").permitAll()
                        .antMatchers("/*.json").permitAll()
                        .antMatchers("/**/*.json").permitAll()
                        .antMatchers("/**/static/js/*.js").permitAll()
                        .antMatchers("/**/static/css/*.css").permitAll()
                        .antMatchers("/**/static/**/*.map").permitAll()
                        .antMatchers("/**/static/media/*").permitAll()


                        // rotte private
                        .antMatchers("/admin").hasAuthority("ADMIN")
                        .antMatchers("/admin/**").hasAuthority("ADMIN")

                        // tutto il resto
                        .anyRequest().authenticated()

                )

                .formLogin()
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .loginPage("/public/sign-in")       // url di redirect in caso non si sia autenticati
                    .loginProcessingUrl("/public/login")    // endpoint di chiamata per il login
                .permitAll()
                    .successHandler( authenticationSuccessHandler() )
                    .defaultSuccessUrl("/")
                    .failureHandler( authenticationFailureHandler() )
                    .failureUrl("/public/sign-in?error=true")
                .permitAll()


                .and()
                .logout()
                    .logoutUrl("/sign-out")
                    .logoutSuccessUrl("/")
                    .permitAll()

                // Sessione jwt
                .and()
                // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                // .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )

                //.userDetailsService( systemUserDetailsService )
                .userDetailsService( customerService )



                // Gestione eccezioni ( non fa il redirect al login url )
                 .exceptionHandling( ex ->
                     ex
                         .accessDeniedHandler( accessDeniedHandler() )
                 )
                    // .authenticationEntryPoint( unauthorizedHandler )


                    //.headers( header -> header.defaultsDisabled().cacheControl() )

                .headers(headers -> headers.frameOptions().sameOrigin())

                //.and()
                .httpBasic(withDefaults())
                .build()

        );


    }


//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint() {
//        return new RestAuthenticationEntryPoint();
//    }

/*
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.getRsaPublicKey() ).build();
    }


    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder( rsaKeys.getRsaPublicKey() ).privateKey( rsaKeys.getRsaPrivateKey() ).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>( new JWKSet( jwk ) );
        return new NimbusJwtEncoder( jwks );
    }
*/

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AppAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AppAuthenticationSuccessHandler();
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AppAccessDeniedHandler();
    }

/*
    @Bean
    public UserDetailsService userDetailsService() {
        return new SystemUserDetailsService();
    }
    */




}
