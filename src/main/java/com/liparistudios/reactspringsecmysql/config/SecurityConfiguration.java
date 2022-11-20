package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.service.CustomerServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;


 // questa guida
// https://www.codejava.net/frameworks/spring-boot/spring-security-jwt-authentication-tutorial


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SecurityConfiguration {


    @Autowired
    private AppBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private  CustomerServiceImplementation customerService;

    @Autowired
    private CustomEncoder encoder;



    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        System.out.println("AuthenticationFailureHandler");
        return new AppAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {

        System.out.println("bean authenticationSuccessHandler");

        return new AppAuthenticationSuccessHandler();
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AppAccessDeniedHandler();
    }


    @Autowired
    private CustomAuthenticationProvider authProvider;

    @Bean
    public AuthenticationManager auth(HttpSecurity http) throws Exception {

        System.out.println("auth manager");
        System.out.println(http.toString());

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        //authenticationManagerBuilder.authenticationProvider(authProvider2());
        authenticationManagerBuilder.authenticationProvider(authProvider);

        authenticationManagerBuilder.jdbcAuthentication().passwordEncoder(encoder.passwordEncoder());

        return authenticationManagerBuilder.build();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {



        http

            // CORS
            .cors( cors -> cors.disable() )

            // CSRF disabilitato
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
                    .antMatchers("/api/v1/customer/signup").permitAll()         // registrazione di un nuovo customer
                    .antMatchers("/api/v1/role").permitAll()                    // lista di tutti i ruoli

                    // public routes
                    .antMatchers("/public").permitAll()
                    .antMatchers("/public/**").permitAll()

                    .antMatchers("/api/v1/session/open/*").permitAll()
                    .antMatchers("/api/v1/session/open/*").permitAll()
                    .antMatchers("/s/*").permitAll()
                    .antMatchers("/s?*").permitAll()
                    .antMatchers("/s").permitAll()

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
                    .anyRequest()
                        .authenticated()

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
            .permitAll()


            .and()
            .logout()
                .logoutUrl("/sign-out")
                .logoutSuccessUrl("/")
                .permitAll()


            .and()
             .sessionManagement( session -> {
                 System.out.println("sessionCreation");
                 session
                     .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                     .invalidSessionUrl("/public/invalid-session")
                     .maximumSessions(1)
                     .maxSessionsPreventsLogin(true)
                 ;
             })

            .authenticationProvider(authProvider)

            // Gestione eccezioni ( non fa il redirect al login url )
             .exceptionHandling( ex ->
                 ex
                     .accessDeniedHandler( accessDeniedHandler() )
             )
            .headers( headers ->
                headers
                    .defaultsDisabled()
                    .contentTypeOptions()

                    .and()
                    .frameOptions()
                    .disable()
            )

        ;


        return http.build();




    }


}
