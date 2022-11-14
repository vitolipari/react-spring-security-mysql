package com.liparistudios.reactspringsecmysql.controller;

import com.liparistudios.reactspringsecmysql.businessLogic.SessionHandlerForControllers;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping({"/", "/short-api"})
public class ShortUrlController {


    /**
     * /s?v={ version }&s={ session }&p={ pin }
	 * equivalente di
	 * /api/v{ version }/access/{ sessionCode }?auth={ pin }
     */
	@GetMapping("/s")
	public ModelAndView accessByVirginSessionShortUrl(
		@RequestParam(name = "v", required = false) String version,
		@RequestParam(name = "s", required = true) String code,
		@RequestParam(name = "p", required = true) String pin,
		HttpServletRequest request,
		HttpServletResponse response
	) {

		Map<String, Object> pageVars = null;
		if( version != null && (version == "1" || version == "v1") ) {
			SessionHandlerForControllers sessionHandler = new SessionHandlerForControllers();
			pageVars = sessionHandler.accessSession( request, response, code, pin );
		}
		ModelAndView page = new ModelAndView("mobile/build/index");
		page.addAllObjects(pageVars);
		return page;

	}


    /**
     * /s/{ version }/{ session }/{ pin }
	 * equivalente a
	 * /api/v{ version }/access/{ sessionCode }?auth={ pin }
	 *
     */
	@GetMapping("/s/{version}/{code}/{pin}")
	public ModelAndView accessByVirginSessionShortUrlPath(
		@PathVariable(name = "version", required = true, value = "v1") String version,
		@PathVariable(name = "code", required = true, value = "c1051") String code,
		@PathVariable(name = "pin", required = true, value = "1051") String pin,
		HttpServletRequest request,
		HttpServletResponse response
	) {

		Map<String, Object> pageVars = null;
		if( version != null && (version == "1" || version == "v1") ) {
			SessionHandlerForControllers sessionHandler = new SessionHandlerForControllers();
			pageVars = sessionHandler.accessSession( request, response, code, pin );
		}
		ModelAndView page = new ModelAndView("mobile/build/index");
		page.addAllObjects(pageVars);
		return page;

	}


}
