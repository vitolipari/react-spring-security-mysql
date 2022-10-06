package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.service.SystemUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class AdminSecurityConfiguration {

    @Bean
    public UserDetailsService adminDetailsService() {
        return new SystemUserDetailsService();
    }

    @Bean
    public PasswordEncoder adminPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers("/").permitAll();

        http
            .antMatcher("/admin/**")

            .cors()
            .and()
            .csrf( csrf -> csrf.disable() )

                .authorizeRequests( auth -> {

                    auth
//                        .antMatchers("/**/admin/*.png").permitAll()
//                        .antMatchers("/**/admin/*.jpg").permitAll()
//                        .antMatchers("/**/admin/*.ico").permitAll()
//                        .antMatchers("/**/admin/*.json").permitAll()
//                        .antMatchers("/**/admin/static/js/*.js").permitAll()
//                        .antMatchers("/**/admin/static/css/*.css").permitAll()
//                        .antMatchers("/**/admin/static/**/*.map").permitAll()
//                        .antMatchers("/**/admin/static/media/*").permitAll()

                        .anyRequest().hasAuthority("ADMIN")
                    ;

                })
//            .authorizeRequests().anyRequest().hasAuthority("ADMIN")

//            .and()
            .formLogin()
                .loginPage("/public/sign-in")            // path da usare: /admin/sign-in
                .usernameParameter("email")
                .loginProcessingUrl("/sign-in")   // path da usare: /admin/sign-in
                .defaultSuccessUrl("/")
                .permitAll()

            .and()
            .logout()
                .logoutUrl("/sign-out")
                .logoutSuccessUrl("/")

            // Sessione jwt
            .and()
            .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
//            .headers( header -> header.frameOptions().sameOrigin().httpStrictTransportSecurity().disable() )
            .headers( header -> header.defaultsDisabled().cacheControl() )
            // .httpBasic( Customizer.withDefaults() )
        ;

        return http.build();
    }

}
