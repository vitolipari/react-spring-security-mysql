package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfiguration {


//    private RsaKeyProperties rsaKeys;

    // @Autowired
    // private AuthEntryPointJwt unauthorizedHandler;


    private final CustomerServiceImplementation customerService;

//    private PasswordEncoder encoder;
//    private CustomEncoder encoder;



/*    @Bean
    public PasswordEncoder passwordEncoder() {

        Map<String, PasswordEncoder> encoders = new HashMap<>(){{
            put("bcrypt", new BCryptPasswordEncoder());
            put("noop", NoOpPasswordEncoder.getInstance());
            put("SHA-512", new Sha512PasswordEncoder());
            put("sha512", new Sha512PasswordEncoder());
            put("sha256", new Sha256PasswordEncoder());
            put("SHA-256", new Sha256PasswordEncoder());
        }};

        *//*
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(encodingId, encoders);
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new CustomPasswordEncoder());
        return delegatingPasswordEncoder;
        *//*

        return new DelegatingPasswordEncoder("sha256", encoders);

    }*/




    public SecurityConfiguration( CustomerServiceImplementation service/*, RsaKeyProperties rsaKeyProperties*//*, CustomEncoder encoder*/ ) {
        this.customerService = service;
//        this.rsaKeys = rsaKeyProperties;
//        this.encoder = encoder;
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
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/public/error?login=true")
                    .successHandler( authenticationSuccessHandler() )
                    .failureHandler( authenticationFailureHandler() )
//                    .authenticationDetailsSource(authenticationDetailsSource)
                .permitAll()


                .and()
                .logout()
                    .logoutUrl("/sign-out")
                    .logoutSuccessUrl("/")
                    .permitAll()

                // Sessione jwt
                .and()
                 .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )

                .userDetailsService( customerService )

                .authenticationEntryPoint(authenticationEntryPoint)
                .addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class)

                .and()

                // Gestione eccezioni ( non fa il redirect al login url )
                 .exceptionHandling( ex ->
                     ex
                         .accessDeniedHandler( accessDeniedHandler() )
                 )
                    // .authenticationEntryPoint( unauthorizedHandler )

                    //.headers( header -> header.defaultsDisabled().cacheControl() )
//                .headers(headers -> headers.frameOptions().sameOrigin())
                    .headers( headers ->
                        headers
                            .defaultsDisabled()
                            .contentTypeOptions()

                            .and()
                            .frameOptions()
                            .disable()
                    )

                //.and()
//                .httpBasic(withDefaults())
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
        System.out.println("AuthenticationFailureHandler");
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
