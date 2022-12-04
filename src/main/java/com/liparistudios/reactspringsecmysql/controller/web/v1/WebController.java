package com.liparistudios.reactspringsecmysql.controller.web.v1;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


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
@RequestMapping( path = {"/", "/public"} )
public class WebController {


    private String[] stepsWeb = {
        "free",
        "admin",
        "mobile",
        "mobile-auth"
    };


    private File getRequestedStaticFile( String filePath ) throws IOException {

//        System.out.println("getRequestedStaticFile");
//        System.out.println( filePath );

        Boolean fileFound = false;
        Integer securityStep = 0;

        File requestedFile = null;
        while( !fileFound && securityStep < stepsWeb.length ) {

            URL requestedFileUrl = this.getClass().getClassLoader().getResource("react_js/"+ stepsWeb[ securityStep ] +"/build/"+ filePath);

            try {
                requestedFile = new File(requestedFileUrl.toURI());
                fileFound = true;
            }
            catch (Exception e) {
                try {
                    requestedFile = new File(requestedFileUrl.getPath());
                    fileFound = true;
                }
                catch (Exception ex) {
                    fileFound = false;
                }
            }

            securityStep++;
        }

//        System.out.println("path del file richiesto");
//        System.out.println( filePath );
//        if (requestedFile != null) System.out.println(requestedFile.toPath());
//        else System.out.println("file non trovato");


        return requestedFile;

    }

    private ByteArrayResource getRequestedStaticFileBytes( String filePath ) throws IOException {
        File requestedFile = getRequestedStaticFile( filePath );
        return new ByteArrayResource(Files.readAllBytes(requestedFile.toPath()));
    }

    private String getRequestedStaticFileContent( String filePath ) throws IOException {
        File requestedFile = getRequestedStaticFile( filePath );
        return Files.readString(Path.of(requestedFile.getAbsolutePath()));
    }


    @GetMapping
    public ModelAndView publicHome( HttpServletRequest request ) {

        System.out.println( "ruolo admin?" );
        System.out.println( request.isUserInRole("ADMIN") );

        Principal principal = request.getUserPrincipal();
        System.out.println("Principal");
        System.out.println( principal );
//        System.out.println( principal.getName() );
//        System.out.println( principal.getName().toString() );

        ModelAndView page = null;
        if( principal == null ) {
            Map<String, Object> pageVars = new HashMap<String, Object>() {{
                put("session", "123abc");
                put("certificate", "123abc");
                put("data", "123abc");
            }};
            page = new ModelAndView("free/build/index");
            page.addAllObjects(pageVars);

        }
        else {
            Map<String, Object> pageVars = new HashMap<String, Object>() {{
                put("session", "123abc");
                put("certificate", "123abc");
                put("data", "123abc");
            }};
            page = new ModelAndView("customer/build/index");
            page.addAllObjects(pageVars);
        }

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

//        ByteArrayResource inputStream = getRequestedStaticFileBytes( picName +"."+ picExt );
        File requestedFile = getRequestedStaticFile( picName +"."+ picExt );
        ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(requestedFile.toPath()));

        return
            ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .contentType(MediaType.parseMediaType(Files.probeContentType( requestedFile.toPath() )))
                .body(inputStream)
        ;

    }


    @GetMapping(path = "/{file}.{ext:[txjson]+}")
    public ResponseEntity<String> getRootLevelFiles(
            @PathVariable( "file" ) String fileName,
            @PathVariable( "ext" ) String ext
    ) throws IOException {
        File requestedFile = getRequestedStaticFile( fileName +"."+ ext);
        String requestedFileContent = Files.readString(Path.of(requestedFile.getAbsolutePath()));

        System.out.println("path del file che da problemi col mime type");
        System.out.println( requestedFile.toPath() );

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
        File requestedFile = getRequestedStaticFile( "/static/media/"+ fileName + ".svg");
        String requestedFileContent = Files.readString(Path.of(requestedFile.getAbsolutePath()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/svg+xml"));
        return new ResponseEntity<String>(requestedFileContent, headers, HttpStatus.OK);
    }

    @GetMapping(path = "/static/js/{file}")
    public ResponseEntity<String> getRootLevelJavascriptFiles(@PathVariable( "file" ) String fileName, HttpServletRequest request) throws IOException {
        String requestedFileContent = getRequestedStaticFileContent( "/static/js/"+ fileName );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/javascript"));
        return new ResponseEntity<String>(requestedFileContent, headers, HttpStatus.OK);
    }

    @GetMapping(path = "/static/css/{file}")
    public ResponseEntity<String> getRootLevelCssStyleFiles(@PathVariable( "file" ) String fileName, HttpServletRequest request) throws IOException {
        String requestedFileContent = getRequestedStaticFileContent( "/static/css/"+ fileName );
        return new ResponseEntity<String>(requestedFileContent, null, HttpStatus.OK);
    }

}
