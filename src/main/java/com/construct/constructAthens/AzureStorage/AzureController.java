package com.construct.constructAthens.AzureStorage;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
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
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class AzureController {
    @Autowired
    private StorageService azureBlobAdapter;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Value("${azure.storage.blob.endpoint}")
    private String blobStorageEndpoint;

    @PostMapping(path="/createImage",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestParam MultipartFile file, @RequestParam UUID employeeId) throws IOException {
        String fileName = azureBlobAdapter.upload(file);
        String imageURL = blobStorageEndpoint + "/" + fileName;
        employeeRepository.updateEmployeeImageURL(employeeId, imageURL);

        return ResponseEntity.ok(imageURL);
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