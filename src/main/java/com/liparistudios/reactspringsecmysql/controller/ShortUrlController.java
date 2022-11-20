package com.liparistudios.reactspringsecmysql.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liparistudios.reactspringsecmysql.businessLogic.SessionHandlerForControllers;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/", "/short-api"})
public class ShortUrlController {


    /**
     * /s?v={ version }&s={ session }&p={ pin }&pl={ platformID }
	 * equivalente di
	 * /api/v{ version }/session/access/{ sessionCode }?auth={ pin }&platform={ platformID }
	 *
	 * Esempio
	 * /s?v=1&s=a87043e26113f240d3b3ee354b9d920e0b0318c5e97b67bba3d03aaa71fd&p=0123456789012345&pl=6
     */
	@GetMapping("/s")
	public ModelAndView accessByVirginSessionShortUrl(
		@RequestParam(name = "v", required = false) String version,
		@RequestParam(name = "s", required = true) String code,
		@RequestParam(name = "p", required = true) String pin,
		@RequestParam(name = "pl", required = true) Long platformID,
		HttpServletRequest request,
		HttpServletResponse response
	) {

		// extract Platform DATA
//		String rawPlatformDATA = Base64.getEncoder().encodeToString(maskedPlatformData.getBytes());
//		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> pageVars = null;

		try {

//			Map<String, Object> platformDATA = mapper.readValue(rawPlatformDATA, Map.class);
			if( version != null && (version == "1" || version == "v1") ) {
				SessionHandlerForControllers sessionHandler = new SessionHandlerForControllers();
				pageVars = sessionHandler.accessSession( request, response, code, pin, platformID );
			}

		} catch (Exception e) {
			e.printStackTrace();
			pageVars.put("error", new HashMap<String, Object>(){{
				put("trace", e.getStackTrace());
				put("message", "errore 15");
				put("msg", e.getMessage());
			}});
		}




		// ---------------------------------------------


		System.out.println("Controllo dati per la pagina");
		System.out.println(pageVars);

		ModelAndView page = new ModelAndView("mobile/build/index");
		page.addAllObjects(pageVars);
		return page;

	}


    /**
     * /s/{ version }/{ session }/{ pin }/{ platformID }
	 * equivalente a
	 * /api/v{ version }/session/access/{ sessionCode }?auth={ pin }&platform= { platformID }
	 *
     */
	@GetMapping("/s/{version}/{code}/{pin}/{platformID}")
	public ModelAndView accessByVirginSessionShortUrlPath(
		@PathVariable(name = "version", required = true, value = "v1") String version,
		@PathVariable(name = "code", required = true, value = "c1051") String code,
		@PathVariable(name = "pin", required = true, value = "1051") String pin,
		@PathVariable(name = "platformID", required = true, value = "6") Long platformID,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		Map<String, Object> pageVars = null;
		try {
//			String rawPlatformDATA = Base64.getEncoder().encodeToString(maskedPlatformData.getBytes());
//			ObjectMapper mapper = new ObjectMapper();
//			Map<String, Object> platformDATA = mapper.readValue(rawPlatformDATA, Map.class);
			if( version != null && (version == "1" || version == "v1") ) {
				SessionHandlerForControllers sessionHandler = new SessionHandlerForControllers();
				pageVars = sessionHandler.accessSession( request, response, code, pin, platformID );
			}
		} catch (Exception e) {

			pageVars.put("error", new HashMap<String, Object>(){{
				put("trace", e.getStackTrace());
				put("message", "errore JsonProcessingException");
				put("msg", e.getMessage());
			}});
		}

		ModelAndView page = new ModelAndView("mobile/build/index");
		page.addAllObjects(pageVars);
		return page;

	}


}
