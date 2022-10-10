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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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


/*


// TODO da mergiare con SecurityConfiguration
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
// @EnableGlobalMethodSecurity( prePostEnabled = true )  // già definito in AdminSecurityConfiguration
public class AdminSecurityConfiguration {

//    @Autowired
    private RsaKeyProperties rsaKeys;

    public AdminSecurityConfiguration( RsaKeyProperties rsaKeys ) {
        this.rsaKeys = rsaKeys;
    }

    @Bean
    public UserDetailsService adminDetailsService() {
        return new SystemUserDetailsService();
    }

//    @Bean
//    public PasswordEncoder adminPasswordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

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
//                        .antMatchers("/** /admin/*.png").permitAll()
//                        .antMatchers("/** /admin/*.jpg").permitAll()
//                        .antMatchers("/** /admin/*.ico").permitAll()
//                        .antMatchers("/** /admin/*.json").permitAll()
//                        .antMatchers("/** /admin/static/js/*.js").permitAll()
//                        .antMatchers("/** /admin/static/css/*.css").permitAll()
//                        .antMatchers("/** /admin/static/** /*.map").permitAll()
//                        .antMatchers("/** /admin/static/media/*").permitAll()

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
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .sessionManagement( session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
//            .headers( header -> header.frameOptions().sameOrigin().httpStrictTransportSecurity().disable() )
            .headers( header -> header.defaultsDisabled().cacheControl() )
            // .httpBasic( Customizer.withDefaults() )
        ;

        return http.build();
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
*/