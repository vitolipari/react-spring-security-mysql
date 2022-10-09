

## Sicurezza
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


## Tutorials & Sitography

### video tutorial

#### best
[autenticazione con JPA](https://www.youtube.com/watch?v=awcCiqBO36E)
34min
[Dan Vega JWT](https://www.youtube.com/watch?v=KYNR5js2cXE)
35min

#### normal
[Dan Vega Spring Security](https://www.youtube.com/watch?v=d7ZmZFbE_qY)
[Multi Ruoli](https://www.youtube.com/watch?v=ErwPP7xLwDY)
42:40


[paginazione JPA](https://www.danvega.dev/blog/2022/05/12/spring-data-jpa-pagination/)

[Buona guida](https://www.baeldung.com/role-and-privilege-for-spring-security-registration)

[da tenere in considerazione](https://stackoverflow.com/questions/44046154/multiple-home-pages-for-different-roles-in-spring-security)

[microservice con Spring Cloud](https://www.youtube.com/watch?v=p485kUNpPvE)
