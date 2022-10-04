package com.liparistudios.reactspringsecmysql.controller.web.v1;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;



@RestController
@CrossOrigin( origins = "*")
@RequestMapping( path = {"/", "", "/public"} )
public class WebController {

    @GetMapping
    public ModelAndView publicHome( HttpServletRequest request ) {

        System.out.println( "ruolo admin?" );
        System.out.println( request.isUserInRole("ADMIN") );

        Principal principal = request.getUserPrincipal();
        System.out.println("Principal");
        System.out.println( principal );
//        System.out.println( principal.getName() );
//        System.out.println( principal.getName().toString() );


        Map<String, Object> pageVars = new HashMap<String, Object>() {{
            put("session", "123abc");
            put("certificate", "123abc");
            put("data", "123abc");
        }};
        ModelAndView page = new ModelAndView("free/build/index");
        page.addAllObjects(pageVars);
        return page;
    }


    @GetMapping( path = {"/sign", "/sign-in", "/sign-up"} )
    public ModelAndView adminSignPage( HttpServletRequest request ) {

        Map<String, Object> pageVars = new HashMap<String, Object>(){{
            put("session", "123abc");
        }};

        ModelAndView page = new ModelAndView("free/build/index");
        page.addAllObjects(pageVars);
        return page;
    }


    @GetMapping(path = {"{pic}.{ext:[jpngtfico]+}"})
    public ResponseEntity<Resource> getRootLevelBytesFiles(
            HttpServletRequest request,
            @PathVariable("pic") String picName,
            @PathVariable("ext") String picExt
    ) throws IOException {

        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react_js/free/build/"+ picName +"."+ picExt);
        File requestedFile = null;
        try {
            requestedFile = new File(requestedFileUrl.toURI());
        } catch (URISyntaxException e) {
            requestedFile = new File(requestedFileUrl.getPath());
        }

        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(requestedFile.toPath()));
        return
                ResponseEntity
                        .status(HttpStatus.OK)
                        .contentLength(inputStream.contentLength())
                        .contentType(MediaType.parseMediaType(Files.probeContentType(requestedFile.toPath())))
                        .body(inputStream)
                ;

    }


    @GetMapping(path = "/{file}.{ext:[txjson]+}")
    public ResponseEntity<String> getRootLevelFiles(
            @PathVariable( "file" ) String fileName,
            @PathVariable( "ext" ) String ext
    ) throws IOException {
        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react_js/free/build/"+ fileName +"."+ ext);
        File requestedFile = null;
        try {
            requestedFile = new File(requestedFileUrl.toURI());
        } catch (URISyntaxException e) {
            requestedFile = new File(requestedFileUrl.getPath());
        }

        String requestedFileContent = Files.readString(Path.of(requestedFile.getAbsolutePath()));

        return
                ResponseEntity
                        .status(HttpStatus.OK)
                        //.contentLength(inputStream.contentLength())
                        .contentType(MediaType.parseMediaType(Files.probeContentType(requestedFile.toPath())))
                        .body(requestedFileContent)
                ;


    }

    @GetMapping( value = "/static/media/{file}.{ext:[svg]+}", produces = MediaType.IMAGE_PNG_VALUE )
    public ResponseEntity<String> getRootLevelSvgFiles(
            @PathVariable( "file" ) String fileName,
            @PathVariable( "ext" ) String ext
    ) throws IOException {
        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react_js/free/build/static/media/"+ fileName + ".svg");
        File requestedFile = null;
        try {
            requestedFile = new File(requestedFileUrl.toURI());
        } catch (URISyntaxException e) {
            requestedFile = new File(requestedFileUrl.getPath());
        }

        String requestedFileContent = Files.readString(Path.of(requestedFile.getAbsolutePath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/svg+xml"));
        return new ResponseEntity<String>(requestedFileContent, headers, HttpStatus.OK);
    }

    @GetMapping(path = "/static/js/{file}")
    public ResponseEntity<String> getRootLevelJavascriptFiles(@PathVariable( "file" ) String fileName) throws IOException {

        System.out.println("richiesta file JS");
        System.out.println( fileName );

        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react_js/free/build/static/js/"+ fileName);
        File requestedFile = null;
        try {
            requestedFile = new File(requestedFileUrl.toURI());
        } catch (URISyntaxException e) {
            requestedFile = new File(requestedFileUrl.getPath());
        }


        System.out.println( requestedFileUrl );
        System.out.println( requestedFileUrl.toString() );

        String requestedFileContent = Files.readString(Path.of(requestedFile.getAbsolutePath()));
        return new ResponseEntity<String>(requestedFileContent, null, HttpStatus.OK);
    }

    @GetMapping(path = "/static/css/{file}")
    public ResponseEntity<String> getRootLevelCssStyleFiles(@PathVariable( "file" ) String fileName) throws IOException {

        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react_js/free/build/static/css/"+ fileName);
        File requestedFile = null;
        try {
            requestedFile = new File(requestedFileUrl.toURI());
        } catch (URISyntaxException e) {
            requestedFile = new File(requestedFileUrl.getPath());
        }

        String requestedFileContent = Files.readString(Path.of(requestedFile.getAbsolutePath()));
        return new ResponseEntity<String>(requestedFileContent, null, HttpStatus.OK);
    }

}
