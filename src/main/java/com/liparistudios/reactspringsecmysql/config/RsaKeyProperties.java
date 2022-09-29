package com.liparistudios.reactspringsecmysql.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties( prefix = "rsa")
public class RsaKeyProperties {

    @Value("${rsa.public-key}")
    private RSAPublicKey rsaPublicKey;

    @Value("${rsa.private-key}")
    private RSAPrivateKey rsaPrivateKey;


}
