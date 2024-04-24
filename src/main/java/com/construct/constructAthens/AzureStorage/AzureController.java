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
                                        @RequestParam(required = false) MultipartFile cvFile,
                                        @RequestParam(required = false) MultipartFile signatureFile,
                                        @RequestParam UUID employeeId) throws IOException {
       String imageURL = null;
       String cvURL = null;
       String signatureURL = null;

       if (imageProfile != null) {
           String fileName = azureBlobAdapter.upload(imageProfile);
           imageURL = blobStorageEndpoint + "/" + fileName;
       }

       if (cvFile != null) {
           String cv = azureBlobAdapter.upload(cvFile);
           cvURL = blobStorageEndpoint + "/" + cv;
       }

       if (signatureFile != null) {
           String sign = azureBlobAdapter.upload(signatureFile);
           signatureURL = blobStorageEndpoint + "/" + sign;
       }

       Employee existingEmployee = employeeRepository.findById(employeeId).orElse(null);
       String existingImageURL = existingEmployee != null ? existingEmployee.getImageURL() : null;
       String existingCvURL = existingEmployee != null ? existingEmployee.getCvURL() : null;
       String existingSignatureURL = existingEmployee != null ? existingEmployee.getSignatureURL() : null;

       String finalImageURL = imageURL != null ? imageURL : existingImageURL;
       String finalCvURL = cvURL != null ? cvURL : existingCvURL;
       String finalSignatureURL = signatureURL != null ? signatureURL : existingSignatureURL;

       employeeRepository.updateEmployeeImageURL(employeeId, finalImageURL, finalCvURL, finalSignatureURL);

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