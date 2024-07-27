package com.vityazev_egor.debt_clear_flow_server.Controllers.API;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.vityazev_egor.debt_clear_flow_server.Misc.Shared;

@RestController
public class ImagesController {

    private final Logger logger = LoggerFactory.getLogger(ImagesController.class);
    
    @GetMapping("/images/{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable("imageName") String imageName){

        Path imagePath = Paths.get(Shared.imagesDirectory.toString(), imageName);
        if (!Files.exists(imagePath)){
            return ResponseEntity.notFound().build();
        }

        byte[] image = null;

        try{
            Files.readAllBytes(imagePath);
        }
        catch (IOException e){
            logger.error("Error while reading image file: " + imagePath.toString(), e);
            return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok().headers(headers).body(image);
    }
}
