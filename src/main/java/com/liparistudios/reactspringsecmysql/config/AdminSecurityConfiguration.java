package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.service.SystemUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AdminSecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        return new SystemUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers("/").permitAll();

        http.antMatcher("/admin/**")
            .authorizeRequests().anyRequest().hasAuthority("ADMIN")

            .and()
            .formLogin().loginPage("/admin/login")
            .usernameParameter("email")
            .loginProcessingUrl("/admin/login")
            .defaultSuccessUrl("/admin/home")
            .permitAll()

            .and()
            .logout().logoutUrl("/admin/logout").logoutSuccessUrl("/")
            ;

        return http.build();
    }

}
