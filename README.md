

## Sicurezza

### PasswordEncoder
[ufficiale](https://www.baeldung.com/spring-security-5-password-storage)

[Esempio 1](https://www.dontpanicblog.co.uk/2022/03/14/spring-security-delegating-password-encoder/)

forse il migliore, da usare per lo SHA-256
[Esempio 2](https://springhow.com/spring-security-password-encoder/)

[Esempio 3](https://blog.marcosbarbero.com/password-encoder-migration-spring-security-5/)

[Esempio 4](https://howtodoinjava.com/spring-security/password-encoders/)



### per la configurazione jwt
#### creazione di una coppia di chiavi

generazione delle chiavi
```bash
openssl genrsa -out ./keypair.pem 2048
```

estrazione della chiave pubblica
```bash
openssl rsa -in ./keypair.pem -pubout -out ./public.pem
```

estrazione della chiave privata
```bash
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in ./keypair.pem -out ./private.pem
```

## Tech Stack
- Java 11
- Spring Boot
- JPA
- MySQL
- Spring Security
- JWT
- RSA Keys
- Thymeleaf
- React
- React Router


## Plugins

### H2
[ufficiale](https://www.baeldung.com/spring-boot-h2-database)


## Tutorials & Sitography

[buono](https://github.com/javadevjournal/javadevjournal/tree/master/spring-security/spring-security-series)


### video tutorial

#### best
[autenticazione con JPA](https://www.youtube.com/watch?v=awcCiqBO36E)
34min
[Dan Vega JWT](https://www.youtube.com/watch?v=KYNR5js2cXE)
35min

[Continuo del video](https://www.youtube.com/watch?v=UaB-0e76LdQ)

#### normal
[Dan Vega Spring Security](https://www.youtube.com/watch?v=d7ZmZFbE_qY)
[Multi Ruoli](https://www.youtube.com/watch?v=ErwPP7xLwDY)
42:40


[paginazione JPA](https://www.danvega.dev/blog/2022/05/12/spring-data-jpa-pagination/)

[Buona guida](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)

[da tenere in considerazione](https://stackoverflow.com/questions/44046154/multiple-home-pages-for-different-roles-in-spring-security)

[microservice con Spring Cloud](https://www.youtube.com/watch?v=p485kUNpPvE)

### note e appunti
#### nota 01
Nella configurazione oltre al login bisogna indicare una classe che 
sia un service e che implementi UserDetailsServer, questa classe la si può 
iniettare nel costruttore (~34:00) 
La classe che implementa l'interfaccia deve implementare il metodo loadUserByUsername
che si aspetta un return di tipo UserDetails, allora viene creata una classe che implementa
UserDetails e che ha come mebro privato la classe relativa alle utenze, 
questa viene iniettata nel costruttore, va fatta attenzione al 
getAuthorities()


Per l'autenticazione bisogna avere un model di tipo SecurityUser 
e un service che imlementa UserDetailsService 