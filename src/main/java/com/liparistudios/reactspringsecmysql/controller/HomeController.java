package com.liparistudios.trainingreactjpa.home;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin( origins = "*")
@RequestMapping( path = {"/", ""} )
public class HomeController {

    @GetMapping
    public ModelAndView home() {
        Map<String, Object> pageVars = new HashMap<String, Object>() {{
            put("session", "123abc");
        }};
        ModelAndView page = new ModelAndView("index");
        page.addAllObjects(pageVars);
        return page;
    }




    @GetMapping(path = {"{pic}.{ext:[jpngtf]+}"})
    public ResponseEntity<Resource> getRootLevelBytesFiles(
            HttpServletRequest request,
            @PathVariable("pic") String picName,
            @PathVariable("ext") String picExt
    ) throws IOException {

        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react/build/"+ picName +"."+ picExt);
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
        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react/build/"+ fileName +"."+ ext);
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
        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react/build/static/media/"+ fileName + ".svg");
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
        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react/build/static/js/"+ fileName);
        File requestedFile = null;
        try {
            requestedFile = new File(requestedFileUrl.toURI());
        } catch (URISyntaxException e) {
            requestedFile = new File(requestedFileUrl.getPath());
        }

        String requestedFileContent = Files.readString(Path.of(requestedFile.getAbsolutePath()));
        return new ResponseEntity<String>(requestedFileContent, null, HttpStatus.OK);
    }

    @GetMapping(path = "/static/css/{file}")
    public ResponseEntity<String> getRootLevelCssStyleFiles(@PathVariable( "file" ) String fileName) throws IOException {

        URL requestedFileUrl = this.getClass().getClassLoader().getResource("react/build/static/css/"+ fileName);
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
