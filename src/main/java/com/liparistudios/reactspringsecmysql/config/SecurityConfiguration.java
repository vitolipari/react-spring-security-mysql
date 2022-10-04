package com.liparistudios.reactspringsecmysql.config;

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
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity( prePostEnabled = true ) // già definito in AdminSecurityConfiguration
public class SecurityConfiguration {

    @Autowired
    private RsaKeyProperties rsaKeys;

    // @Autowired
    // private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return new SystemUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

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
                        .antMatchers("/api/v1/customer/signup").permitAll()         // registrazione di un nuovo customer
                        .antMatchers("/api/v1/customer/signup2").permitAll()         // registrazione di un nuovo customer
                        .antMatchers("/api/v1/permission").permitAll()              // lista di tutti i permessi
                        .antMatchers("/api/v1/role").permitAll()                    // lista di tutti i ruoli

                        .antMatchers("/public").permitAll()
                        .antMatchers("/public/**").permitAll()
                        .antMatchers("/").permitAll()
//                        .antMatchers("/**").permitAll()
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
                        .anyRequest().authenticated()

                )


                    /*
                    .antMatcher("/admin/**")
                    .authorizeRequests()
//                    .anyRequest()
//                    .hasAuthority( "ADMIN ")

                    .and()

                    .formLogin()
                    .loginPage("/free/signin")
                    .usernameParameter("email")
                    .loginProcessingUrl("/public/sign-in")
                    .defaultSuccessUrl("/admin/")
                    .permitAll()

                    .and()

                    .logout()
                    .logoutUrl("/admin/sign-out")
                    .logoutSuccessUrl("/admin/sign-out")

                    */

                // Gestione eccezioni
                // .exceptionHandling()
                // .authenticationEntryPoint( unauthorizedHandler )

                // Sessione
//                .and()

                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)


                .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
                // .sessionCreationPolicy( SessionCreationPolicy.STATELESS )

                /*
                .headers( header ->
                    header
                        .frameOptions()
                        .sameOrigin().httpStrictTransportSecurity().disable()
                )
                */
                .headers( header -> header.defaultsDisabled().cacheControl() )

                //.and()
                // .httpBasic( Customizer.withDefaults() )
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
}
