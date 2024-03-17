package com.construct.constructAthens.AzureStorage;

import com.azure.core.util.BinaryData;
import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public class StorageService {

    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    BlobContainerClient blobContainerClient;
    @Autowired
    private  EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;
        public String upload(MultipartFile multipartFile) throws IOException {
            String uniqueFilename = generateUniqueFilename(multipartFile.getOriginalFilename());
            BlobClient blob = blobContainerClient.getBlobClient(uniqueFilename);
            blob.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);
            return uniqueFilename;
        }

    private String generateUniqueFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();

        String extension = getFileExtension(originalFilename);
        return  uuid+"."+ extension;
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }

    public List<String> listBlobs() {

        PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
        List<String> names = new ArrayList<String>();
        for (BlobItem item : items) {
            names.add(item.getName());
        }
        return names;

    }
    public Boolean deleteBlob(String blobName) {

        BlobClient blob = blobContainerClient.getBlobClient(blobName);
        blob.delete();
        return true;
    }



}
