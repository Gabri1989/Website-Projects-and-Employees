package com.construct.constructAthens.security.services;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.security.UserInfoRepository;
import com.construct.constructAthens.security.entity.UserInfo;
import com.construct.constructAthens.security.entity.UserInfoDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
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

    /*public UserInfoService(UserInfoRepository repository) {
        this.employeeRepository = employeeRepository;
        this.repository=repository;
        this.encoder=encoder;
    }*/

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userDetail = repository.findByUsername(username);
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        UUID userId = UUID.randomUUID();
        userInfo.setId(userId);
        UserInfo savedUser = repository.save(userInfo);
        UUID userid = savedUser.getId();
        String username = savedUser.getUsername();
        Employee employee = new Employee();
        employee.setId(userid);
        employee.setUsername(username);
        employeeRepository.save(employee);
        return "User Added Successfully";
    }
    public Optional<UserInfo> getUserById(UUID id) {
        return repository.findById(id);
    }

    public List<UserInfo> getAllUsers() {
        return repository.findAll();
    }

    public void deleteUserById(UUID id) {
        repository.deleteById(id);
    }
    public List<UserInfoDto> getAllUsersDTO() {
        List<UserInfo> users = repository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserInfoDto convertToDto(UserInfo user) {
        UserInfoDto dto = new UserInfoDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoles(user.getRoles());
        return dto;
    }

}
