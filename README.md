seguendo qesta guida
https://www.youtube.com/watch?v=KYNR5js2cXE

sono giunto a 26:00, manca il POM da sistemare


a seguire
https://www.danvega.dev/blog/2022/05/12/spring-data-jpa-pagination/


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

