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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfiguration {


    private RsaKeyProperties rsaKeys;

    // @Autowired
    // private AuthEntryPointJwt unauthorizedHandler;


    //private final SystemUserDetailsService systemUserDetailsService;

    private CustomerServiceImplementation customerService;


    public SecurityConfiguration( /*SystemUserDetailsService sudService,*/ RsaKeyProperties rsaKeyProperties, CustomerServiceImplementation service ) {
        this.customerService = service;
        this.rsaKeys = rsaKeyProperties;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        return (
            http

                // CORS
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
//                        .antMatchers("/admin").hasAuthority("ADMIN")
//                        .antMatchers("/admin/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated()

                )

                .formLogin()
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .loginPage("/public/sign-in")       // url di redirect in caso non si sia autenticati
                    .loginProcessingUrl("/public/login")    // endpoint di chiamata per il login
                    .successHandler(new AppAuthenticationSuccessHandler())
                    .defaultSuccessUrl("/")
                    .failureHandler( new AppAuthenticationFailureHandler() )
                    .failureUrl("/public/sign-in?error=true")
                    .permitAll()


                .and()
                .logout()
                    .logoutUrl("/sign-out")
                    .logoutSuccessUrl("/")
                    .permitAll()

                // Sessione jwt
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )

                //.userDetailsService( systemUserDetailsService )
                .userDetailsService( customerService )

                // Gestione eccezioni ( non fa il redirect al login url )
//                 .exceptionHandling( ex ->
//                     ex
//                         .authenticationEntryPoint( new BearerTokenAuthenticationEntryPoint() )
//                         .accessDeniedHandler( new BearerTokenAccessDeniedHandler() )
//                 )
                    // .authenticationEntryPoint( unauthorizedHandler )


                    .headers( header -> header.defaultsDisabled().cacheControl() )

                //.and()
                .httpBasic( Customizer.withDefaults() )
                .build()

        );


    }


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


    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AppAuthenticationFailureHandler();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new SystemUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        Map<String, PasswordEncoder> encoders = new HashMap<>(){{
            put("bcrypt", new BCryptPasswordEncoder());
            put("noop", NoOpPasswordEncoder.getInstance());
            put("SHA-512", new Sha512PasswordEncoder());
            put("sha512", new Sha512PasswordEncoder());
            put("sha256", new Sha256PasswordEncoder());
            put("SHA-256", new Sha256PasswordEncoder());

//            put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
//            put("sha256", new StandardPasswordEncoder());
            // put("ldap", new LdapShaPasswordEncoder());
            // put("MD4", new Md4PasswordEncoder());
            // put("MD5", new MessageDigestPasswordEncoder("MD5"));
            // put("pbkdf2", new Pbkdf2PasswordEncoder());
            // put("scrypt", new SCryptPasswordEncoder());
            // put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
            // put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
        }};

        /*
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(encodingId, encoders);
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new CustomPasswordEncoder());
        return delegatingPasswordEncoder;
        */

        return new DelegatingPasswordEncoder("sha256", encoders);

    }


}
