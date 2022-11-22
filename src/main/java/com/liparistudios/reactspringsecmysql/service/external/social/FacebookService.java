package com.liparistudios.reactspringsecmysql.service.external.social;


import org.springframework.stereotype.Service;

/**
 *
 *         https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow
 *
 *         Accesso degli utenti
 *         Se un utente non ha effettuato l'accesso alla tua app o a Facebook, mostra la finestra di dialogo Accedi
 *         per richiedere di effettuare entrambi gli accessi. Se non ha effettuato l'accesso a Facebook, gli verrà richiesto
 *         di farlo prima di accedere alla tua app. Questo verrà rilevato automaticamente, pertanto non è necessario
 *         che tu esegua ulteriori operazioni per abilitare questo comportamento.
 *
 *         Chiamata della finestra di dialogo Accedi e impostazione dell'URL di reindirizzamento
 *         Per mostrare la finestra di dialogo Accedi, l'app deve eseguire un reindirizzamento a un endpoint:
 *
 *             https://www.facebook.com/v5.0/dialog/oauth?client_id={app-id}&redirect_uri={redirect-uri}&state={state-param}
 *
 *         L'endpoint ha i parametri:
 *         client_id:      l'ID della tua app, disponibile nella Dashboard gestione app.
 *         redirect_uri:   l'URL a cui desideri reindirizzare l'utente al momento dell'accesso,
 *                         che acquisisce la risposta della finestra di dialogo Accedi.
 *                         Se lo usi nella visualizzazione web di un'app per computer, configuralo
 *                         su https://www.facebook.com/connect/login_success.html.
 *                         Puoi confermare che è l'URL impostato per la tua app nella Dashboard gestione app.
 *                         In Prodotti nel menu di navigazione a sinistra della Dashboard gestione app,
 *                         clicca su Facebook Login, quindi clicca su Impostazioni.
 *                         Verifica gli URL di reindirizzamento OAuth validi nella sezione Impostazioni OAuth del client.
 *         state:          un valore di tipo stringa creato dalla tua app per mantenere il parametro state tra
 *                         la richiesta e la callback.
 *                         Questo parametro deve essere usato per impedire attacchi Cross-Site Request Forgery e
 *                         verrà passato di nuovo a te, invariato, nel tuo URI di reindirizzamento.
 *
 *
 *         Inoltre, ha i parametri facoltativi:
 *
 *         response_type:  determina se i dati della risposta al reindirizzamento all'app sono parametri o frammenti di URL.
 *                         Consulta la sezione Conferma delle identità per scegliere quale tipo usare nella tua app.
 *                         Può essere uno tra:
 *                             code:           i dati della risposta sono parametri di URL contenenti il parametro code
 *                                             (una stringa crittografata diversa per ogni richiesta di accesso).
 *                                             Si tratta del comportamento predefinito quando il parametro non viene specificato.
 *                                             È utile per la gestione del token da parte del server.
 *                             token:          i dati della risposta sono un frammento di URL contenente un token d'accesso.
 *                                             Le app per computer devono usare questa impostazione per response_type.
 *                                             È utile per la gestione del token da parte del client.
 *                             code%20token:   i dati della risposta sono un frammento di URL contenente un token d'accesso
 *                                             e il parametro code.
 *         granted_scopes: restituisce una lista separata da virgole delle autorizzazioni che l'utente ha concesso
 *                         all'app durante l'accesso. Può essere combinato con altri valori response_type.
 *                         Se combinato con token, i dati della risposta verranno inclusi come frammento di URL,
 *                         in caso contrario come parametro di URL.
 *         scope:          una lista separata da virgole o spazi delle autorizzazioni da richiedere a chi usa l'app.
 *
 *
 *
 *         Gestione delle risposte della finestra di dialogo Accedi
 *         A questo punto del flusso di accesso, l'utente visualizzerà la finestra di dialogo Accedi e potrà scegliere
 *         se concedere o meno all'app l'accesso ai dati.
 *
 *         Scegliendo Ok nella finestra di dialogo Accedi, concederà l'accesso al profilo pubblico, alla lista di amici
 *         e a tutte le autorizzazioni richieste dall'app.
 *
 *         In ogni caso, il browser tornerà all'app, che riceverà dati che indicano se l'utente ha annullato o
 *         ha effettuato l'accesso.
 *         Quando l'app usa il metodo di reindirizzamento descritto sopra, al redirect_uri restituito dalla tua app
 *         vengono aggiunti parametri o frammenti di URL (in base al response_type scelto), che devono essere acquisiti.
 *
 *         Date le diverse combinazioni di linguaggi che è possibile usare nelle app web, la guida non mostra esempi specifici.
 *         Tuttavia, la maggior parte dei linguaggi moderni è in grado di eseguire il parsing dell'URL come segue:
 *
 *         Il linguaggio JavaScript lato server è in grado di acquisire i frammenti di URL (ad esempio, jQuery BBQ),
 *         mentre i parametri di URL possono essere acquisiti tramite codice lato client e lato server
 *         (ad esempio, $_GET in PHP, jQuery.deparam in jQuery BBQ, querystring.parse in Node.js o urlparse in Python).
 *         Microsoft mette a disposizione una guida e un codice di esempio per le app Windows 8 che si connettono
 *         a un "fornitore online" (in questo caso, Facebook).
 *
 *         Quando un utente effettua l'accesso a un'app per computer, Facebook lo reindirizza al parametro redirect_uri
 *         descritto sopra, inserendo un token d'accesso e altri metadati (ad esempio, la scadenza del token) nel frammento di URI:
 *
 *             https://www.facebook.com/connect/login_success.html#access_token=ACCESS_TOKEN...
 *
 *         L'app deve essere in grado di individuare il reindirizzamento e di leggere il token d'accesso dell'URI tramite
 *         i meccanismi offerti dal sistema operativo e dal framework di sviluppo usato. In seguito, consulta direttamente
 *         il passaggio Esame dei token d'accesso.
 *
 *         Accesso annullato
 *         Quando l'utente che sta usando la tua app non accetta la finestra di dialogo Accedi cliccando su Annulla,
 *         viene reindirizzato a:
 *
 *             YOUR_REDIRECT_URI?error_reason=user_denied&error=access_denied&error_description=Permissions+error.
 *
 *         Consulta Gestione delle autorizzazioni mancanti per scoprire di più su quali operazioni deve eseguire
 *         l'app quando l'utente annulla l'accesso.
 *
 *         Conferma delle identità
 *         Il flusso di reindirizzamento prevede che i browser siano reindirizzati dalla finestra di dialogo Accedi
 *         agli URL nella tua app, a cui il traffico può accedere direttamente attraverso determinati frammenti o parametri.
 *         Se l'app li accetta come parametri validi, potrebbe usare questi dati per scopi potenzialmente dannosi.
 *         Pertanto, prima di generare un token d'accesso, l'app deve confermare che l'utente che la sta usando è
 *         lo stesso a cui sono destinati i dati della risposta. Esistono diversi modi per confermare l'identità di un utente,
 *         secondo il parametro response_type ricevuto:
 *
 *         Se ricevi code, scambialo con un token d'accesso tramite un endpoint.
 *         Dal momento che usa la chiave segreta, la chiamata deve essere tra server
 *         (non inserire la chiave segreta in un codice lato client).
 *
 *         Se ricevi token, devi verificarlo.
 *         Effettua una chiamata API a un endpoint di analisi, che indicherà per chi è stato generato il token e da quale app.
 *         Dal momento che la chiamata API richiede l'uso di un token d'accesso per l'app, non effettuarla da un client.
 *         Effettua la chiamata da un server in cui puoi memorizzare la chiave segreta in modo sicuro.
 *
 *         Se ricevi sia code che token, segui entrambi i passaggi.
 *         Tieni presente che puoi generare un parametro state personalizzato e usarlo con la richiesta di accesso come
 *         protezione dagli attacchi CSRF (Cross-Site Request Forgery).
 *
 *         Scambio del codice per un token d'accesso
 *         Per ottenere un token d'accesso, fai una richiesta HTTP GET al seguente endpoint OAuth:
 *
 *             GET https://graph.facebook.com/v5.0/oauth/access_token?client_id={app-id}&redirect_uri={redirect-uri}&client_secret={app-secret}&code={code-parameter}
 *
 *         L'endpoint prevede alcuni parametri necessari:
 *
 *         client_id:      l'ID app.
 *         redirect_uri:   un argomento necessario, che deve corrispondere al parametro request_uri originale, usato per
 *                         avviare l'accesso OAuth.
 *         client_secret:  la chiave segreta che è possibile trovare nella Dashboard gestione app. Non includerla in un codice
 *                         lato client o in binari che è possibile decompilare. È estremamente importante mantenerla segreta
 *                         in quanto nucleo di protezione dell'app e di chi la usa.
 *         code:           il parametro ricevuto dal reindirizzamento della finestra di dialogo Accedi descritto sopra.
 *
 *         Risposta
 *         La risposta che riceverai sarà in formato JSON e, se l'operazione è avvenuta correttamente, sarà:
 *
 *             {
 *               "access_token": {access-token},
 *               "token_type": {type},
 *               "expires_in":  {seconds-til-expiration}
 *             }
 *
 *         In caso contrario, riceverai un messaggio di errore esplicativo.
 *
 *         Esame dei token d'accesso
 *         Sia che l'app usi o meno code o token come response_type dalla finestra di dialogo Accedi, riceverà un token d'accesso.
 *         Puoi eseguire controlli automatici sui token tramite un endpoint API Graph:
 *
 *             GET graph.facebook.com/debug_token?input_token={token-to-inspect}&access_token={app-token-or-admin-token}
 *
 *         L'endpoint accetta i parametri seguenti:
 *         input_token:    il token da esaminare.
 *         access_token:   il token d'accesso dell'app o per uno sviluppatore dell'app.
 *
 *         La risposta alla chiamata API è un array JSON contenente dati sul token esaminato. Ad esempio:
 *
 *             {
 *                 "data": {
 *                     "app_id": 138483919580948,
 *                     "type": "USER",
 *                     "application": "Social Cafe",
 *                     "expires_at": 1352419328,
 *                     "is_valid": true,
 *                     "issued_at": 1347235328,
 *                     "metadata": {
 *                         "sso": "iphone-safari"
 *                     },
 *                     "scopes": [
 *                         "email",
 *                         "publish_actions"
 *                     ],
 *                     "user_id": "1207059"
 *                 }
 *             }
 *
 *         I campi app_id e user_id aiutano l'app a verificare che il token d'accesso sia valido sia per l'utente
 *         che per l'app stessa.
 *         Per una descrizione completa degli altri campi, consulta la guida su come ottenere informazioni sui token d'accesso.
 *
 *         Verifica delle autorizzazioni
 *         Puoi chiamare il segmento /me/permissions per recuperare la lista delle autorizzazioni concesse o meno da un
 *         determinato utente.
 *         L'app può usare questo metodo per controllare quali di quelle richieste non può usare.
 *
 *
 *
 *         Nuova richiesta delle autorizzazioni negate
 *         Facebook Login consente agli utenti di negare la condivisione di alcune autorizzazioni con la tua app.
 *
 *         L'autorizzazione public_profile è sempre necessaria e visualizzata in grigio poiché non è possibile disabilitarla.
 *
 *         Se, in questo esempio, l'utente disabilitasse user_likes ("Mi piace"), controllando /me/permissions per
 *         le autorizzazioni concesse il risultato sarebbe il seguente:
 *
 *             {
 *               "data":
 *                 [
 *                   {
 *                     "permission":"public_profile",
 *                     "status":"granted"
 *                   },
 *                   {
 *                     "permission":"user_likes",
 *                     "status":"declined"
 *                   }
 *                 ]
 *             }
 *
 *         Tieni presente che user_likes non è stata concessa.
 *
 *         Puoi chiedere di nuovo all'utente di concedere le autorizzazioni negate all'app.
 *         Mostra prima una schermata in cui spieghi il motivo per cui dovrebbe concedere una determinata autorizzazione.
 *         Chiamando la finestra di dialogo Accedi come descritto sopra, però, l'autorizzazione non sarà richiesta.
 *
 *         Questo poiché, una volta che l'utente ha negato un'autorizzazione, la finestra di dialogo Accedi non la richiede
 *         a meno che non sia tu a specificarlo.
 *
 *         Puoi farlo aggiungendo il parametro auth_type=rerequest all'URL della finestra di dialogo Accedi:
 *
 *             https://www.facebook.com/v5.0/dialog/oauth?client_id={app-id}&redirect_uri={redirect-uri}&auth_type=rerequestscope=email
 *
 *         In questo modo, richiederà nuovamente l'autorizzazione negata.
 *
 *         Memorizzazione dei token d'accesso e dello stato d'accesso
 *         A questo punto del flusso, l'utente è autenticato e ha effettuato l'accesso.
 *         L'app può effettuare chiamate API per suo conto.
 *         Prima, però, è necessario memorizzare il token d'accesso e lo stato d'accesso.
 *
 *         Memorizzazione dei token d'accesso
 *         Quando l'app ha ricevuto il token d'accesso tramite il passaggio precedente, deve memorizzarlo e renderlo disponibile
 *         per ogni chiamata API. Non esiste un processo specifico, ma in generale, se stai creando un'app per il web è
 *         preferibile aggiungere il token come variabile della sessione per collegarla a un determinato utente,
 *         se invece stai creando un'app nativa per computer o mobile usa il datastore che hai a disposizione.
 *         Inoltre, l'app deve memorizzare il token e lo user_id in un database per poterlo identificare.
 *
 *         Consulta la nota sulle dimensioni nel documento relativo ai token d'accesso.
 *
 *         Monitoraggio dello stato d'accesso
 *         L'app deve memorizzare lo stato d'accesso dell'utente, evitando di effettuare ulteriori chiamate alla finestra
 *         di dialogo Accedi.
 *         Indipendentemente dalla procedura che sceglierai, adatta il controllo dello stato d'accesso.
 *
 *         Disconnessione degli utenti
 *         Puoi disconnettere gli utenti dalla tua app annullando l'indicatore dello stato d'accesso che hai aggiunto,
 *         ad esempio eliminando la sessione che indica se l'utente ha effettuato l'accesso. Inoltre, devi rimuovere
 *         il token d'accesso memorizzato.
 *
 *         Disconnettere gli utenti non è come revocare un'autorizzazione di accesso
 *         (rimuovendo l'autenticazione concessa in precedenza), operazione che è possibile eseguire separatamente.
 *         Pertanto, fai in modo che l'app non reindirizzi gli utenti disconnessi alla finestra di dialogo Accedi.
 *
 *         Rilevamento delle disinstallazioni delle app
 *         Gli utenti possono disinstallare le app tramite Facebook.com senza interagire effettivamente con esse.
 *         Per aiutarti a capire quando questo accade, ti consentiamo di fornire un URL di callback per la rimozione
 *         dell'autorizzazione, che sarà chiamato ogni volta.
 *
 *         Puoi abilitare una callback alla rimozione dell'autorizzazione mediante la Dashboard gestione app.
 *         Accedi alla tua app, quindi scegli Prodotti, Facebook Login e infine Impostazioni.
 *         Qui troverai il campo URL di callback per la rimozione dell'autorizzazione.
 *
 *         Quando un utente rimuove l'autorizzazione all'app, l'URL riceve un HTTP POST contenente una richiesta firmata.
 *         Consulta la nostra guida su come eseguire il parsing di una richiesta firmata per capire come decodificarla
 *         e conoscere l'ID utente che ha attivato la callback.
 *
 *         Risposta alle richieste di eliminazione dei dati degli utenti
 *         Le persone possono richiedere a un'app di eliminare tutte le informazioni su di loro ricevute da Facebook.
 *         Per rispondere a queste richieste, consulta Callback relativa alla richiesta di eliminazione dei dati.
 *
 *
 */
