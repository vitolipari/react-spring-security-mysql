package com.liparistudios.reactspringsecmysql.businessLogic;

import com.liparistudios.reactspringsecmysql.model.Platform;
import com.liparistudios.reactspringsecmysql.model.Session;
import com.liparistudios.reactspringsecmysql.service.PlatformService;
import com.liparistudios.reactspringsecmysql.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionHandlerForControllers {

	@Autowired
	SessionService sessionService;

	@Autowired
	PlatformService platformService;

	public Map<String, Object> accessSession(
		HttpServletRequest request,
		HttpServletResponse response,
		String code,
		String pin,
		Long platformID
	) {

		Map<String, Object> pageVars = new HashMap<String, Object>();
		try {

			pageVars = new HashMap<String, Object>(){{
				put("session",
					new HashMap<String, Object>(){{
						put("code", code);
					}}
				);
			}};
			LocalDateTime now = LocalDateTime.now();

			// controllo sessione
			System.out.println("cerco la sessione "+ code);
			System.out.println("session service");
			System.out.println(sessionService);

//			if( sessionService == null ) sessionService = new SessionService();

			Session session = sessionService.getSessionByCode( code );
			System.out.println("sessione presa");
			System.out.println( session );
			((Map<String, Object>) pageVars.get("session")).put("id", session.getId());


			// Platform platform = platformService.getPlatformBySessionCode( code );
			System.out.println("ricavo la pittaforma");
			Platform platform = platformService.getPlatformBySessionId( session.getId() );
			System.out.println( platform );
			System.out.println( platformID );

			pageVars.put("platform", platform);

			// controllo sessione che può essere abilitata
			if( session.isLive() ) {
				((Map<String, Object>) pageVars.get("session")).put("live",
					new HashMap<String, Object>(){{
						put("since", session.getAccess());
					}}
				);
			}
			else {

				// controllo sessione scaduta
				if( session.isExpired() ) {
					((Map<String, Object>) pageVars.get("session")).put("expired", session.isExpired());
				}
				else {

					// controllo sessione chiusa
					if( session.isClosed() ) {
						((Map<String, Object>) pageVars.get("session")).put("closed", session.isClosed());
					}
					else {

						// sessione OK
						((Map<String, Object>) pageVars.get("session")).put("access", now);

						// controllo timeToEnable
						if(now.isBefore( session.getOpen().plus(90, ChronoUnit.SECONDS) )) {
							// sessione scansionabile
							session.setAccess( now );

						}
						else {
							// sessione scaduta
							session.setClosed( now );
							((Map<String, Object>) pageVars.get("session")).put("closed", now);

						}


					}

				}


			}



			sessionService.save( session );


		}
		catch (Exception e) {
			// codice sessione non trovato

			e.printStackTrace();

			pageVars.put("error",
				new HashMap<String, Object>(){{
					put("code", 600);
					put("title", "Codice sessione non trovato");
					put("message", e.getMessage());
					put("stackTrace", e.getStackTrace());
				}}
			);

		}
		finally {

			return pageVars;
		}

	}

}
