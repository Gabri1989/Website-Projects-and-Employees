package com.construct.constructAthens.AzureStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.net.URISyntaxException;
@RestController
@RequestMapping("/files")
public class AzureController {

    @Autowired
    private StorageService azureBlobAdapter;

    @PostMapping("/createImage")
    public ResponseEntity<String> upload
            (@RequestParam MultipartFile file)
            throws IOException {

        String fileName = azureBlobAdapter.upload(file);
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/getImages")
    public ResponseEntity<List<String>> getAllBlobs() {

        List<String> items = azureBlobAdapter.listBlobs();
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/deleteImage")
    public ResponseEntity<Boolean> delete
            (@RequestParam String fileName) {

        azureBlobAdapter.deleteBlob(fileName);
        return ResponseEntity.ok().build();
    }

    /*@GetMapping("/download")
    public ResponseEntity<Resource> getFile
            (@RequestParam String fileName)
            throws URISyntaxException {

        ByteArrayResource resource =
                new ByteArrayResource(azureBlobAdapter
                        .getFile(fileName));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\""
                        + fileName + "\"");

        return ResponseEntity.ok().contentType(MediaType
                        .APPLICATION_OCTET_STREAM)
                .headers(headers).body(resource);
    }*/
}