@Service
public class FacebookService /*extends ExternalService*/ {

    /*
    @Value("${facebook.redirect-url}")
    private String redirectUrl;

    @Value("${facebook.secret-key}")
    private String clientSecret;

    @Value("${facebook.client-id}")
    private String clientId;

    @Value("${facebook.api-version}")
    private String apiVersion;

    /**
     * Viene scambiato il codice per ricevere il token di accesso
     *
     * @param code
     * @return
     * /
    public Map<String, Object> getAccessToken(String code ) throws Exception {

        // API url
        String apiUrl = "https://graph.facebook.com/";
        apiUrl += apiVersion;
        apiUrl += "/oauth/access_token";

        // headers
        Map<String, String> headers = new HashMap<String, String>(){{
            put("Content-Type", "application/x-www-form-urlencoded");
        }};

        // payload
        Map<String, Object> payload = new HashMap<String, Object>(){{
            put("client_id",        clientId);
            put("redirect_uri",     redirectUrl);
            put("client_secret",    clientSecret);
            put("code",             code);
        }};

        return this.remoteServerConnect(
            setUserDataConnectionHeaders( headers ),
            HttpMethod.POST,
            apiUrl,
            payload
        );

    }

    public String validateAccessToken() {
        return null;
    }

    public String enableDeniedPermission() {
        return null;
    }


    public Map<String, Object> getUserData(String access_token) throws Exception {

        // fields
        String fields = Arrays.asList(
                new String[]{
                    "id"
                    , "name"
                    , "email"
                    , "picture.width(1024)"
                    , "friends"
                    , "address"
                    , "birthday"
                    , "hometown"
                    , "link"
                    , "gender"
                    , "likes"
                    , "location"
                    , "events"
                    , "photos"
                    , "videos"
                    , "taggable_friends"
                    , "first_name"
                    , "last_name"
                    , "website"
                    , "languages"
                    , "tagged"
                }
            )
                .stream()
                .collect(Collectors.joining(","))
        ;

        // API url
        String apiUrl = "https://graph.facebook.com/me?fields=";
        apiUrl += fields;
        apiUrl += "&access_token=";
        apiUrl += access_token;

        // headers
        Map<String, String> headers = new HashMap<String, String>(){{
            put("Content-Type", "application/x-www-form-urlencoded");
        }};

        return this.remoteServerConnect(
            setUserDataConnectionHeaders( headers ),
            HttpMethod.POST,
            apiUrl,
            new HashMap<>()
        );

    }
    */
}
