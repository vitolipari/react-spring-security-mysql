package com.liparistudios.reactspringsecmysql.config;

import com.liparistudios.reactspringsecmysql.model.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDate;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour

    @Value("${jwt.key}")
    private String SECRET_KEY;

    public String generateAccessToken(Customer customer) {
        return Jwts
            .builder()
            .setSubject(String.format("%s,%s", customer.getId(), customer.getEmail()))
            .setIssuer("CodeJava")
            .setIssuedAt( new Date() )
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact()
        ;
    }

}
