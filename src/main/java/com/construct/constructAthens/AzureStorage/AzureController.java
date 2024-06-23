package com.construct.constructAthens.AzureStorage;

import com.azure.core.management.Resource;
import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.*;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class AzureController {
    @Autowired
    private StorageService azureBlobAdapter;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Value("${azure.storage.blob.endpoint}")
    private String blobStorageEndpoint;


   @PostMapping(path="/uploadFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ResponseEntity<String> upload(@RequestParam(required = false) MultipartFile imageProfile,
                                        @RequestParam UUID employeeId) throws IOException {
       String imageURL = null;
       if (imageProfile != null) {
           String fileName = azureBlobAdapter.upload(imageProfile);
           imageURL = blobStorageEndpoint + "/" + fileName;
       }
       Employee existingEmployee = employeeRepository.findById(employeeId).orElse(null);
       String existingImageURL = existingEmployee != null ? existingEmployee.getImageURL() : null;
       String finalImageURL = imageURL != null ? imageURL : existingImageURL;
       employeeRepository.updateEmployeeImageURL(employeeId, finalImageURL);
       return ResponseEntity.ok(finalImageURL);
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


}