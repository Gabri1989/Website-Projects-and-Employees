package com.construct.constructAthens.Employees;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWithImageRequest {
    private Employee employee;
    private MultipartFile imageFile;
}
