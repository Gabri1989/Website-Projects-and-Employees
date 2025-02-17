package com.construct.constructAthens.security.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.construct.constructAthens.AzureStorage.StorageService;
import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.security.UserInfoRepository;
import com.construct.constructAthens.security.entity.UserInfo;
import com.construct.constructAthens.security.entity.UserInfoDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userDetail = repository.findByUsername(username);
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public ResponseEntity<String> addUser(UserInfo userInfo) {
        try {
            if (repository.existsByUsername(userInfo.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
            }
            userInfo.setPassword(encoder.encode(userInfo.getPassword()));
            UUID userId = UUID.randomUUID();
            userInfo.setId(userId);
            LocalDate dataDemo=LocalDate.now();
            userInfo.setDate_account_created(dataDemo.toString());
            UserInfo savedUser = repository.save(userInfo);
            UUID userid = savedUser.getId();
            String username = savedUser.getUsername();
            Employee employee = new Employee();
            employee.setId(userid);
            employee.setUsername(username);


            employeeRepository.save(employee);
            return ResponseEntity.ok("User Added Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }

    }

    public Optional<UserInfo> getUserById(UUID id) {
        return repository.findById(id);
    }

    public void deleteUserById(UUID id) {
        repository.deleteById(id);
        employeeRepository.deleteById(id);
    }
    public List<UserInfoDto> getAllUsersDTO() {
        List<UserInfo> users = repository.findAll();
        List<Employee> employees = employeeRepository.findAll();

        List<UserInfoDto> dtos = new ArrayList<>();
        for (UserInfo userInfo : users) {
            Employee employee = employees.stream()
                    .filter(emp -> emp.getId().equals(userInfo.getId()))
                    .findFirst()
                    .orElse(null);
            UserInfoDto dto = convertToDto(userInfo, employee);
            dtos.add(dto);
        }

        return dtos;
    }

    private UserInfoDto convertToDto(UserInfo user,Employee employee) {
        UserInfoDto dto = new UserInfoDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles());
        dto.setDate_account_created(user.getDate_account_created());

        if (employee != null) {
            dto.setImageURL(employee.getImageURL());
        }
        return dto;
    }

}
